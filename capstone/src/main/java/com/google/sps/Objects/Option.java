package com.google.sps.Objects;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Entity;

/**
 * Represents each option of the poll. Has text field and keeps track of users
 * who voted for each option.
 *
 * @author lucyqu
 *
 */
public final class Option {

  private final String text;
  // List of userIds of people who voted for option
  private List<String> votes;
  private final long id;

  /**
   * Constructor that takes in id of option, text, and list of user votes.
   *
   * @param text String representing option name
   */
  public Option(long id, String text, List<String> votes) {
    this.text = text;
    this.votes = votes;
    this.id = id;
  }

  /**
   * Returns text for each option.
   *
   * @return String text
   */
  public String getText() {
    return this.text;
  }

  /**
   * Add userId to list to keep track of users who voted for option.
   *
   * @param userId user id
   */
  public void addVote(String userId) {
    this.votes.add(userId);
  }

  /**
   * Get users who voted for option.
   *
   * @return list of user ids
   */
  public List<String> getVotes() {
    return this.votes;
  }

  public long getId() {
    return this.id;
  }

  public static Option fromEntity(Entity entity) {
    long id = entity.getKey().getId();
    String text = (String) entity.getProperty("text");
    List<String> votes = (ArrayList<String>) entity.getProperty("votes");
    return new Option(id, text, votes);
  }

  public Entity toEntity() {
    Entity optionEntity = new Entity("Option");
    long timestamp = System.currentTimeMillis();
    optionEntity.setProperty("text", text);
    optionEntity.setProperty("votes", this.votes);
    optionEntity.setProperty("timestamp", timestamp);
    return optionEntity;
  }
}