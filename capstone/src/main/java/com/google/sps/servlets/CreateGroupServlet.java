package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import com.google.sps.Objects.Tag;
import error.ErrorHandler;

@WebServlet("/createGroup")
public class CreateGroupServlet extends AuthenticatedServlet {

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String groupName = request.getParameter("groupName");

    ArrayList<String> members = new ArrayList<String>();
    members.add(userId);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    // Creates Group with submitted data and add to database
    Entity groupEntity = createGroupEntity(groupName, members);
    datastore.put(groupEntity);
    addUserToGroup(userId, groupEntity.getKey().getId(), response, datastore);
  }

  private Entity createGroupEntity(String groupName, ArrayList<String> members) {
    Entity groupEntity = new Entity("Group");
    groupEntity.setProperty("memberIds", members);
    groupEntity.setProperty("challenges", new ArrayList<Long>());
    groupEntity.setProperty("posts", new ArrayList<Long>());
    groupEntity.setProperty("options", new ArrayList<Long>());
    groupEntity.setProperty("locationIds", new ArrayList<Long>());
    groupEntity.setProperty("tags", new ArrayList<Tag>());
    groupEntity.setProperty("groupName", groupName);
    groupEntity.setProperty("headerImg", "");
    groupEntity.setProperty("midLatitude", 0.0);
    groupEntity.setProperty("midLongitude", 0.0);
    return groupEntity;
  }

  /** Add group to a user's list of groups. */
  private void addUserToGroup(
      String userId, long groupId, HttpServletResponse response, DatastoreService datastore)
      throws IOException {
    Entity userEntity = getExistingUser(userId, response, datastore);

    ArrayList<Long> groups =
        (userEntity.getProperty("groups") == null)
            ? new ArrayList<Long>()
            : (ArrayList<Long>) userEntity.getProperty("groups");
    groups.add(groupId);
    userEntity.setProperty("groups", groups);

    datastore.put(userEntity);
  }

  /** Retrieves existing user entity from datastore. */
  private Entity getExistingUser(
      String userId, HttpServletResponse response, DatastoreService datastore) throws IOException {
    Key entityKey = KeyFactory.createKey("User", userId);
    Entity userEntity;
    try {
      userEntity = datastore.get(entityKey);
    } catch (EntityNotFoundException e) {
      ErrorHandler.sendError(response, "User not found.");
      userEntity = null;
    }
    return userEntity;
  }

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {}
}
