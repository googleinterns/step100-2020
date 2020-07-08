package com.google.sps.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import error.ErrorHandler;

public abstract class AuthenticatedServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userId = userService.getCurrentUser().getUserId();
      this.doGet(userId, request, response);
    } else {
      ErrorHandler.sendError(response, "User is not logged in.");
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      String userId = userService.getCurrentUser().getUserId();
      this.doPost(userId, request, response);
    } else {
      ErrorHandler.sendError(response, "User is not logged in.");
    }
  }

  public abstract void doGet(
      String userId, HttpServletRequest request, HttpServletResponse response) throws IOException;

  public abstract void doPost(
      String userId, HttpServletRequest request, HttpServletResponse response) throws IOException;
}
