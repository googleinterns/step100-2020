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
  private boolean isCompleted;

  /**
   * Constructor to set the instance variables.
   *
   * @param challenge Challenge object
   * @param isCompleted boolean indicating whether user has completed challenge
   */
  public ChallengeResponse(Challenge challenge, boolean isCompleted) {
    this.challenge = challenge;
    this.isCompleted = isCompleted;
  }
}
