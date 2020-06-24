package com.google.sps.Objects;

import java.util.ArrayList;

public final class User {

  private final String userId;
  private final String name;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String phoneNumber;
  private final ArrayList<Badge> badges;
  private final ArrayList<String> groups;
  private final ArrayList<String> interests;

  public User(String userId, String firstName, String lastName, String email) {
    this.userId = userId;
    this.name = firstName + " " + lastName;
    this.firstName = firstname;
    this.lastName = lastName;
    this.email = email;
  }

  public User(String userId, String firstName, String lastName, String email, String phoneNumber,
      ArrayList<Badge> badges, ArrayList<String> groups, ArrayList<String> interests) {
    this.userId = userId;
    this.name = firstName + " " + lastName;
    this.firstName = firstname;
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

  public ArrayList<String> getGroups() {
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

  /* Adds a unique groupId string to the user's list of groups. */
  public void addGroup(String newGroupId) {
    this.groups.add(newGroupId);
  }
}
