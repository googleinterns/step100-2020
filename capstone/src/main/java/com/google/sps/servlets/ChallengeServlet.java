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
    long groupId = Long.parseLong(request.getParameter("groupId"));
    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    ChallengeResponse challengeResponse =
        this.buildChallengeResponse(groupEntity, userId, datastore, response, request);
    ServletHelper.write(response, challengeResponse, "application/json");
  }

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
    Entity challengeEntity = challenge.toEntity();
    datastore.put(challengeEntity);
    this.updateChallengesList(request, response, datastore, challengeEntity);
  }

  private ChallengeResponse buildChallengeResponse(
      Entity groupEntity,
      String userId,
      DatastoreService datastore,
      HttpServletResponse response,
      HttpServletRequest request)
      throws IOException {
    if (groupEntity == null) {
      return null;
    }
    List<Long> challengeIds =
        (groupEntity.getProperty("challenges") == null)
            ? new ArrayList<Long>()
            : (ArrayList<Long>) groupEntity.getProperty("challenges");
    Challenge challenge = this.getMostRecentChallenge(challengeIds, datastore, response, request);
    if (challenge == null) {
      return null;
    }
    boolean hasUserCompleted = challenge.getHasUserCompleted(userId);
    return new ChallengeResponse(challenge, hasUserCompleted);
  }

  private Challenge getMostRecentChallenge(
      List<Long> challengeIds,
      DatastoreService datastore,
      HttpServletResponse response,
      HttpServletRequest request)
      throws IOException {
    if (challengeIds.size() == 0) {
      return null;
    } else {
      long mostRecentChallengeId = challengeIds.get(challengeIds.size() - 1);
      Entity newestChallengeEntity =
          ServletHelper.getEntityFromId(response, mostRecentChallengeId, datastore, "Challenge");
      if (newestChallengeEntity == null) {
        return null;
      }
      // if challenge already passed
      if ((long) newestChallengeEntity.getProperty("dueDate") < System.currentTimeMillis()) {
        return null;
      } else {
        return Challenge.fromEntity(newestChallengeEntity);
      }
    }
  }

  private void updateChallengesList(
      HttpServletRequest request,
      HttpServletResponse response,
      DatastoreService datastore,
      Entity challengeEntity)
      throws IOException {
    long groupId = Long.parseLong(request.getParameter("groupId"));
    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    List<Long> challenges =
        (groupEntity.getProperty("challenges") == null)
            ? new ArrayList<Long>()
            : (List<Long>) groupEntity.getProperty("challenges");
    challenges.add(challengeEntity.getKey().getId());
    groupEntity.setProperty("challenges", challenges);
    datastore.put(groupEntity);
  }
}
