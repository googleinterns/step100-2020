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
import java.io.PrintWriter;
import com.google.sps.Objects.Post;
import com.google.appengine.api.datastore.EntityNotFoundException;

@WebServlet("/post-comment")

public class CommentsServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
   
    // Get post id and comment text
    Long postId = Long.parseLong(request.getParameter("id"));
    String commentText = request.getParameter("comment-text");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity postEntity = this.getPostFromId(response, postId, datastore);
    ArrayList<EmbeddedEntity> allComments = (ArrayList<EmbeddedEntity>) postEntity.getProperty("comments");

    // Create comment entity and add to comment arraylist for post
    if (allComments == null) {
      ArrayList<EmbeddedEntity> comments = new ArrayList<>();
      comments.add(createCommentEntity(commentText, "user"));
      postEntity.setProperty("comments", comments);
    } else {
      allComments.add(createCommentEntity(commentText, "user"));
      postEntity.setProperty("comments", allComments);
    }
    datastore.put(postEntity);
  }

  private EmbeddedEntity createCommentEntity(String commentText, String user) {
    EmbeddedEntity commentEntity = new EmbeddedEntity();
    commentEntity.setProperty("timestamp", System.currentTimeMillis());
    commentEntity.setProperty("commentText", commentText);
    commentEntity.setProperty("userId", user);
    return commentEntity;
  }

  private Entity getPostFromId(
    HttpServletResponse response, long postId, DatastoreService datastore)throws IOException {
      try {
        return datastore.get(KeyFactory.createKey("Post", postId));
      } catch (EntityNotFoundException e) {
        return null;
      }
  }
}