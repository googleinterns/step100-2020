package com.google.sps.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.graph.UserVertex;

public class DatabaseRetriever implements Serializable {

  /** */
  private static final long serialVersionUID = 1L;

  public DatabaseRetriever() {}

  public List<String> getNamesFromDb() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("User");
    PreparedQuery pq = datastore.prepare(query);
    List<String> names = new ArrayList<String>();
    for (Entity userEntity : pq.asIterable()) {
      String firstName = (String) userEntity.getProperty("firstName");
      String lastName = (String) userEntity.getProperty("lastName");
      // unique separator to account for names like Marie Rose Shapiro
      String name = firstName + "@" + lastName;
      names.add(name);
    }
    return names;
  }

  public static Map<UserVertex, Integer> getImmediateFriends(String currUserId) {
    Map<UserVertex, Integer> friendsToNumSharedGroups = new HashMap<UserVertex, Integer>();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity currUserEntity = getEntityFromId(currUserId, "User", datastore);

    if (currUserEntity != null) {
      List<Long> groupIds = (currUserEntity.getProperty("groups") == null) ? new ArrayList<Long>() : (List<Long>) currUserEntity.getProperty("groups");
      System.out.println("group ids size " + groupIds.size());

      for (Long groupId : groupIds) {
        Entity groupEntity = getEntityFromId(groupId, "Group", datastore);
        if (groupEntity != null) {
          List<String> memberIds = (List<String>) groupEntity.getProperty("memberIds");
          for (String memberId : memberIds) {
            if (!memberId.equals(currUserId)) {
              System.out.println("member ids size " + memberId);
              UserVertex userVertex = new UserVertex(memberId);
              if (!friendsToNumSharedGroups.containsKey(userVertex)) {
                friendsToNumSharedGroups.put(userVertex, 1);
              } else {
                friendsToNumSharedGroups.put(
                    userVertex, 1 + friendsToNumSharedGroups.get(userVertex));
              }
            }
          }
        }
      }
    }

    return friendsToNumSharedGroups;
  }

  public static Entity getEntityFromId(String id, String type, DatastoreService datastore) {
    try {
      return datastore.get(KeyFactory.createKey(type, id));
    } catch (EntityNotFoundException e) {
      System.err.println("Cannot get entity from database");
      return null;
    }
  }

  public static Entity getEntityFromId(long id, String type, DatastoreService datastore) {
    try {
      return datastore.get(KeyFactory.createKey(type, id));
    } catch (EntityNotFoundException e) {
      System.err.println("Cannot get entity from database");
      return null;
    }
  }
}
