package com.google.sps.Objects;

import java.util.ArrayList;

public final class User {

  private final String userId;
  private String name;
  private String firstName;
  private String lastName;
  private final String email;
  private String phoneNumber;
  private final ArrayList<Badge> badges;
  private final ArrayList<Group> groups;
  private final ArrayList<String> interests;

  public User(String userId, String firstName, String lastName, String email) {
    this.userId = userId;
    this.name = firstName + " " + lastName;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.badges = new ArrayList<Badge>();
    this.groups = new ArrayList<Group>();
    this.interests = new ArrayList<String>();
  }

  public User(String userId, String firstName, String lastName, String email, String phoneNumber,
      ArrayList<Badge> badges, ArrayList<Group> groups, ArrayList<String> interests) {
    this.userId = userId;
    this.name = firstName + " " + lastName;
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.phoneNumber = phoneNumber;
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

  public ArrayList<Badge> getBadges() {
    return badges;
  }

  public ArrayList<Group> getGroups() {
    return groups;
  }

  public ArrayList<String> getInterests() {
    return interests;
  }

  public void setFirstName (String firstName) {
    this.firstName = firstName;
    this.name = firstName + " " + getLastName();
  }

  public void setLastName (String lastName) {
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
    // Iterate through list, checking if Group already exists to ensure uniqueness.
    // Could have used a HashSet here, but HashSets are not directly returned by Datastore,
    // negating the search time complexity benefits.
    if (!this.groups.contains(newGroup)) {
      this.groups.add(newGroup);
    }
  }
}
