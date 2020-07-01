// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.sps.Objects.User;
import com.google.sps.Objects.Group;
import com.google.sps.Objects.Badge;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
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
import java.util.LinkedHashSet;
import com.google.gson.Gson;
import error.ErrorHandler;

/**
 * Servlet to handle returning User data from the Datastore.
 */
@WebServlet("/user")
public class UserDataServlet extends HttpServlet {

  private ErrorHandler errorHandler = new ErrorHandler();

  /**
   * Gets User data from the Datastore and returns it.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    User currentUser = null;

    if (userService.isUserLoggedIn()) {
      String userId = userService.getCurrentUser().getUserId();
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Key entityKey = KeyFactory.createKey("User", userId);
      try {
        currentUser = currentUser.fromEntity(datastore.get(entityKey));
      } catch (EntityNotFoundException e) {
        errorHandler.sendError(response, "User not found.");
      }
    } else {
      errorHandler.sendError(response, "User not logged in.");
    }

    // Convert to json
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(currentUser));
  }

  /**
   * Creates and returns a User object given a user Entity.
   */
  public User getUserObject(Entity entity) {
    String userId = (String) entity.getProperty("userId");
    String firstName = (String) entity.getProperty("firstName");
    String lastName = (String) entity.getProperty("lastName");
    String email = (String) entity.getProperty("email");
    String phoneNumber = (String) entity.getProperty("phoneNumber");
    String profilePic = ""; // todo: add profilePic url to datastore/figure out Blobstore
    ArrayList<String> interests = (ArrayList<String>) entity.getProperty("interests");

    LinkedHashSet<String> badgeIds = (entity.getProperty("badges") == null)
        ? new LinkedHashSet<>()
        : new LinkedHashSet<String>((ArrayList<String>) entity.getProperty("badges"));
    LinkedHashSet<String> groupIds = (entity.getProperty("groups") == null)
        ? new LinkedHashSet<>()
        : new LinkedHashSet<String>((ArrayList<String>) entity.getProperty("groups"));
    LinkedHashSet<Badge> badges = getBadges(badgeIds);
    LinkedHashSet<Group> groups = getGroups(groupIds);

    User user = new User(userId, firstName, lastName, email, phoneNumber, profilePic, 
                         badges, groups, interests);
    return user;
  }

 /**
  * Creates and returns a list of Groups given a list of groupIds (Strings).
  */
  public LinkedHashSet<Group> getGroups(LinkedHashSet<String> groupIdList) {
    return null;
  }

 /**
  * Creates and returns a Group object given a group Entity.
  */
  public Group getGroupObject(Entity entity) {
    return null;
  }

 /**
  * Creates and returns a list of Badges given a list of badgeIds (Strings).
  */
  public LinkedHashSet<Badge> getBadges(LinkedHashSet<String> badgeIdList) {
    return null;
  }

 /**
  * Creates and returns a Badge object given a badge Entity.
  */
  public Badge getBadgeObject(Entity entity) {
    return null;
  }
}