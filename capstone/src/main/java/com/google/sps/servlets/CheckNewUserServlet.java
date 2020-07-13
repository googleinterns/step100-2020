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

import error.ErrorHandler;
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
import java.util.ArrayList;

@WebServlet("/checkNewUser")
public class CheckNewUserServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    boolean isUserNew = false;

    if (userService.isUserLoggedIn()) {
      String userId = userService.getCurrentUser().getUserId();

      // Check if user already exists in Datastore. If so, do nothing.
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Key entityKey = KeyFactory.createKey("User", userId);
      try {
        datastore.get(entityKey);
      } catch (EntityNotFoundException e) {
        // If the user doesn't exist in Datastore.
        isUserNew = true;
      }
    } else {
      ErrorHandler.sendError(response, "User not logged in.");
      return;
    }

    String json = String.format("{\"isUserNew\": %b}", isUserNew);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}
