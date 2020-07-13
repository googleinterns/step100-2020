package com.google.sps.servlets;

import com.google.sps.Objects.Badge;
import com.google.sps.Objects.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import error.ErrorHandler;

@WebServlet("/editProfile")
public class EditProfileServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String first = request.getParameter("first");
    String last = request.getParameter("last");
    String email = request.getParameter("email");
    String phone = request.getParameter("phone");
    ArrayList<String> interests = getInterests(request);

    updateProfile(first, last, email, phone, interests, response);
  }

  /**
   * Gets the list of interests entered by user.
   */
  private ArrayList<String> getInterests(HttpServletRequest request) {
    // Get input from the form.
    String interests = request.getParameter("interests");
    // Trim any whitespace immediately following a comma.
    interests = interests.replaceAll("(\\s*,\\s*)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", ",");

    // Convert the input to an array list.
    String[] interestsArray = interests.split(",");
    ArrayList<String> interestsList = new ArrayList<String>();
    interestsList.addAll(Arrays.asList(interestsArray));

    return interestsList;
  }

  /** 
   * Updates a user's profile information.
   */
  private void updateProfile(String first, String last, String email, String phone, 
      ArrayList<String> interests, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity userEntity = getExistingUser(response, datastore);
    User user = getUpdatedUser(userEntity, first, last, email, phone, interests, response);
    datastore.put(user.toEntity());
  }

  /** 
   * Retrieves existing user entity from datastore.
   */
  private Entity getExistingUser(HttpServletResponse response, DatastoreService datastore) 
      throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    String userId = userService.getCurrentUser().getUserId();
    // not error checking because this class will extend AuthenticatedServlet 
    // (will implement once Lucy's PR with the class is merged to master)
    Key entityKey = KeyFactory.createKey("User", userId);
    Entity userEntity;
    try {
      userEntity = datastore.get(entityKey);
    } catch (EntityNotFoundException e) {
      ErrorHandler.sendError(response, "User not found.");
      userEntity = null;
    }
    return userEntity;
  }

  /**
   * Creates updated user object to return.
   */
  private User getUpdatedUser(Entity entity, String first, String last, String email, String phone, 
      ArrayList<String> interests, HttpServletResponse response) throws IOException {
    User user;
    try {
      user = User.fromEntity(entity);
    } catch (EntityNotFoundException e) {
      ErrorHandler.sendError(response, "User entity error.");
      return null;
    }
    user.setFirstName(first);
    user.setLastName(last);
    user.setEmail(email);
    user.setPhoneNumber(phone);
    user.setInterests(interests);
    return user;
  }
}
