package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@WebServlet("/join-group")
public class JoinGroupServlet extends AuthenticatedServlet {

  /** Gets whether user is already in the group. */
  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    long groupId = Long.parseLong(request.getParameter("groupId"));
    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    boolean isMember = this.getIsMember(userId, groupEntity);

    String json = "{";
    json += "\"isMember\": ";
    json += "\"" + String.valueOf(isMember) + "\"";
    json += "}";
    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  /**
   * Adds user to the group by updating members list for group entity and updating groups list for
   * user entity.
   */
  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    long groupId = Long.parseLong(request.getParameter("groupId"));
    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    Entity userEntity = ServletHelper.getUserFromId(response, userId, datastore);
    this.updateGroupMembersList(groupEntity, userId, datastore);
    this.updateUserGroupsList(userEntity, groupId, datastore);
  }

  private boolean getIsMember(String userId, Entity groupEntity) {
    List<String> members = (ArrayList<String>) groupEntity.getProperty("memberIds");
    return members.contains(userId);
  }

  /**
   * Adds user to group's list of member ids.
   *
   * @param groupEntity
   * @param userId
   * @param datastore
   */
  private void updateGroupMembersList(
      Entity groupEntity, String userId, DatastoreService datastore) {
    System.out.println("update group members list");
    List<String> memberIds = (ArrayList<String>) groupEntity.getProperty("memberIds");
    memberIds.add(userId);
    groupEntity.setProperty("memberIds", memberIds);
    datastore.put(groupEntity);
  }

  /**
   * Adds group to user's list of groups.
   *
   * @param userEntity
   * @param groupId
   * @param datastore
   */
  private void updateUserGroupsList(Entity userEntity, long groupId, DatastoreService datastore) {
    List<Long> groupIds =
        (userEntity.getProperty("groups") == null)
            ? new ArrayList<Long>()
            : (ArrayList<Long>) userEntity.getProperty("groups");
    System.out.println("update user groups list");
    if (!groupIds.contains(groupId)) {
      groupIds.add(groupId);
    }
    userEntity.setProperty("groups", groupIds);
    datastore.put(userEntity);
  }
}
