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
import com.google.sps.servlets.ServletHelper;
import error.ErrorHandler;

@WebServlet("/group-member")

public class GroupMemberServlet extends AuthenticatedServlet {

  @Override
  // Adds a new member to a group 
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response) throws IOException {

    String email = request.getParameter("email");
    Long groupId = Long.parseLong(request.getParameter("groupId"));

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity memberEntity = getMemberEntity(email, response, datastore);
    Entity group = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    if (group == null) return;

    String memResponse = doPost_helper(response, datastore, groupId, memberEntity, group);
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
        (ArrayList<String>) group.getProperty("memberIds");
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
        addGroupToUser(response, datastore, groupId, memberEntity);
      }
    }
    return memResponse;
  }

  private void addGroupToUser(HttpServletResponse response, DatastoreService datastore, Long groupId, Entity memberEntity) {
    ArrayList<Long> groups = 
        (ArrayList<Long>) memberEntity.getProperty("groups");
    if (groups == null) {
      groups = new ArrayList<>();
    }
    groups.add(groupId);
    memberEntity.setProperty("groups", groups);
    datastore.put(memberEntity);
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

  private Entity getGroupFromId(
    HttpServletResponse response, long groupId, DatastoreService datastore) throws IOException {
    try {
      return datastore.get(KeyFactory.createKey("Group", groupId));
    } catch (EntityNotFoundException e) {
      ErrorHandler.sendError(response, "Group does not exist.");
      return null;
    }
  }

  // Gets a MemberResponse object from userId
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    String userId = request.getParameter("id");
    
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity member = ServletHelper.getUserFromId(response, userId, datastore);
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