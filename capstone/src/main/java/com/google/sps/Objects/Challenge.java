package com.google.sps.Objects;

import java.util.ArrayList;

import com.google.appengine.api.datastore.Entity;

public final class Challenge {

  private final String challengeName;
  private final long dueDate;
  private final Badge badge;
  private final ArrayList<String> usersCompleted;
  private final long id;

  public Challenge(
      String challengeName, long dueDate, Badge badge, ArrayList<String> usersCompleted, long id) {
    this.dueDate = dueDate;
    this.challengeName = challengeName;
    this.badge = badge;
    this.usersCompleted = usersCompleted;
    this.id = id;
  }

  public long getDueDate() {
    return dueDate;
  }

  public String getChallengeName() {
    return challengeName;
  }

  public Badge getBadge() {
    return badge;
  }

  public ArrayList<String> getUsersCompleted() {
    return usersCompleted;
  }

  public void addCompletedUser(String userId) {
    this.usersCompleted.add(userId);
  }

  public boolean getIsCompleted(String userId) {
    if (this.usersCompleted == null) {
      return false;
    } else if (this.usersCompleted.contains(userId)) {
      return true;
    }
    return false;
  }

  public static Challenge fromEntity(Entity entity) {
    String challengeName = (String) entity.getProperty("name");
    long dueDate = (long) entity.getProperty("dueDate");
    long id = entity.getKey().getId();
    ArrayList<String> usersCompleted = (ArrayList<String>) entity.getProperty("usersCompleted");
    System.out.println("in challenge class, from entity " + usersCompleted);
    // setting badge as null for now
    return new Challenge(challengeName, dueDate, null, usersCompleted, id);
  }

  public Entity toEntity() {
    Entity challengeEntity = new Entity("Challenge");
    challengeEntity.setProperty("name", this.challengeName);
    challengeEntity.setProperty("dueDate", this.dueDate);
    challengeEntity.setProperty("usersCompleted", this.usersCompleted);
    challengeEntity.setProperty("timestamp", System.currentTimeMillis());
    // not setting badge for now
    return challengeEntity;
  }
}
