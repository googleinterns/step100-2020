package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.Objects.Challenge;
import com.google.sps.Objects.Time;
import com.google.sps.Objects.response.ChallengeResponse;

@WebServlet("challenge")
public class ChallengeServlet extends AuthenticatedServlet {

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity groupEntity = ServletHelper.getGroupEntity(request, response, datastore);
    ChallengeResponse challengeResponse =
        this.buildChallengeResponse2(groupEntity, userId, datastore, response);

    //    Query query = new Query("Challenge").addSort("timestamp", SortDirection.DESCENDING);
    //    PreparedQuery results = datastore.prepare(query);
    //    ChallengeResponse challengeResponse = this.buildChallengeResponse(results, userId);
    ServletHelper.write(response, challengeResponse, "application/json");
  }

  private ChallengeResponse buildChallengeResponse2(
      Entity groupEntity, String userId, DatastoreService datastore, HttpServletResponse response)
      throws IOException {
    List<Long> challengeIds =
        (groupEntity.getProperty("challenges") == null)
            ? new ArrayList<Long>()
            : (ArrayList<Long>) groupEntity.getProperty("challenges");
    Challenge challenge = this.getMostRecentChallenge(challengeIds, datastore, response);
    if (challenge == null) {
      return null;
    }
    boolean hasUserCompleted = challenge.getHasUserCompleted(userId);
    return new ChallengeResponse(challenge, hasUserCompleted);
  }

  private Challenge getMostRecentChallenge(
      List<Long> challengeIds, DatastoreService datastore, HttpServletResponse response)
      throws IOException {
    Challenge newestChallenge = null;
    Entity newestChallengeEntity = null;
    Long newestTimestamp = 0L;

    for (Long challengeId : challengeIds) {
      // get newest challenge
      Entity entity = ServletHelper.getEntityFromId(response, challengeId, datastore, "Challenge");
      Long timestamp = (Long) entity.getProperty("timestamp");
      if (timestamp > newestTimestamp) {
        newestTimestamp = timestamp;
        newestChallengeEntity = entity;
      }
    }
    newestChallenge = Challenge.fromEntity(newestChallengeEntity);
    return newestChallenge;
  }

  //  private ChallengeResponse buildChallengeResponse(PreparedQuery results, String userId) {
  //    Challenge challenge = null;
  //    ChallengeResponse challengeResponse = null;
  //    // Check if there are challenges in database
  //    if (Iterables.size(results.asIterable()) > 0) {
  //      // Get most recent challenge in database
  //      Entity entity = results.asIterable().iterator().next();
  //      challenge = Challenge.fromEntity(entity);
  //      // Gets whether current user has completed challenge
  //      boolean hasUserCompleted = challenge.getHasUserCompleted(userId);
  //      challengeResponse = new ChallengeResponse(challenge, hasUserCompleted);
  //    }
  //    return challengeResponse;
  //  }

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String challengeName = request.getParameter("name");
    long dueDate = Challenge.getDueDate(new Time());
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
}
