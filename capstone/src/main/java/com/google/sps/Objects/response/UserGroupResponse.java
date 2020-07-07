package com.google.sps.Objects.response;

import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList;
import com.google.sps.Objects.Challenge;

/**
 * Used as a wrapper class to contain information needed to convert to JSON to
 * pass to frontend. Describes information about a group that needs to be
 * accessible from a User's profile. 
 * Includes a list of challenges, the name of the group,the url of the group's 
 * header image, and the group id.
 */
public final class UserGroupResponse {

  private final ArrayList<Challenge> challenges;
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
    long groupId = (long) entity.getProperty("groupId");
    String groupName = (String) entity.getProperty("groupName");
    String headerImg = (String) entity.getProperty("headerImg");
    // TODO: fix below code, won't work with Challenges as EmbeddedEntities of Groups
    ArrayList<Challenge> challenges = (ArrayList<Challenge>) entity.getProperty("challenges");
    UserGroupResponse response = new UserGroupResponse(challenges, groupName, headerImg, groupId);
    return response;
  }
}