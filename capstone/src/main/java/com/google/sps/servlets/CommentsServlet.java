package com.google.sps.servlets;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.util.Map;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import com.google.sps.Objects.User;
import com.google.sps.Objects.Post;
import com.google.sps.Objects.Comment;

@WebServlet("/post-comment")

public class CommentsServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Long postId= Long.parseLong(request.getParameter("id"));
    String commentText = request.getParameter("comment-text");
  
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity postEntity = this.getPostFromId(response, postId, datastore);
    List<EmbeddedEntity> allComments = postEntity.get("comments");

    EmbeddedEntity commentEntity = new EmbeddedEntity();
    commentEntity.setProperty("timestamp", System.currentTimeMillis());
    commentEntity.setProperty("commentText", commentText);
    commentEntity.setProperty("userId", "user");

    // Update datastore
    postEntity.setProperty("comments", commentsList);
    datastore.put(postEntity);
  }

  private Entity getPostFromId(HttpServletResponse response, long postId,
      DatastoreService datastore) throws IOException {
    try {
      return datastore.get(KeyFactory.createKey("Post", postId));
    } catch (EntityNotFoundException e) {
      this.sendError(response, "Cannot get entity from datastore");
      return null;
    }
  }

}