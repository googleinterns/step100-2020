package com.google.sps.Objects.response;

import java.util.ArrayList;
import com.google.sps.Objects.Badge;
import java.util.LinkedHashSet;
import com.google.appengine.api.datastore.Entity;

/**
 * Contains information for the response sent by GroupMembersServlet.
 */
public final class MemberResponse {

  private final String profilePic;
  private final String firstName;
  private final String lastName;
  private final LinkedHashSet<Badge> badges;
  private final String userId;

  /**
   * Constructs a MemberResponse object.
   *
   * @param profilePic String url of profile image 
   * @param firstName String of user first name 
   * @param lastName String of user last name
   * @param badges List of badge objects 
   * @param userId user id 
   */
  public MemberResponse(String profilePic, String firstName, String lastName, LinkedHashSet<Badge> badges, String userId) {
    this.profilePic = profilePic;
    this.badges = badges;
    this.firstName = firstName;
    this.lastName = lastName;
    this.userId = userId;
  }
  
  /**
   * Constructs a MemberResponse object from an entity
   *
   * @param profilePic String url of profile image 
   * @param firstName String of user first name 
   * @param lastName String of user last name  
   * @param badges List of badge objects 
   * @param userId user id 
   */
  public static MemberResponse fromEntity(Entity entity) {
    String profilePic = (String) entity.getProperty("profilePic");
    String firstName = (String) entity.getProperty("firstName");
    String lastName = (String) entity.getProperty("lastName");
    String userId = (String) entity.getProperty("userId");
    LinkedHashSet<Badge> badges = (entity.getProperty("badges") == null) 
      ? new LinkedHashSet<>() 
      : new LinkedHashSet<Badge>((ArrayList<Badge>) entity.getProperty("badges"));   
    return new MemberResponse(profilePic, firstName, lastName, badges, userId);
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
   * @return String of user first name 
   */
  public String getLastName() {
    return this.lastName;
  }

  /**
   *
   * @return String of post url
   */
  public String getStringURL() {
    return this.profilePic;
  }

  /**
   *
   * @return List of badges
   */
  public LinkedHashSet<Badge> getBadges() {
    return this.badges;
  }

  /**
   *
   * @return String of user id 
   */
  public String getUserId() {
    return this.userId;
  }
}

