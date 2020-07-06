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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.repackaged.com.google.common.collect.Iterables;

/**
 * This servlet is called weekly to delete the top challenge suggestion so that the new weekly
 * challenge can be updated to be the next top voted suggestion following the one that has just been
 * deleted.
 *
 * @author lucyqu
 */
@WebServlet("delete-top-option")
public class DeletePollOptionServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Option").addSort("timestamp", SortDirection.ASCENDING);
    PreparedQuery results = datastore.prepare(query);
    long maxVotedId = this.getMaxVotedId(results);
    this.deleteEntity(maxVotedId, results, datastore, response);
  }

  /**
   * Sets the variables maxVotes and maxVotesId by iterating through the entities.
   *
   * @param results queried results
   * @return maxVotesId the id of option with maximum number of votes
   */
  private long getMaxVotedId(PreparedQuery results) {
    long maxVotedId = 0;
    int maxVotes = 0;
    Iterable<Entity> resultsIterable = results.asIterable();
    // Set maxVotedId to be first one in iterable of Entities
    if (Iterables.size(resultsIterable) > 0) {
      maxVotedId = results.asIterable().iterator().next().getKey().getId();
    }
    /*
     * Update maxVotes and maxVotedId by iterating through Entities, checking for
     * max votes
     */
    for (Entity entity : resultsIterable) {
      long id = entity.getKey().getId();
      List<String> votes = (ArrayList<String>) entity.getProperty("votes");
      int numVotes = (votes != null) ? votes.size() : 0;
      if (numVotes > maxVotes) {
        maxVotes = numVotes;
        maxVotedId = id;
      }
    }
    return maxVotedId;
  }

  /**
   * Deletes the option entity with the maximum number of votes.
   *
   * @param results queried results
   * @param datastore database storing information
   */
  private void deleteEntity(
      long maxVotedId,
      PreparedQuery results,
      DatastoreService datastore,
      HttpServletResponse response) {
    Key optionKey = KeyFactory.createKey("Option", maxVotedId);
    datastore.delete(optionKey);
  }
}
