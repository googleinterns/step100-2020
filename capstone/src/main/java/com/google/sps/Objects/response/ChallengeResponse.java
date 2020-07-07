package com.google.sps.Objects.response;

import com.google.sps.Objects.Challenge;

public class ChallengeResponse {

  private Challenge challenge;
  private boolean isCompleted;

  public ChallengeResponse(Challenge challenge, boolean isCompleted) {
    this.setChallenge(challenge);
    this.isCompleted = isCompleted;
  }

  public Challenge getChallenge() {
    return challenge;
  }

  public void setChallenge(Challenge challenge) {
    this.challenge = challenge;
  }
}
