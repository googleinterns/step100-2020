package com.google.sps.Objects.response;

import java.util.List;

import com.google.sps.Objects.Option;

public final class PollResponse {

  private final List<Option> options;
  // List of ids of options for which current user voted for
  private final List<String> votedOptions;

  public PollResponse(List<Option> options, List<String> votedOptions) {
    this.options = options;
    this.votedOptions = votedOptions;
  }

  public List<Option> getOptions() {
    return this.options;
  }

  public List<String> getVotedOptions() {
    return this.votedOptions;
  }
}
