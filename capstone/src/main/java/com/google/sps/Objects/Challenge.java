package com.google.sps.Objects;

import java.util.ArrayList;

import com.google.appengine.api.datastore.Entity;

public final class Challenge {

  private final String challengeName;
  private final long dueDate;
  private final Badge badge;
  private final ArrayList<String> usersCompleted;

  public Challenge(String challengeName, long dueDate, Badge badge,
      ArrayList<String> usersCompleted) {
    this.dueDate = dueDate;
    this.challengeName = challengeName;
    this.badge = badge;
    this.usersCompleted = usersCompleted;
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

  public ArrayList<String> usersCompleted() {
    return usersCompleted;
  }

  public void addUserCompleted(String userId) {
    this.usersCompleted.add(userId);
  }

  public static Challenge fromEntity(Entity entity) {
    String challengeName = (String) entity.getProperty("name");
    long dueDate = (long) entity.getProperty("dueDate");
    ArrayList<String> usersCompleted = (ArrayList<String>) entity.getProperty("usersCompleted");
    // setting badge as null for now
    return new Challenge(challengeName, dueDate, null, usersCompleted);
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
