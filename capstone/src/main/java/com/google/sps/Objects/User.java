package com.google.sps.Objects;

import java.util.ArrayList;

public final class User {

  private final String userId;
  private final String name;
  private final String firstname;
  private final String email;
  private final ArrayList<Badge> badges;
  private final ArrayList<String> groups;
  private final ArrayList<String> interests;

  public User(String userId, String firstname, String lastname, String email, 
      ArrayList<Badge> badges, ArrayList<String> groups, ArrayList<String> interests) {
    this.userId = userId;
    this.name = firstname + " " + lastname;
    this.firstname = firstname;
    this.email = email;
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

  public String getEmail() {
    return email;
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
