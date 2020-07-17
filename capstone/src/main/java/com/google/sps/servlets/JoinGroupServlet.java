package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@WebServlet("/join-group")
public class JoinGroupServlet extends HttpServlet {

  /** Gets whether user is already in the group. */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      String userId = userService.getCurrentUser().getUserId();
      long groupId = Long.parseLong(request.getParameter("groupId"));
      Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
      String isMember = String.valueOf(this.getIsMember(userId, groupEntity));
      ServletHelper.write(response, isMember, "application/json");
    } else {
      // Redirect user to sign in page
    }
  }

  /** Adds user to the group. */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // TODO Auto-generated method stub

  }

  private boolean getIsMember(String userId, Entity groupEntity) {
    List<Long> members = (ArrayList<Long>) groupEntity.getProperty("members");
    return members.contains(userId);
  }
}
