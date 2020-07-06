package com.google.sps.Objects.response;

import java.util.List;
import com.google.sps.Objects.Badge;

/**
 * Contains information for the response sent by GroupMembersServlet.
 */
public final class MemberResponse {

  private final String profilePic;
  private final String name;
  private final LinkedHashSet<Badge> badges;
  private final String userId;

  /**
   * Constructs a MemberResponse object.
   *
   * @param profilePic String url of profile image 
   * @param name String of user full name  
   * @param badges List of badge objects 
   * @param userId user id 
   */
  public PostResponse(String profilePic, String name, LinkedHashSet<Badge> badges, String userId) {
    this.profilePic = profilePic;
    this.badges = badges;
    this.name = name;
    this.userId = userId;
  }
  
  /**
   * Constructs a MemberResponse object from an entity
   *
   * @param profilePic String url of profile image 
   * @param name String of user full name  
   * @param badges List of badge objects 
   * @param userId user id 
   */
  public static MemberResponse fromEntity(Entity entity) {
    String profilePic = (String) entity.getProperty("profilePic");
    String name = (String) entity.getProperty("name");
    String userId = (String) entity.getProperty("userId");
    HashSet<Badge> badges = (entity.getProperty("badges") == null) 
      ? new HashSet<>() 
      : new HashSet<Badge>((ArrayList<Badge>) entity.getProperty("badges"));   
    return new MemberResponse(profilePic, name, badges, userId);
  }

  /**
   *
   * @return String of user full name 
   */
  public String getName() {
    return this.name;
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

