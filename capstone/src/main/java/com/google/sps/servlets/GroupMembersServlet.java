package com.google.sps.servlets;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import error.ErrorHandler;

@WebServlet("/group-members")

public class GroupMembersServlet extends HttpServlet {

  private ErrorHandler errorHandler = new ErrorHandler();

  @Override
  // Adds a new member to a group 
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    String userId = this.getUserId(response);
    Long groupId = Long.parseLong(request.getParameter("groupId"));

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity group = this.getGroupFromId(response, groupId, datastore);
    ArrayList<String> members = 
      (ArrayList<String>) group.getProperty("members");
    if (members == null) {
      members = new ArrayList<>();
    }

    this.addMember(members, userId);
    group.setProperty("members", members);
    datastore.put(group);
  }

  private void addMember(ArrayList<String> members, String userId) {
    if (!members.contains(userId)) {
      members.add(userId);
    }
  }

  private String getUserId(HttpServletResponse response) throws IOException {
  UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      return userService.getCurrentUser().getUserId();
    }
    errorHandler.sendError(response, "User is not logged in.");
    return "";
  }

  private Entity getGroupFromId(HttpServletResponse response, long groupId, DatastoreService datastore) throws IOException {
    try {
      return datastore.get(KeyFactory.createKey("Group", groupId));
    } catch (EntityNotFoundException e) {
      errorHandler.sendError(response, "Group does not exist.");
      return null;
    }
  }
}
