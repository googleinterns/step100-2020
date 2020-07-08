package com.google.sps.Objects.response;

import com.google.sps.Objects.Challenge;

public class ChallengeResponse {

  private Challenge challenge;
  private boolean userHasCompleted;

  public ChallengeResponse(Challenge challenge, boolean userHasCompleted) {
    this.setChallenge(challenge);
    this.userHasCompleted = userHasCompleted;
  }

  public Challenge getChallenge() {
    return challenge;
  }

  public void setChallenge(Challenge challenge) {
    this.challenge = challenge;
  }
}
