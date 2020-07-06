package com.google.sps.servlets;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
import com.google.appengine.repackaged.com.google.common.collect.Iterables;
import com.google.sps.Objects.Challenge;

@WebServlet("challenge")
public class ChallengeServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Challenge").addSort("timestamp", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    String json = "";
    // Check if there are challenges in database
    if (Iterables.size(results.asIterable()) > 0) {
      // Get most recent challenge in database
      Entity entity = results.asIterable().iterator().next();
      Challenge challenge = Challenge.fromEntity(entity);
      ServletHelper.write(response, challenge, "application/json");

    } else {
      ServletHelper.write(response, null, "application/json");
    }
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String challengeName = request.getParameter("name");
    LocalDateTime dueDate = this.getDueDate(LocalDateTime.now());
    long dueDateMillis = Timestamp.valueOf(dueDate).getTime();
    Challenge challenge =
        new Challenge(challengeName, dueDateMillis, null, new ArrayList<String>());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(challenge.toEntity());
  }

  /**
   * Sets due date to midnight, 7 days from when challenge is posted.
   *
   * @param d current date time
   * @return date time in a week from current date time
   */
  private LocalDateTime getDueDate(LocalDateTime d) {
    return d.plusDays(7).withHour(23).withMinute(59).withSecond(59).withNano(0);
  }
}
