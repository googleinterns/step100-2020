package com.google.sps.Objects.response;

/**
 * Contains information for the response sent by LoginServlet.
 */
public class LoginResponse {

  public final String loginUrl;
  public final String logoutUrl;
  public final String email;
  public final boolean loggedIn;

  /**
   * Constructs a LoginResponse object.
   *
   * @param email String representing the email of a user
   * @param loginUrl String url user is redirected to upon logging in
   * @param logoutUrl String url user is redirected to upon logging out
   * @param loggedIn boolean representing whether User is logged in or not.
   */
  public LoginResponse(String loginUrl, String logoutUrl, String email, boolean loggedIn) {
    this.loginUrl = loginUrl;
    this.logoutUrl = logoutUrl;
    this.email = email;
    this.loggedIn = loggedIn;
  }
}
