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
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@WebServlet("/createNewUser")
public class CreateNewUserServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String first = request.getParameter("first");
    String last = request.getParameter("last");
    String phone = request.getParameter("phone");
    ArrayList<String> interests = getInterests(request);
    
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(createUserEntity(userService, first, last, phone, interests));
    }
  }

  /**
   * Creates a new User Entity and adds it to the Datastore.
   */
  private Entity createUserEntity(UserService userService, String first, String last, 
                                  String phone, ArrayList<String> interests) {
    String userId = userService.getCurrentUser().getUserId();
    Entity userEntity = new Entity("User", userId);
    userEntity.setProperty("userId", userId);
    userEntity.setProperty("firstName", first);
    userEntity.setProperty("lastName", last);
    userEntity.setProperty("email", userService.getCurrentUser().getEmail());
    userEntity.setProperty("phoneNumber", phone);
    userEntity.setProperty("badges", new ArrayList<Badge>());
    userEntity.setProperty("groups", new ArrayList<String>());
    userEntity.setProperty("interests", interests);
    return userEntity;
  }

  /**
   * Gets the list of interests entered by user.
   */
  private ArrayList<String> getInterests(HttpServletRequest request) {
    // Get input from the form.
    String interests = request.getParameter("interests");

    // Convert the input to an array list.
    String[] interestsArray = interests.split(",");
    ArrayList<String> interestsList = new ArrayList<String>();
    interestsList.addAll(Arrays.asList(interestsArray));

    return interestsList;
  }
}
