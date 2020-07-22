package com.google.sps.Objects;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.servlets.ServletHelper;

public final class Group {

  private final ArrayList<String> memberIds;
  private final ArrayList<Long> challengeIds;
  private final ArrayList<Long> postIds;
  private final ArrayList<Long> optionIds;
  private final String groupName;
  private final String headerImg;
  private final long groupId;
  private double midLatitude;
  private double midLongitude;
  private ArrayList<Long> locationIds;

  public Group(
      ArrayList<String> memberIds,
      ArrayList<Long> challenges,
      ArrayList<Long> posts,
      ArrayList<Long> options,
      ArrayList<Long> locationsIds,
      String groupName,
      String headerImg,
      long groupId, 
      double midLatitude, 
      double midLongitude) {
    this.memberIds = memberIds;
    this.challengeIds = challenges;
    this.postIds = posts;
    this.optionIds = options;
    this.locationIds = locationIds;
    this.groupName = groupName;
    this.headerImg = headerImg;
    this.groupId = groupId;
    this.midLongitude = midLongitude;
    this.midLatitude = midLatitude;
  }

  public static Entity getGroupEntity(
      HttpServletRequest request, HttpServletResponse response, DatastoreService datastore)
      throws IOException {
    String groupIdString = request.getParameter("groupId");
    long groupId = Long.parseLong(groupIdString);
    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    return groupEntity;
  }

  public ArrayList<String> getMemberIds() {
    return memberIds;
  }

  public ArrayList<Long> challenges() {
    return challengeIds;
  }

  public ArrayList<Long> getPosts() {
    return postIds;
  }

  public ArrayList<Long> getOptions() {
    return optionIds;
  }

  public String getGroupName() {
    return groupName;
  }

  public String getHeaderImg() {
    return headerImg;
  }

  public long getGroupId() {
    return groupId;
  }

  public double getMidLatitude() {
    return midLatitude;
  }

  public double getMidLongitude() {
    return midLongitude;
  }

  public ArrayList<Long> getLocationsIds() { 
    return locationIds;
  }

  public void setMidLatitude(double midLatitude) {
    this.midLatitude = midLatitude;
  }

  public void setMidLongitude(double midLongitude) {
    this.midLongitude = midLongitude;
  }

  public void setLocationIds(ArrayList<Long> locationIds) {
    this.locationIds = locationIds;
  }

  public void addChallenge(Long newChallenge) {
    this.challengeIds.add(newChallenge);
  }

  public void addMember(String memberId) {
    this.memberIds.add(memberId);
  }

  public void addPost(Long newPost) {
    this.postIds.add(newPost);
  }

  /* Given a Group entity, creates and returns a Group object. */
  public static Group fromEntity(Entity entity) {
    ArrayList<String> memberIds = (ArrayList<String>) entity.getProperty("memberIds");
    ArrayList<Long> challenges = getPropertyList("challenges", entity);
    ArrayList<Long> posts = getPropertyList("posts", entity);
    ArrayList<Long> options = getPropertyList("options", entity);
    ArrayList<Long> locationIds = getPropertyList("locations", entity);
    String groupName = (String) entity.getProperty("groupName");
    String headerImg = (String) entity.getProperty("headerImg");
    double midLatitude = (double) entity.getProperty("midLatitude");
    double midLongitude = (double) entity.getProperty("midLongitude");
    long groupId = entity.getKey().getId();

    return new Group(memberIds, challenges, posts, options, locationIds, groupName, headerImg, groupId, midLatitude, midLongitude);
  }

  private static ArrayList<Long> getPropertyList(String property, Entity entity) {
    ArrayList<Long> propertyList =
        (entity.getProperty(property) == null)
            ? new ArrayList<Long>()
            : (ArrayList<Long>) entity.getProperty(property);
    return propertyList;
  }
}
