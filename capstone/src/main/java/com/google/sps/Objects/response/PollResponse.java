package com.google.sps.Objects.response;

import java.util.List;

import com.google.sps.Objects.Option;

public final class PollResponse {

  private final List<Option> options;
  // List of ids of options for which current user voted
  private final List<Long> votedOptions;
  private final String userId;

  public PollResponse(List<Option> options, List<Long> votedOptions, String userId) {
    this.options = options;
    this.votedOptions = votedOptions;
    this.userId = userId;
  }

  public List<Option> getOptions() {
    return this.options;
  }

  public List<Long> getVotedOptions() {
    return this.votedOptions;
  }
}
