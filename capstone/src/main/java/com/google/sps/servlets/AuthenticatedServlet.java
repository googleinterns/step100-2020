package com.google.sps.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public abstract class AuthenticatedServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userId = userService.getCurrentUser().getUserId();
      this.doGet(userId, request, response);
    } else {
      this.redirectToLogin(userService, response);
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userId = userService.getCurrentUser().getUserId();
      this.doPost(userId, request, response);
    } else {
      this.redirectToLogin(userService, response);
    }
  }

  /**
   * Redirects user to login page.
   *
   * @param userService UserService instance
   * @param response response data
   * @throws IOException exception thrown if cannot read or write to file
   */
  private void redirectToLogin(UserService userService, HttpServletResponse response)
      throws IOException {
    String loginUrl = userService.createLoginURL("/");
    response.sendRedirect(loginUrl);
  }

  public abstract void doGet(
      String userId, HttpServletRequest request, HttpServletResponse response) throws IOException;

  public abstract void doPost(
      String userId, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
