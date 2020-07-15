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
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.PrintWriter;
import com.google.gson.Gson;
import com.google.sps.Objects.response.MemberResponse;
import error.ErrorHandler;

@WebServlet("/group-member")

public class GroupMemberServlet extends HttpServlet {

  @Override
  // Adds a new member to a group 
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    
    String email = request.getParameter("email");
    Long groupId = Long.parseLong(request.getParameter("groupId"));

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity memberEntity = getMemberEntity(email, response, datastore);
    Entity group = this.getGroupFromId(response, groupId, datastore);
    if (group == null) return;

    String memResponse = doPost_helper(response, datastore, groupId, memberEntity, group);
    String json = "{\"response\":" + "\"" + memResponse + "\"}";
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  // If no account for email, return 'This email doesn't have an account'
  // If user already in group, return 'User already in group'
  // If user not it group, add user and return 'User added to group'
  private String doPost_helper(HttpServletResponse response, DatastoreService datastore, Long groupId, Entity memberEntity, Entity group) {
    String memResponse = "";
    if (memberEntity == null) {
      memResponse += "This email doesn't have an account.";
    } else {

      ArrayList<String> members = 
        (ArrayList<String>) group.getProperty("members");
      if (members == null) {
        members = new ArrayList<>();
      }

      String memberId = (String) memberEntity.getProperty("userId");
      if (members.contains(memberId)) {
        memResponse += "User is already in group.";
      } else {
        this.addMember(members, memberId);
        group.setProperty("memberIds", members);
        datastore.put(group);
        memResponse += "User added to group.";
      }
    }
    return memResponse;
  }

  private Entity getMemberEntity(String email, HttpServletResponse response, DatastoreService datastore){
    Filter findMemberEntity =
    new FilterPredicate("email", FilterOperator.EQUAL, email);
    Query query = new Query("User").setFilter(findMemberEntity);
    PreparedQuery pq = datastore.prepare(query);
    return pq.asSingleEntity();
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
    ErrorHandler.sendError(response, "User is not logged in.");
    return "";
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
}