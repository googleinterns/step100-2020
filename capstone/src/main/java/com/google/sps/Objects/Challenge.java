package com.google.sps.Objects;

import java.util.ArrayList;

public final class Challenge {

  private final String challengeName;
  private final long timestamp;
  private final Badge badge;
  private final ArrayList<String> usersCompleted;

  public Challenge(String challengeName, long timestamp, Badge badge,
      ArrayList<String> usersCompleted) {
    this.timestamp = timestamp;
    this.challengeName = challengeName;
    this.badge = badge;
    this.usersCompleted = usersCompleted;
  }

  public long getTimestamp() {
    return timestamp;
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

  public void addUserCompleted(String user) {
    this.usersCompleted.add(user);
  }
}
