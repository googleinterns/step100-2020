package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import error.ErrorHandler;

@WebServlet("/mark-challenge")
public class MarkChallengeServlet extends HttpServlet {
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String challengeIdString = request.getParameter("id");
    boolean isOptionChecked = Boolean.parseBoolean(request.getParameter("checked"));
    long challengeId = Long.parseLong(challengeIdString);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    String userId = this.getUserId(response);
    Entity challengeEntity = this.getEntityFromId(response, challengeId, datastore);
    Set<String> votesSet = this.getUpdatedVotes(challengeEntity, isOptionChecked, userId);
    // Update datastore
    challengeEntity.setProperty("votes", votesSet);
    System.out.println("in mark challenge----------" + votesSet.toString());
    datastore.put(challengeEntity);
  }

  /**
   * Gets the id of the currently logged in user.
   *
   * @param response HttpServletResponse
   * @return String user id
   * @throws IOException exception thrown from send error method.
   */
  private String getUserId(HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      return userService.getCurrentUser().getUserId();
    } else {
      ErrorHandler.sendError(response, "User is not logged in.");
    }
    return "";
  }

  /**
   * Retrieves the option entity from the database based on id.
   *
   * @param response HttpServletResponse
   * @param optionId id of current option
   * @param datastore datastore holding all data
   * @return Option entity
   * @throws IOException error thrown from sendError method
   */
  private Entity getEntityFromId(
      HttpServletResponse response, long challengeId, DatastoreService datastore)
      throws IOException {
    try {
      return datastore.get(KeyFactory.createKey("Challenge", challengeId));
    } catch (EntityNotFoundException e) {
      ErrorHandler.sendError(response, "Cannot get entity from datastore");
      return null;
    }
  }

  /**
   * Updates the votes for a particular poll option. Gets an ArrayList from the database
   * representing the list of people who have voted for a particular option, which is passed in as
   * optionEntity. Converts this ArrayList to a Set and then checks whether the checkbox is checked
   * and whether the set already contains the current user id and then updates the set accordingly.
   *
   * @param optionEntity option entity from database
   * @param isOptionChecked boolean whether checkbox is checked for current option
   * @param userId id of user
   * @return set representing users who have voted for current option
   */
  private Set<String> getUpdatedVotes(
      Entity challengeEntity, boolean isOptionChecked, String userId) {
    /*
     * Using ArrayList here because datastore will only return type ArrayList.
     * Casting it to a HashSet will still have O(n) time complexity, so ArrayLists
     * seem to be the best option in this scenario.
     */
    List<String> votes =
        (challengeEntity.getProperty("votes") == null)
            ? new ArrayList<>()
            : (ArrayList<String>) challengeEntity.getProperty("votes");
    Set<String> votesSet;
    if (votes == null) {
      votesSet = new HashSet<String>();
    } else {
      votesSet = new HashSet<String>(votes);
    }
    /*
     * If checkbox is unchecked and list of votes contains user, remove user id from
     * list of votes for current option
     */
    if (!isOptionChecked && votesSet.contains(userId)) {
      votesSet.remove(userId);
    } else if (isOptionChecked && !votesSet.contains(userId)) {
      /*
       * If checkbox is checked and list of votes does not contain user, add user id
       * to list
       */
      votesSet.add(userId);
    }
    return votesSet;
  }
}
