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

@WebServlet("/update-votes")
public class UpdateVotesServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get id of changed checkbox
    String optionIdString = request.getParameter("id");
    boolean isOptionChecked = Boolean.parseBoolean(request.getParameter("checked"));
    long optionId = this.parseToLong(response, optionIdString);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    String userId = this.getUserId(response);
    Entity optionEntity = this.getEntityFromId(response, optionId, datastore);
    Set<String> votesSet = this.getUpdatedVotes(optionEntity, isOptionChecked, userId);

    // Update datastore
    optionEntity.setProperty("votes", votesSet);
    datastore.put(optionEntity);
  }

  /**
   * Handles error for Java Servlet and displays that something went wrong.
   *
   * @param response    HttpServletResponse
   * @param errorString error message
   * @throws IOException exception thrown when cannot write to file
   */
  private void sendError(HttpServletResponse response, String errorString) throws IOException {
    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Cannot parse to long.");
    response.getWriter().print("<html><head><title>Oops an error happened!</title></head>");
    response.getWriter().print("<body>Something bad happened uh-oh!</body>");
    response.getWriter().println("</html>");
  }

  private String getUserId(HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      return userService.getCurrentUser().getUserId();
    } else {
      this.sendError(response, "User is not logged in.");
    }
    return "";
  }

  private long parseToLong(HttpServletResponse response, String optionIdString) throws IOException {
    try {
      return Long.parseLong(optionIdString);
    } catch (NumberFormatException e) {
      this.sendError(response, "Cannot parse to long.");
      return 0;
    }
  }

  private Entity getEntityFromId(HttpServletResponse response, long optionId,
      DatastoreService datastore) throws IOException {
    try {
      return datastore.get(KeyFactory.createKey("Option", optionId));
    } catch (EntityNotFoundException e) {
      this.sendError(response, "Cannot get entity from datastore");
      return null;
    }
  }

  private Set<String> getUpdatedVotes(Entity optionEntity, boolean isOptionChecked, String userId) {
    /*
     * Using ArrayList here because datastore will only return type ArrayList.
     * Casting it to a HashSet will still have O(n) time complexity, so ArrayLists
     * seem to be the best option in this scenario.
     */
    List<String> votes = (ArrayList<String>) optionEntity.getProperty("votes");
    Set<String> votesSet;
    /*
     * If current option does not have any votes, initialize ArrayList.
     */
    if (votes == null) {
      votes = new ArrayList<String>();
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
