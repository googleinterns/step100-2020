package com.google.sps.Objects.response;

import com.google.sps.Objects.Challenge;

/**
 * This class is used in the ChallengeServlet to wrap together the current challenge and whether the
 * current user has completed the challenge.
 *
 * @author lucyqu
 */
public class ChallengeResponse {

  private Challenge challenge;
  private boolean hasUserCompleted;

  /**
   * Constructor to set the instance variables.
   *
   * @param challenge Challenge object
   * @param hasUserCompleted boolean indicating whether user has completed challenge
   */
  public ChallengeResponse(Challenge challenge, boolean hasUserCompleted) {
    this.challenge = challenge;
    this.hasUserCompleted = hasUserCompleted;
  }
}
