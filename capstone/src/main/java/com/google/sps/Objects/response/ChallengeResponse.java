package com.google.sps.Objects.response;

import com.google.sps.Objects.Challenge;

public class ChallengeResponse {

  private final Challenge challenge;
  private final boolean isCompleted;

  public ChallengeResponse(Challenge challenge, boolean isCompleted) {
    this.challenge = challenge;
    this.isCompleted = isCompleted;
  }
}
