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
import com.google.gson.Gson;
import com.google.sps.Objects.Challenge;

@WebServlet("challenge")
public class ChallengeServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("Challenge").addSort("dueDate", SortDirection.DESCENDING);
    PreparedQuery results = datastore.prepare(query);
    // Get most recent challenge in database
    Entity entity = results.asIterable().iterator().next();
    String challengeName = (String) entity.getProperty("name");
    long dueDate = (long) entity.getProperty("dueDate");
    ArrayList<String> usersCompleted = (ArrayList<String>) entity.getProperty("usersCompleted");
    // setting badge as null for now
    Challenge challenge = new Challenge(challengeName, dueDate, null, usersCompleted);
    String json = new Gson().toJson(challenge);
    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String challengeName = request.getParameter("name");
    LocalDateTime dueDate = this.getDueDate(LocalDateTime.now());
    long dueDateMillis = Timestamp.valueOf(dueDate).getTime();
    Entity challengeEntity = new Entity("Challenge");
    challengeEntity.setProperty("name", challengeName);
    challengeEntity.setProperty("dueDate", dueDateMillis);
    challengeEntity.setProperty("usersCompleted", new ArrayList<String>());
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(challengeEntity);
  }

  /**
   * Sets due date 7 days from when challenge is posted and at midnight.
   *
   * @param d current date time
   * @return date time in a week from current date time
   */
  private LocalDateTime getDueDate(LocalDateTime d) {
    return d.plusDays(7).withHour(23).withMinute(59).withSecond(59).withNano(0);
  }
}
