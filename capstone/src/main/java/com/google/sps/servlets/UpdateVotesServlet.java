package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    // Get whether checkbox is currently checked
    String checked = request.getParameter("checked");
    long optionId;

    try {
      optionId = Long.parseLong(optionIdString);
    } catch (NumberFormatException e) {
      System.err.println("ERROR: Failed to parse to long");
      return;
    }

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    // get userId of current logged in user
    String userId = "";
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      userId = userService.getCurrentUser().getUserId();
    } else {
      System.err.println("ERROR: User is not logged in");
    }

    // Get entity from datastore based on id
    Entity optionEntity = null;
    try {
      optionEntity = datastore.get(KeyFactory.createKey("Option", optionId));
    } catch (EntityNotFoundException e) {
      System.err.println("ERROR: Could not get entity from datastore");
      return;
    }

    /*
     * Using ArrayList here because datastore will only return type ArrayList.
     * Casting it to a HashSet will still have O(n) time complexity, so ArrayLists
     * seem to be the best option in this scenario.
     */
    List<String> votes = (ArrayList<String>) optionEntity.getProperty("votes");
    /*
     * If current option does not have any votes, initialize ArrayList.
     */
    if (votes == null) {
      votes = new ArrayList<String>();
    }
    /*
     * If checkbox is unchecked and list of votes contains user, remove user id from
     * list of votes for current option
     */
    if (checked.equals("false") && votes.contains(userId)) {
      votes.remove(userId);
    } else if (checked.equals("true") && !votes.contains(userId)) {
      /*
       * If checkbox is checked and list of votes does not contain user, add user id
       * to list
       */
      votes.add(userId);
    }
    // Update datastore
    optionEntity.setProperty("votes", votes);
    datastore.put(optionEntity);
  }
}
