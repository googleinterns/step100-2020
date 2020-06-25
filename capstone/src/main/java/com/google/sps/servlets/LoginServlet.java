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

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    String loginUrl = userService.createLoginURL("/group.html");
    String logoutUrl = userService.createLogoutURL("/");

    String json;
    if (userService.isUserLoggedIn()) {
      json = String.format("{\"email\": \"%s\", \"url\": \"%s\"}",
                      userService.getCurrentUser().getEmail(), logoutUrl);
    } else {
      json = String.format("{\"email\": \"%s\", \"url\": \"%s\"}", 
                          "null", loginUrl);
    }

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }
}