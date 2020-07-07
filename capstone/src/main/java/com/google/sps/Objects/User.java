package com.google.sps.Objects;

import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList;
import java.util.LinkedHashSet;

public final class User {

  private final String userId;
  private String name;
  private String firstName;
  private String lastName;
  private final String email;
  private String phoneNumber;
  private String profilePic;
  private final LinkedHashSet<Badge> badges;
  private final LinkedHashSet<Long> groups;
  private final ArrayList<String> interests;

  public User(String userId, String firstName, String lastName, 
      String email, String phoneNumber, String profilePic,
      LinkedHashSet<Badge> badges, LinkedHashSet<Long> groups, ArrayList<String> interests) {
    this.userId = userId;
    this.name = firstName + " " + lastName;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.profilePic = profilePic;
    this.badges = badges;
    this.groups = groups;
    this.interests = interests;
  }

  public String getUserId() {
    return userId;
  }

  public String getName() {
    return name;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getProfilePic() {
    return profilePic;
  }

  public LinkedHashSet<Badge> getBadges() {
    return badges;
  }

  public LinkedHashSet<Long> getGroups() {
    return groups;
  }

  public ArrayList<String> getInterests() {
    return interests;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
    this.name = firstName + " " + getLastName();
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
    this.name = getFirstName() + " " + lastName;
  }

  public void setPhoneNumber(String newPhoneNumber) {
    this.phoneNumber = newPhoneNumber;
  }

  public void addBadge(Badge newBadge) {
    this.badges.add(newBadge);
  }

  public void addInterest(String newInterest) {
    this.interests.add(newInterest);
  }

  public void addGroup(long newGroupId) {
    this.groups.add(newGroupId);
  }

  /**
   * Creates and returns a User object given a user Entity.
   */
  public static User fromEntity(Entity entity) {
    String userId = (String) entity.getProperty("userId");
    String firstName = (String) entity.getProperty("firstName");
    String lastName = (String) entity.getProperty("lastName");
    String email = (String) entity.getProperty("email");
    String phoneNumber = (String) entity.getProperty("phoneNumber");
    String profilePic = ""; // TODO: add profilePic url to datastore/figure out Blobstore
    ArrayList<String> interests = (entity.getProperty("interests") == null)
        ? new ArrayList<>()
        : (ArrayList<String>) entity.getProperty("interests");
    LinkedHashSet<Long> groupIds = (entity.getProperty("groups") == null)
        ? new LinkedHashSet<>()
        : new LinkedHashSet<Long>((ArrayList<Long>) entity.getProperty("groups"));

    LinkedHashSet<String> badgeIds = (entity.getProperty("badges") == null)
        ? new LinkedHashSet<>()
        : new LinkedHashSet<String>((ArrayList<String>) entity.getProperty("badges"));

    // TODO: use badgeIds to create list of badge objects
    LinkedHashSet<Badge> badges = new LinkedHashSet<>();

    User user = new User(userId, firstName, lastName, email, phoneNumber, profilePic, 
                         badges, groupIds, interests);
    return user;
  }

  /**
   * Creates and returns a User Entity from the current User object.
   */
  public Entity toEntity() {
    Entity userEntity = new Entity("User", userId);
    userEntity.setProperty("userId", userId);
    userEntity.setProperty("firstName", firstName);
    userEntity.setProperty("lastName", lastName);
    userEntity.setProperty("email", email);
    userEntity.setProperty("phoneNumber", phoneNumber);
    userEntity.setProperty("profilePic", profilePic);
    userEntity.setProperty("badges", badges);
    userEntity.setProperty("groups", groups);
    userEntity.setProperty("interests", interests);
    return userEntity;
  }

  /**
   * Helper method for {@code fromEntity()}. Returns a list of badges given a list of badge ids.
   */
  private LinkedHashSet<Badge> getBadgeList(LinkedHashSet<String> badgeIds) { 
    
  }
}
