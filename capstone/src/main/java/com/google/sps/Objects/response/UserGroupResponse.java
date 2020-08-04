package com.google.sps.Objects.response;

import java.util.ArrayList;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.Objects.Challenge;
import com.google.sps.servlets.ServletHelper;

/**
 * Used as a wrapper class to contain information needed to convert to JSON to
 * pass to frontend. Describes information about a group that needs to be
 * accessible from a User's profile. 
 * Includes a list of challenges, the name of the group,the url of the group's 
 * header image, and the group id.
 */
public final class UserGroupResponse {

  private ArrayList<Challenge> challenges;
  private final String groupName;
  private final String headerImg;
  private final long groupId;
  
  public UserGroupResponse(ArrayList<Challenge> challenges, String groupName, 
      String headerImg, long groupId) {
    this.challenges = challenges;
    this.groupName = groupName;
    this.headerImg = headerImg;
    this.groupId = groupId;
  }

  /**
   * Creates and returns a UserGroupResponse object given a group Entity.
   */
  public static UserGroupResponse fromEntity(Entity entity) {
    long groupId = (long) entity.getKey().getId();
    String groupName = (String) entity.getProperty("groupName");
    String headerImg = (String) entity.getProperty("headerImg");
    ArrayList<Challenge> challenges = new ArrayList<>();
    UserGroupResponse response = new UserGroupResponse(challenges, groupName, headerImg, groupId);
    return response;
  }

  public void setChallenges(ArrayList<Challenge> challenges) {
    this.challenges = challenges;
  } 
}