package com.google.sps.Objects;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Entity;

/**
 * Represents the current challenge in a group.
 *
 * @author lucyqu
 */
public final class Challenge {

  private final String challengeName;
  private final long dueDate;
  private final Badge badge;
  private final List<String> usersCompleted;
  private final long id;

  /**
   * Constructor to set the instance variables.
   *
   * @param challengeName name of challenge
   * @param dueDate due date of challenge
   * @param badge badge for challenge
   * @param usersCompleted user ids of users who have completed challenge
   * @param id id of challenge
   */
  public Challenge(
      String challengeName, long dueDate, Badge badge, List<String> usersCompleted, long id) {
    this.dueDate = dueDate;
    this.challengeName = challengeName;
    this.badge = badge;
    this.usersCompleted = usersCompleted;
    this.id = id;
  }

  /**
   * Gets the name of the challenge
   *
   * @return string
   */
  public String getChallengeName() {
    return challengeName;
  }

  /**
   * Gets the badge object for this challenge.
   *
   * @return Badge
   */
  public Badge getBadge() {
    return badge;
  }

  /**
   * Gets the user ids of users who have completed the challenge.
   *
   * @return List of user ids
   */
  public List<String> getUsersCompleted() {
    return usersCompleted;
  }

  /**
   * Adds user to ArrayList of users who have completed the challenge/
   *
   * @param userId id of user.
   */
  public void addCompletedUser(String userId) {
    this.usersCompleted.add(userId);
  }

  /**
   * Returns whether userId is contained in ArrayList of users who have completed the challenge.
   *
   * @param userId id of user
   * @return boolean
   */
  public boolean getHasUserCompleted(String userId) {
    if (this.usersCompleted == null) {
      return false;
    } else {
      return this.usersCompleted.contains(userId);
    }
  }

  /**
   * Converts Entity to Challenge.
   *
   * @param entity Entity from database
   * @return Challenge object
   */
  public static Challenge fromEntity(Entity entity) {
    String challengeName = (String) entity.getProperty("name");
    long dueDate = (long) entity.getProperty("dueDate");
    long id = entity.getKey().getId();
    List<String> usersCompleted =
        (entity.getProperty("votes") == null)
            ? new ArrayList<String>()
            : (ArrayList<String>) entity.getProperty("votes");
    // setting badge as null for now
    return new Challenge(challengeName, dueDate, null, usersCompleted, id);
  }

  /**
   * Converts Challenge object to Entity.
   *
   * @return Entity object
   */
  public Entity toEntity() {
    Entity challengeEntity = new Entity("Challenge");
    challengeEntity.setProperty("name", this.challengeName);
    challengeEntity.setProperty("dueDate", this.dueDate);
    challengeEntity.setProperty("votes", this.usersCompleted);
    challengeEntity.setProperty("timestamp", System.currentTimeMillis());
    // not setting badge for now
    return challengeEntity;
  }

  /**
   * Sets due date to midnight, 7 days from when challenge is posted.
   *
   * @return due date in milliseconds
   */
  public static long getDueDate(Time time) {
    return time.getDueDate();
  }
}
