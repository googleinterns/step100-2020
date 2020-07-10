package com.google.sps.Objects.response;

import java.util.List;

import com.google.sps.Objects.Option;

/**
 * Wrapper class that consists of two ArrayLists. One ArrayList consists of all options in the poll
 * on the page and the other ArrayList consists of the ids for options that the current user has
 * voted for.
 *
 * @author lucyqu
 */
public class OptionsAndUserVotedOptions {

  // All Options in the poll
  private List<Option> options;
  // Ids for options that User has voted for
  private List<Long> votedOptions;

  /**
   * Constructor to set instance variables.
   *
   * @param options
   * @param votedOptions
   */
  public OptionsAndUserVotedOptions(List<Option> options, List<Long> votedOptions) {
    this.options = options;
    this.votedOptions = votedOptions;
  }

  /**
   * Gets the list of options in the poll.
   *
   * @return list of Option objects.
   */
  public List<Option> getOptions() {
    return this.options;
  }

  /**
   * Gets list of ids of options that user has voted for.
   *
   * @return list of ids
   */
  public List<Long> getVotedOptions() {
    return this.votedOptions;
  }
}
