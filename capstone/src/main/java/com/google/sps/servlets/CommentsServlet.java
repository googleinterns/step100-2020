package com.google.sps.servlets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.PrintWriter;
import com.google.sps.Objects.Post;
import com.google.sps.error.ErrorHandler;
import com.google.sps.Objects.Comment;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.sps.servlets.ServletHelper;

@WebServlet("/post-comment")

public class CommentsServlet extends AuthenticatedServlet  {
  
  private ErrorHandler errorHandler = new ErrorHandler();

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Get post id, comment text, and userProfilePic
    Long postId = Long.parseLong(request.getParameter("id"));
    String commentText = request.getParameter("comment-text");
    Entity userEntity = 
        ServletHelper.getUserFromId(response, userId, datastore);
    String userProfilePic = (String) userEntity.getProperty("profilePic");

    Entity postEntity = ServletHelper.getEntityFromId(response, postId, datastore, "Post");
    if (postEntity == null) return;
    ArrayList<EmbeddedEntity> allComments = (ArrayList<EmbeddedEntity>) postEntity.getProperty("comments");
  
    // Create comment entity and add to comment arraylist for post
    Comment submittedComment = new Comment(System.currentTimeMillis(), commentText, userId, userProfilePic);
    if (allComments == null) {
      ArrayList<EmbeddedEntity> comments = new ArrayList<>();
      comments.add(submittedComment.toEntity());
      postEntity.setProperty("comments", comments);
    } else {
      allComments.add(submittedComment.toEntity());
      postEntity.setProperty("comments", allComments);
    }
    datastore.put(postEntity);
  }

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {}
}