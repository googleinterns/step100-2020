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
import com.google.sps.Objects.response.UserGroupResponse;
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
 * Servlet that returns a user's groups data from the Datastore.
 */
@WebServlet("/user-groups")
public class UserGroupsServlet extends AuthenticatedServlet {

  private UserService userService = UserServiceFactory.getUserService();
  private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /**
   * Gets User's Group data from the Datastore and returns it.
   */
  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response) 
      throws IOException {
    Entity currentUser = null;
    ArrayList<UserGroupResponse> groups = new ArrayList<>();

    currentUser = getUserEntity(userId, response);

    LinkedHashSet<Long> groupIds = (currentUser.getProperty("groups") == null)
      ? new LinkedHashSet<>()
      : new LinkedHashSet<Long>((ArrayList<Long>) currentUser.getProperty("groups"));
      
    groups = getGroupsFromIds(groupIds, response);

    // If either of these values are null, an error occured. Do not continue.
    if (currentUser == null || groups == null) {
      return;
    }

    // Convert to json
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(groups));
  }

  /*
   * Returns the User Entity corresponding to the current user.
   */
  public Entity getUserEntity(String userId, HttpServletResponse response) throws IOException {
    Key entityKey = KeyFactory.createKey("User", userId);
    try {
      return datastore.get(entityKey);
    } catch (EntityNotFoundException e) {
      ErrorHandler.sendError(response, "User not found.");
      return null;
    }
  }

  /*
   * Returns the list of users groups given a list of group ids.
   */
  public ArrayList<UserGroupResponse> getGroupsFromIds(LinkedHashSet<Long> groupIds, 
      HttpServletResponse response) throws IOException {
    ArrayList<UserGroupResponse> groups = new ArrayList<>();
    for (long groupId : groupIds) {
      Entity groupEntity = null;
      try {
        groupEntity = datastore.get(KeyFactory.createKey("Group", groupId));
      } catch (EntityNotFoundException e) {
        ErrorHandler.sendError(response, "Group does not exist.");
        return null;
      }
      UserGroupResponse groupResponse = UserGroupResponse.fromEntity(groupEntity);
      groups.add(groupResponse);
    }
    return groups;
  }

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {}
}