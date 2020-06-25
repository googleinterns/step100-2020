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

import com.google.sps.Objects.Badge;
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
import java.util.LinkedHashSet;

@WebServlet("/signup")
public class SignUpServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      // Get the input from the form.
      String first = request.getParameter("first");
      String last = request.getParameter("last");
      String email = userService.getCurrentUser().getEmail();
      String userId = userService.getCurrentUser().getUserId();

      if (first.equals("") && last.equals("")) {
        first = userService.getCurrentUser().getNickname();
      }

      // Check if user already exists in Datastore. If so, do nothing.
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Key entityKey = KeyFactory.createKey("User", userId);
      try {
        datastore.get(entityKey);
      } catch (EntityNotFoundException e) {
        // If the user doesn't exist in Datastore, then create the user.
        Entity userEntity = new Entity("User", userId);
        userEntity.setProperty("userId", userId);
        userEntity.setProperty("firstName", first);
        userEntity.setProperty("lastName", last);
        userEntity.setProperty("email", email);
        userEntity.setProperty("phoneNumber", "");
        userEntity.setProperty("badges", new LinkedHashSet<String>());
        userEntity.setProperty("groups", new LinkedHashSet<String>());
        userEntity.setProperty("interests", new LinkedHashSet<String>());
        datastore.put(userEntity);
      }
    }
  }
}