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
import com.google.sps.Objects.Comment;
import com.google.appengine.api.datastore.EntityNotFoundException;

@WebServlet("/post-comment")

public class CommentsServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String userId = "";
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      userId = userService.getCurrentUser().getUserId();
    }
   
    // Get post id and comment text
    Long postId = Long.parseLong(request.getParameter("id"));
    String commentText = request.getParameter("comment-text");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity postEntity = this.getPostFromId(response, postId, datastore);
    ArrayList<EmbeddedEntity> allComments = (ArrayList<EmbeddedEntity>) postEntity.getProperty("comments");

    // Create comment entity and add to comment arraylist for post
    if (allComments == null) {
      ArrayList<EmbeddedEntity> comments = new ArrayList<>();
      comments.add(Comment.toEntity(commentText, userId));
      postEntity.setProperty("comments", comments);
    } else {
      allComments.add(Comment.toEntity(commentText, userId));
      postEntity.setProperty("comments", allComments);
    }
    datastore.put(postEntity);
  }

  private Entity getPostFromId(
    HttpServletResponse response, long postId, DatastoreService datastore)  throws IOException {
      try {
        return datastore.get(KeyFactory.createKey("Post", postId));
      } catch (EntityNotFoundException e) {
        return null;
      }
  }
}