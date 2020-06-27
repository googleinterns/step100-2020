package com.google.sps.Objects.response;

import java.util.List;

import com.google.sps.Objects.Option;

/**
 * Used as a wrapper class to contain information needed to convert to JSON to
 * pass to frontend. Includes a list of poll options, a list of ids for
 * checkboxes that the current user has voted for, and the id of the user.
 *
 * @author lucyqu
 *
 */
public final class PollResponse {

  private final List<Option> options;
  private final List<Long> votedOptions;
  private final String userId;

  /**
   * Constructor to set instance variables.
   *
   * @param options      List of Option objects
   * @param votedOptions Ids for the checkboxes that user has marked as checked
   * @param userId       user id
   */
  public PollResponse(List<Option> options, List<Long> votedOptions, String userId) {
    this.options = options;
    this.votedOptions = votedOptions;
    this.userId = userId;
  }

  /**
   * Gets list of Option objects.
   *
   * @return List of Option objects
   */
  public List<Option> getOptions() {
    return this.options;
  }

  /**
   * Gets the list of ids for checkboxes that user has marked as checked.
   * 
   * @return List of ids
   */
  public List<Long> getVotedOptions() {
    return this.votedOptions;
  }
}
