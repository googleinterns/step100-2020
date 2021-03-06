package com.google.sps.Objects;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

public final class User {

  private final String userId;
  private String firstName;
  private String lastName;
  private String fullName;
  private String email;
  private String phoneNumber;
  private String profilePic;
  private String address;
  private double longitude;
  private double latitude;
  private final LinkedHashSet<Badge> badges;
  private final LinkedHashSet<Long> groups;
  private ArrayList<String> interests;

  public User(
      String userId,
      String firstName,
      String lastName,
      String email,
      String phoneNumber,
      String profilePic,
      String address,
      double latitude,
      double longitude,
      LinkedHashSet<Badge> badges,
      LinkedHashSet<Long> groups,
      ArrayList<String> interests) {
    this.userId = userId;
    this.firstName = firstName;
    this.lastName = lastName;
    this.fullName = firstName.toUpperCase() + " " + lastName.toUpperCase();
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.profilePic = profilePic;
    this.address = address;
    this.longitude = longitude;
    this.latitude = latitude;
    this.badges = badges;
    this.groups = groups;
    this.interests = interests;
  }

  public String getUserId() {
    return userId;
  }

  public String getName() {
    return fullName;
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

  public String getAddress() {
    return address;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setProfilePic(String pic) {
    this.profilePic = pic;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
    this.fullName = firstName.toUpperCase() + " " + getLastName().toUpperCase();
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
    this.fullName = getFirstName().toUpperCase() + " " + lastName.toUpperCase();
  }

  public void setPhoneNumber(String newPhoneNumber) {
    this.phoneNumber = newPhoneNumber;
  }

  public void setEmail(String newEmail) {
    this.email = newEmail;
  }

  public void setInterests(ArrayList<String> interests) {
    this.interests = interests;
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

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  /*
   * Overrides the equals() method to effectively compare two User objects.
   */
  @Override
  public boolean equals(Object other) {
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof User)) return false;
    User user = (User) other;
    return userId.equals(user.userId)
        && firstName.equals(user.firstName)
        && lastName.equals(user.lastName)
        && email.equals(user.email)
        && phoneNumber.equals(user.phoneNumber)
        && profilePic.equals(user.profilePic)
        && address.equals(user.address)
        && latitude == user.latitude
        && longitude == user.longitude
        && interests.containsAll(user.interests)
        && user.interests.containsAll(interests)
        && groups.containsAll(user.groups)
        && user.groups.containsAll(groups)
        && badges.containsAll(user.badges)
        && user.badges.containsAll(badges);
  }

  /** Creates and returns a User object given a user Entity. */
  public static User fromEntity(Entity entity) throws EntityNotFoundException {
    String userId = (String) entity.getProperty("userId");
    String firstName = (String) entity.getProperty("firstName");
    String lastName = (String) entity.getProperty("lastName");
    String email = (String) entity.getProperty("email");
    String phoneNumber = (String) entity.getProperty("phoneNumber");
    String profilePic = (String) entity.getProperty("profilePic");
    String address = (String) entity.getProperty("address");
    double latitude;
    double longitude;
    if (entity.getProperty("latitude") == null || entity.getProperty("longitude") == null) {
      latitude = 0;
      longitude = 0;
    } else {
      latitude = (double) entity.getProperty("latitude");
      longitude = (double) entity.getProperty("longitude");
    }
    ArrayList<String> interests =
        (entity.getProperty("interests") == null)
            ? new ArrayList<>()
            : (ArrayList<String>) entity.getProperty("interests");
    LinkedHashSet<Long> groupIds =
        (entity.getProperty("groups") == null)
            ? new LinkedHashSet<>()
            : new LinkedHashSet<Long>((ArrayList<Long>) entity.getProperty("groups"));

    LinkedHashSet<Long> badgeIds =
        (entity.getProperty("badges") == null)
            ? new LinkedHashSet<>()
            : new LinkedHashSet<Long>((ArrayList<Long>) entity.getProperty("badges"));

    LinkedHashSet<Badge> badges = getBadgeList(badgeIds);

    User user =
        new User(
            userId,
            firstName,
            lastName,
            email,
            phoneNumber,
            profilePic,
            address,
            latitude,
            longitude,
            badges,
            groupIds,
            interests);
    return user;
  }

  /** Creates and returns a User Entity from the current User object. */
  public Entity toEntity() {
    Entity userEntity = new Entity("User", userId);
    userEntity.setProperty("userId", userId);
    userEntity.setProperty("firstName", firstName);
    userEntity.setProperty("lastName", lastName);
    userEntity.setProperty("fullName", fullName.toUpperCase());
    userEntity.setProperty("email", email);
    userEntity.setProperty("phoneNumber", phoneNumber);
    userEntity.setProperty("profilePic", profilePic);
    userEntity.setProperty("address", address);
    userEntity.setProperty("latitude", latitude);
    userEntity.setProperty("longitude", longitude);
    userEntity.setProperty("badges", badges);
    userEntity.setProperty("groups", groups);
    userEntity.setProperty("interests", interests);
    return userEntity;
  }

  /** Helper method for {@code fromEntity()}. Returns a list of badges given a list of badge ids. */
  private static LinkedHashSet<Badge> getBadgeList(LinkedHashSet<Long> badgeIds)
      throws EntityNotFoundException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    LinkedHashSet<Badge> badges = new LinkedHashSet<>();
    for (long badgeId : badgeIds) {
      Key badgeKey = KeyFactory.createKey("Badge", badgeId);
      Entity badgeEntity = null;
      try {
        badgeEntity = datastore.get(badgeKey);
      } catch (EntityNotFoundException e) {
        throw new EntityNotFoundException(badgeKey);
      }
      badges.add(Badge.fromEntity(badgeEntity));
    }
    return badges;
  }
}
