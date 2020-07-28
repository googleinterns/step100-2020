package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.gson.Gson;
import com.google.sps.Objects.response.MemberResponse;

import error.ErrorHandler;

@WebServlet("/group-member")
public class GroupMemberServlet extends AuthenticatedServlet {

  @Override
  // Adds a new member to a group
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    String email = request.getParameter("email");
    String newMemberId = request.getParameter("userId");

    Long groupId = Long.parseLong(request.getParameter("groupId"));

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    Entity memberEntity = null;
    if (email != null) {
      memberEntity = getMemberEntityFromEmail(email, response, datastore);
      if (memberEntity == null) return;
    } else if (newMemberId != null) {
      memberEntity = ServletHelper.getUserFromId(response, newMemberId, datastore);
    }

    Entity group = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    if (group == null) return;

    doPost_maybeAddUserToGroup(response, datastore, groupId, memberEntity, group);
  }

  // If no account for email, return 'This email doesn't have an account'
  // If user already in group, return 'User already in group'
  // If user not it group, add user and return 'User added to group'
  private void doPost_maybeAddUserToGroup(
      HttpServletResponse response,
      DatastoreService datastore,
      Long groupId,
      Entity memberEntity,
      Entity group) {
    if (memberEntity != null) {
      ArrayList<String> members = (ArrayList<String>) group.getProperty("memberIds");
      if (members == null) {
        members = new ArrayList<>();
      }

      String memberId = (String) memberEntity.getProperty("userId");
      if (addGroupToUser(response, datastore, groupId, memberEntity)) {
        this.addMember(members, memberId);
        group.setProperty("memberIds", members);
        datastore.put(group);
      }
    }
  }

  private boolean addGroupToUser(
      HttpServletResponse response, DatastoreService datastore, Long groupId, Entity memberEntity) {
    ArrayList<Long> groups = (ArrayList<Long>) memberEntity.getProperty("groups");
    if (groups == null) {
      groups = new ArrayList<>();
    }
    if (!groups.contains(groupId)) {
      groups.add(groupId);
      memberEntity.setProperty("groups", groups);
      datastore.put(memberEntity);
      return true;
    }
    return false;
  }

  private Entity getMemberEntityFromEmail(
      String email, HttpServletResponse response, DatastoreService datastore) throws IOException {
    Filter findMemberEntity = new FilterPredicate("email", FilterOperator.EQUAL, email);
    Query query = new Query("User").setFilter(findMemberEntity);
    PreparedQuery pq = datastore.prepare(query);
    if (pq.asSingleEntity() == null) {
      ErrorHandler.sendError(response, "Cannot get entity from datastore");
      return null;
    }
    return pq.asSingleEntity();
  }

  private void addMember(ArrayList<String> members, String userId) {
    if (!members.contains(userId)) {
      members.add(userId);
    }
  }

  // Gets a MemberResponse object from userId
  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {

    String memberId = request.getParameter("id");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity member = ServletHelper.getUserFromId(response, memberId, datastore);
    if (member == null) return;
    MemberResponse memResponse = MemberResponse.fromEntity(member, /* includeBadges= */ false);

    // Convert to json
    response.setContentType("application/json;");
    response.getWriter().println(new Gson().toJson(memResponse));
  }
}
