package com.google.sps.Objects;

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
  private final LinkedHashSet<Group> groups;
  private final ArrayList<String> interests;

  public User(String userId, String firstName, String lastName, 
      String email, String phoneNumber, String profilePic,
      LinkedHashSet<Badge> badges, LinkedHashSet<Group> groups, ArrayList<String> interests) {
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

  public LinkedHashSet<Group> getGroups() {
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

  public void addGroup(Group newGroup) {
    this.groups.add(newGroup);
  }
}
