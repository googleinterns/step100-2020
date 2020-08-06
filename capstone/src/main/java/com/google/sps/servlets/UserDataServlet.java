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
import com.google.sps.error.ErrorHandler;
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

/**
 * Servlet to handle returning User data from the Datastore.
 */
@WebServlet("/user")
public class UserDataServlet extends AuthenticatedServlet {

  /**
   * Gets User data from the Datastore and returns it.
   */
  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response) 
      throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    User currentUser = null;

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key entityKey = KeyFactory.createKey("User", userId);
    try {
      currentUser = User.fromEntity(datastore.get(entityKey));
    } catch (EntityNotFoundException e) {
      ErrorHandler.sendError(response, "User not found.");
    }

    // Convert to json
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(currentUser));
  }

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {}
}