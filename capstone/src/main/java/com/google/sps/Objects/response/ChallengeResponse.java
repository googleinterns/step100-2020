package com.google.sps.Objects.response;

import com.google.sps.Objects.Challenge;

/**
 * This class is used in the ChallengeServlet to wrap together the current challenge and whether the
 * current user has completed the challenge.
 *
 * @author lucyqu
 */
public class ChallengeResponse {

<<<<<<< HEAD
  private final Challenge challenge;
  private final boolean isCompleted;

  public ChallengeResponse(Challenge challenge, boolean isCompleted) {
    this.challenge = challenge;
    this.isCompleted = isCompleted;
=======
  private Challenge challenge;
  private boolean userHasCompleted;

  /**
   * Constructor to set the instance variables.
   *
   * @param challenge Challenge object
   * @param isCompleted boolean indicating whether user has completed challenge
   */
  public ChallengeResponse(Challenge challenge, boolean userHasCompleted) {
    this.challenge = challenge;
    this.userHasCompleted = userHasCompleted;
>>>>>>> ffe3fdc09ef24c0ae4adf994055edcda95bf80ff
  }
}
