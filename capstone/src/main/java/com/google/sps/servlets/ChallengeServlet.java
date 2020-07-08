package com.google.sps.servlets;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.repackaged.com.google.common.collect.Iterables;
import com.google.sps.Objects.Challenge;
import com.google.sps.Objects.response.ChallengeResponse;

@WebServlet("challenge")
public class ChallengeServlet extends AuthenticatedServlet {

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Challenge").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    Challenge challenge = null;
    ChallengeResponse challengeResponse = null;
    // Check if there are challenges in database
    if (Iterables.size(results.asIterable()) > 0) {
      // Get most recent challenge in database
      Entity entity = results.asIterable().iterator().next();
      challenge = Challenge.fromEntity(entity);
      // Gets whether current user has completed challenge
      boolean hasUserCompleted = challenge.getHasUserCompleted(userId);
      challengeResponse = new ChallengeResponse(challenge, hasUserCompleted);
    }
    ServletHelper.write(response, challengeResponse, "application/json");
  }

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String challengeName = request.getParameter("name");
    long dueDate = this.getDueDate();
    Challenge challenge =
        new Challenge(
            challengeName,
            dueDate, /* due date in milliseconds */
            null, /* badge */
            new ArrayList<String>() /* users completed */,
            0 /* id */);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(challenge.toEntity());
  }

  /**
   * Sets due date to midnight, 7 days from when challenge is posted.
   *
   * @return due date in milliseconds
   */
  private long getDueDate() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime dueDate = now.plusDays(7).withHour(23).withMinute(59).withSecond(59).withNano(0);
    return Timestamp.valueOf(dueDate).getTime();
  }
}
