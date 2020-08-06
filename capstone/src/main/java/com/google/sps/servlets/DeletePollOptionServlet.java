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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.sps.error.ErrorHandler;

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
    long groupId = Long.parseLong(request.getParameter("groupId"));
    List<Long> options = this.getOptionsList(groupId, request, response, datastore);
    long maxVotedId = this.getMaxVotedOption(options, response, datastore);
    if (maxVotedId != 0) {
      try {
        this.deleteEntity(maxVotedId, datastore);
        this.updateGroupOptionsList(groupId, maxVotedId, request, response, datastore);
      } catch (EntityNotFoundException e) {
        ErrorHandler.sendError(response, "Entity not found.");
        return;
      }
    } else {
      return;
    }
  }

  private List<Long> getOptionsList(
      long groupId,
      HttpServletRequest request,
      HttpServletResponse response,
      DatastoreService datastore)
      throws IOException {
    Entity entity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    List<Long> options =
        (entity.getProperty("options") == null)
            ? new ArrayList<Long>()
            : (List<Long>) entity.getProperty("options");
    return options;
  }

  private long getMaxVotedOption(
      List<Long> options, HttpServletResponse response, DatastoreService datastore)
      throws IOException {
    if (options.size() == 0) {
      return 0;
    }
    long maxVotedOptionId = options.get(0);
    int maxVotes = 0;
    for (Long optionId : options) {
      Entity optionEntity = ServletHelper.getEntityFromId(response, optionId, datastore, "Option");
      List<String> votes = (ArrayList<String>) optionEntity.getProperty("votes");
      int numVotes = (votes != null) ? votes.size() : 0;
      if (numVotes > maxVotes) {
        maxVotes = numVotes;
        maxVotedOptionId = optionId;
      }
    }
    return maxVotedOptionId;
  }

  /**
   * Deletes the option entity with the maximum number of votes.
   *
   * @param results queried results
   * @param datastore database storing information
   */
  private void deleteEntity(long maxVotedId, DatastoreService datastore)
      throws EntityNotFoundException {
    Key optionKey = KeyFactory.createKey("Option", maxVotedId);
    datastore.delete(optionKey);
  }

  private void updateGroupOptionsList(
      long groupId,
      long optionId,
      HttpServletRequest request,
      HttpServletResponse response,
      DatastoreService datastore)
      throws IOException {
    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    List<Long> options =
        (groupEntity.getProperty("options") == null)
            ? new ArrayList<Long>()
            : (List<Long>) groupEntity.getProperty("options");
    options.remove(optionId);
    groupEntity.setProperty("options", options);
    datastore.put(groupEntity);
  }
}
