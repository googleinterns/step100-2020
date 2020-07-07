package com.google.sps.Objects.response;

import java.util.ArrayList;
import com.google.appengine.api.datastore.Entity;

/**
 * Contains information for the response sent by AllGroupMembersServlet.
 */
public final class BasicMemberResponse {

  private final String profilePic;
  private final String firstName;
  private final String lastName;
  private final String userId;

  /**
   * Constructs a BasicMemberResponse object.
   *
   * @param profilePic String url of profile image 
   * @param firstName String of user first name 
   * @param lastName String of user last name 
   * @param userId user id 
   */
  public BasicMemberResponse(String profilePic, String firstName, String lastName,String userId) {
    this.profilePic = profilePic;
    this.firstName = firstName;
    this.lastName = lastName;
    this.userId = userId;
  }
  
  /**
   * Constructs a BasicMemberResponse object from an entity
   *
   * @param profilePic String url of profile image 
   * @param firstName String of user first name 
   * @param lastName String of user last name  
   * @param userId user id 
   */
  public static BasicMemberResponse fromEntity(Entity entity) {
    String profilePic = (String) entity.getProperty("profilePic");
    String firstName = (String) entity.getProperty("firstName");
    String lastName = (String) entity.getProperty("lastName");
    String userId = (String) entity.getProperty("userId"); 
    return new BasicMemberResponse(profilePic, firstName, lastName, userId);
  }

  /**
   *
   * @return String of user first name 
   */
  public String getFirstName() {
    return this.firstName;
  }

  /**
   *
   * @return String of user last name 
   */
  public String getLastName() {
    return this.lastName;
  }

  /**
   *
   * @return String of img url
   */
  public String getStringURL() {
    return this.profilePic;
  }

  /**
   *
   * @return String of user id 
   */
  public String getUserId() {
    return this.userId;
  }
}

