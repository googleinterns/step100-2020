package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.sps.Objects.Option;
import com.google.sps.Objects.comparator.OptionsComparator;
import com.google.sps.Objects.response.PollResponse;

@WebServlet("/poll")
public class PollServlet extends AuthenticatedServlet {

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity groupEntity = ServletHelper.getGroupEntity(request, response, datastore);
    PollResponse pollResponse = this.buildPollResponse2(groupEntity, userId, response, datastore);

    //    Query query = new Query("Option").addSort("timestamp", SortDirection.ASCENDING);
    //    PreparedQuery results = datastore.prepare(query);
    //    PollResponse pollResponse = this.buildPollResponse(results, userId);
    ServletHelper.write(response, pollResponse, "application/json");
  }

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String text = request.getParameter("text");
    Option option = new Option(0, text, new ArrayList<String>());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Entity optionEntity = option.toEntity();
    datastore.put(optionEntity);
    this.updateGroupData(request, response, datastore, optionEntity);
  }

  private void updateGroupData(
      HttpServletRequest request,
      HttpServletResponse response,
      DatastoreService datastore,
      Entity optionEntity)
      throws IOException {
    Entity entity = ServletHelper.getGroupEntity(request, response, datastore);
    List<Long> options =
        (entity.getProperty("options") == null)
            ? new ArrayList<Long>()
            : (List<Long>) entity.getProperty("options");
    options.add(optionEntity.getKey().getId());
    entity.setProperty("options", options);
    datastore.put(entity);
  }

  private PollResponse buildPollResponse2(
      Entity groupEntity, String userId, HttpServletResponse response, DatastoreService datastore)
      throws IOException {
    ArrayList<Long> optionIds =
        (groupEntity.getProperty("options") == null)
            ? new ArrayList<Long>()
            : (ArrayList<Long>) groupEntity.getProperty("options");
    List<Option> options = new ArrayList<Option>();
    List<Long> votedOptions = new ArrayList<Long>();

    for (Long optionId : optionIds) {
      Entity entity = ServletHelper.getEntityFromId(response, optionId, datastore, "Option");
      Option option = Option.fromEntity(entity);
      List<String> votes = option.getVotes();
      long id = option.getId();
      options.add(option);
      // If current user voted for option, add to list of voted options
      if (votes != null && votes.contains(userId)) {
        votedOptions.add(id);
      }
    }
    // Sort list of options based on number of votes
    Collections.sort(options, new OptionsComparator());
    return new PollResponse(options, votedOptions, userId);
  }

  //  /**
  //   * Builds a PollResponse object by populating two ArrayLists, one that holds all options in a
  // poll
  //   * and the other containing the ids of options for which the user has voted.
  //   *
  //   * @param results query results
  //   * @param userId user id
  //   * @return PollResponse object
  //   */
  //  private PollResponse buildPollResponse(PreparedQuery results, String userId) {
  //    // All options in a poll
  //    List<Option> options = new ArrayList<Option>();
  //    /*
  //     * List to keep track of options current user has voted for so that checkboxes
  //     * can be marked as checked on frontend side
  //     */
  //    List<Long> votedOptions = new ArrayList<Long>();
  //    for (Entity entity : results.asIterable()) {
  //      Option option = Option.fromEntity(entity);
  //      List<String> votes = option.getVotes();
  //      long id = option.getId();
  //      options.add(option);
  //      // If current user voted for option, add to list of voted options
  //      if (votes != null && votes.contains(userId)) {
  //        votedOptions.add(id);
  //      }
  //    }
  //    // Sort list of options based on number of votes
  //    Collections.sort(options, new OptionsComparator());
  //    return new PollResponse(options, votedOptions, userId);
  //  }
}
