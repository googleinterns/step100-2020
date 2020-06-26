package com.google.sps.Objects;

import java.util.List;

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
    return votes;
  }
}