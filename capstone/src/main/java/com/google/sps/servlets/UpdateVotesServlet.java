package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

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

    // Get entity from datastore based on id
    Entity optionEntity = null;
    try {
      optionEntity = datastore.get(KeyFactory.createKey("Option", optionId));
    } catch (EntityNotFoundException e) {
      System.err.println("ERROR: Could not get entity from datastore");
      return;
    }

    ArrayList<String> votes = (ArrayList<String>) optionEntity.getProperty("votes");
    if (votes != null) {
      // If user checked checkbox, add user id to list of votes for current option
      if (checked.equals("true")) {
        votes.add("100");
      } else {
        // If user unchecked checkbox, remove user from list of votes
        votes.remove("100");
      }
    } else {
      /*
       * If current option does not have any votes, initialize ArrayList and add user
       * id to list
       */
      votes = new ArrayList<String>();
      votes.add("100");
    }

    // Update datastore
    optionEntity.setProperty("votes", votes);
    datastore.put(optionEntity);
  }
}
