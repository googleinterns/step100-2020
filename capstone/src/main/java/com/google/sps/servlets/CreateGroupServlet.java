package com.google.sps.servlets;

import com.google.sps.Objects.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.EntityNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
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
    groupEntity.setProperty("options", new ArrayList<EmbeddedEntity>());    
    groupEntity.setProperty("groupName", groupName);
    groupEntity.setProperty("headerImg", "");
    return groupEntity;
	}

  /**
   * Add group to a user's list of groups.
   */
  private void addUserToGroup(String userId, long groupId, HttpServletResponse response, 
      DatastoreService datastore) throws IOException {
    Entity userEntity = getExistingUser(userId, response, datastore);

    ArrayList<Long> groups = (userEntity.getProperty("groups") == null)
        ? new ArrayList<Long>()
       : (ArrayList<Long>) userEntity.getProperty("groups");
    groups.add(groupId);
    userEntity.setProperty("groups", groups);

    datastore.put(userEntity);
  }

  /** 
   * Retrieves existing user entity from datastore.
   */
  private Entity getExistingUser(String userId, HttpServletResponse response, 
      DatastoreService datastore) throws IOException {
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
