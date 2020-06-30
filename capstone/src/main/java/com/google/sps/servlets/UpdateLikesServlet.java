package com.google.sps.servlets;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import error.ErrorHandler;

@WebServlet("/update-likes")
public class UpdateLikesServlet extends HttpServlet {

  private ErrorHandler errorHandler = new ErrorHandler();

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get post id and action of user (liked or unliked)
    Long postId = Long.parseLong(request.getParameter("id"));
    boolean isLiked = Boolean.parseBoolean(request.getParameter("liked"));
   
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    String userId = this.getUserId(response);
    Entity post = this.getPostFromId(response, postId, datastore);
    ArrayList<String> likes = (ArrayList<String>) post.getProperty("likes");
    if (likes == null) {
      likes = new ArrayList<>();
    }
    this.getUpdatedVotes(isLiked, userId, likes);

    // Update datastore
    post.setProperty("likes", likes);
    datastore.put(post);
  }

  private void getUpdatedVotes(boolean isLiked, String userId, ArrayList<String> likes) {
    if(isLiked) {
      if(!likes.contains(userId)) likes.add(userId);
    } else {
      likes.remove(userId);
    }
  }

  private String getUserId(HttpServletResponse response) throws IOException {
  UserService userService = UserServiceFactory.getUserService();
    if(userService.isUserLoggedIn()) {
      return userService.getCurrentUser().getUserId();
    }
    errorHandler.sendError(response, "User is not logged in.");
    return "";
  }

  private Entity getPostFromId(HttpServletResponse response, long postId, DatastoreService datastore) throws IOException {
    try {
      return datastore.get(KeyFactory.createKey("Post", postId));
    } catch (EntityNotFoundException e) {
      errorHandler.sendError(response, "Post does not exist.");
      return null;
    }
  }
}
