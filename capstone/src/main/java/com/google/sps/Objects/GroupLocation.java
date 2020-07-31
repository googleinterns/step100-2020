package com.google.sps.Objects;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.sps.servlets.ServletHelper;
import com.google.sps.Objects.Coordinate;
import com.google.appengine.api.datastore.EntityNotFoundException;
import java.io.IOException;

public class GroupLocation {

  private Long groupId;

  public GroupLocation(Long groupId) {
    this.groupId = groupId;
  }

  /* Find group midpoint by groupId */
  public Coordinate findGroupMidPoint(Long groupId) throws IOException{
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    
    Entity groupEntity = getGroupEntity(groupId, datastore);
    if (groupEntity == null) return null;
    ArrayList<String> memberIds = (ArrayList<String>) groupEntity.getProperty("memberIds");

    ArrayList<Coordinate> groupCoordinates = createGroupPointsArray(memberIds);
    return findMidPoint(groupCoordinates);
  }

  /* Create array of group's coordinate locations */
  public ArrayList<Coordinate> createGroupPointsArray(ArrayList<String> allGroupMembers) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    ArrayList<Coordinate> groupPoints = new ArrayList<>();
    for(String memberId: allGroupMembers) {
      Entity userEntity = getUserEntity(memberId, datastore);
      if (userEntity != null && (double) userEntity.getProperty("latitude") != 0.0 && (double) userEntity.getProperty("longitude") != 0.0) {
        Coordinate newCoordinate = new Coordinate(
          (double) userEntity.getProperty("latitude"), 
          (double) userEntity.getProperty("longitude"));
        groupPoints.add(newCoordinate);
      }
    }
    return groupPoints;
  }

  /* Calculate geographic midpoint of all group coordinates */
  private Coordinate findMidPoint(ArrayList<Coordinate> groupLocations) {
    double x = 0.0;
    double y = 0.0;
    double z = 0.0;

    if (groupLocations.size() == 1) {
      return groupLocations.get(0);
    }

    for (Coordinate coord: groupLocations) {
      double latitude = coord.getLat() * Math.PI / 180;
      double longitude = coord.getLng() * Math.PI / 180;

      x += Math.cos(latitude) * Math.cos(longitude);
      y += Math.cos(latitude) * Math.sin(longitude);
      z += Math.sin(latitude);
    }

    int groupSize = groupLocations.size();
    x = x / groupSize;
    y = y / groupSize;
    z = z / groupSize;

    double centralLongitude = Math.atan2(y, x);
    double centralSquareRoot = Math.sqrt(x * x + y * y);
    double centralLatitude = Math.atan2(z, centralSquareRoot);

    return new Coordinate(centralLatitude * 180 / Math.PI, centralLongitude * 180 / Math.PI);
  }

  private Entity getUserEntity(String userId, DatastoreService datastore) throws IOException {
    try {
      return datastore.get(KeyFactory.createKey("User", userId));
    } catch (EntityNotFoundException e) {
      return null;
    }
  }

  private Entity getGroupEntity(Long groupId, DatastoreService datastore) throws IOException {
    try { 
      return datastore.get(KeyFactory.createKey("Group", groupId));
    } catch (EntityNotFoundException e) {
      return null;
    }
  }
}

  