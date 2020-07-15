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
import com.google.gson.Gson;
import com.google.sps.Objects.response.MemberResponse;
import error.ErrorHandler;

@WebServlet("/group-member")

public class GroupMemberServlet extends AuthenticatedServlet {

  @Override
  // Adds a new member to a group 
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    Long groupId = Long.parseLong(request.getParameter("groupId"));

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity group = this.getGroupFromId(response, groupId, datastore);
    if (group != null) {
      ArrayList<String> members = 
        (ArrayList<String>) group.getProperty("members");
      if (members == null) {
        members = new ArrayList<>();
      }

      this.addMember(members, userId);
      group.setProperty("members", members);
      datastore.put(group);
    } else {
      return;
    }
  }

  private void addMember(ArrayList<String> members, String userId) {
    if (!members.contains(userId)) {
      members.add(userId);
    }
  }

  private Entity getGroupFromId(
    HttpServletResponse response, long groupId, DatastoreService datastore) throws IOException {
    try {
      return datastore.get(KeyFactory.createKey("Group", groupId));
    } catch (EntityNotFoundException e) {
      ErrorHandler.sendError(response, "Group does not exist.");
      return null;
    }
  }

  private Entity getUserFromId(
    HttpServletResponse response, String userId, DatastoreService datastore) throws IOException {
    try {
      return datastore.get(KeyFactory.createKey("User", userId));
    } catch (EntityNotFoundException e) {
      ErrorHandler.sendError(response, "User does not exist.");
      return null;
    }
  }

  // Gets a MemberResponse object from userId
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String userId = request.getParameter("id");
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity member = this.getUserFromId(response, userId, datastore);
    MemberResponse memResponse = MemberResponse.fromEntity(
      member, /* includeBadges= */ true);

    // Convert to json
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(memResponse));  
  }

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {}
}