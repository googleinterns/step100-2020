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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

@WebServlet("delete-top-option")
public class DeletePollOptionServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Option").addSort("timestamp", SortDirection.ASCENDING);
    ;
    PreparedQuery results = datastore.prepare(query);
    int maxVotes = 0;
    long maxVotesId = 0;
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      List<String> votes = (ArrayList<String>) entity.getProperty("votes");
      int numVotes;
      if (votes != null) {
        numVotes = votes.size();
      } else {
        numVotes = 0;
      }
      if (numVotes > maxVotes) {
        maxVotes = numVotes;
        maxVotesId = id;
      }
    }
    for (Entity optionEntity : results.asIterable()) {
      if (optionEntity.getKey().getId() == maxVotesId) {
        datastore.delete(optionEntity.getKey());
        break;
      }
    }
  }
}
