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

import com.google.sps.error.ErrorHandler;
import com.google.sps.servlets.ServletHelper;

@WebServlet("/update-likes")
public class UpdateLikesServlet extends AuthenticatedServlet {

  private ErrorHandler errorHandler = new ErrorHandler();

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get post id and action of user (liked or unliked)
    Long postId = Long.parseLong(request.getParameter("id"));
    boolean isLiked = Boolean.parseBoolean(request.getParameter("liked"));
   
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity post = ServletHelper.getEntityFromId(response, postId, datastore, "Post");
    if (post == null || userId.equals("")) return;
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
    if (isLiked) {
      if (!likes.contains(userId)) likes.add(userId);
    } else {
      likes.remove(userId);
    }
  }

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {}
}
