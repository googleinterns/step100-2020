package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.Objects.Option;
import com.google.sps.Objects.comparator.OptionsComparator;
import com.google.sps.Objects.response.PollResponse;

@WebServlet("/poll")
public class PollServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String userId = "";
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn()) {
      userId = userService.getCurrentUser().getUserId();
    } else {
      this.sendError(response, "ERROR: User is not logged in");
    }
    Query query = new Query("Option").addSort("timestamp", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<Option> options = new ArrayList<Option>();
    /*
     * List to keep track of options current user has voted for so that checkboxes
     * can be marked as checked on frontend side
     */
    List<Long> votedOptions = new ArrayList<Long>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String text = (String) entity.getProperty("text");
      List<String> votes = (ArrayList<String>) entity.getProperty("votes");
      Option option = new Option(id, text, votes);
      options.add(option);
      // If current user voted for option, add to list of voted options
      if (votes != null && votes.contains(userId)) {
        votedOptions.add(id);
      }
    }
    // Sort list of options based on number of votes
    Collections.sort(options, new OptionsComparator());
    PollResponse pollResponse = new PollResponse(options, votedOptions, userId);
    String json = new Gson().toJson(pollResponse);
    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String text = request.getParameter("text");
    Entity optionEntity = new Entity("Option");
    long timestamp = System.currentTimeMillis();
    optionEntity.setProperty("text", text);
    optionEntity.setProperty("votes", new ArrayList<String>());
    optionEntity.setProperty("timestamp", timestamp);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
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
}
