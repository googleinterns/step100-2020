package com.google.sps.search.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.sps.Objects.User;
import com.google.sps.error.ErrorHandler;
import com.google.sps.graph.Dijkstra;
import com.google.sps.graph.UserEdge;
import com.google.sps.graph.UserVertex;
import com.google.sps.servlets.AuthenticatedServlet;
import com.google.sps.servlets.ServletHelper;

@WebServlet("/search-results")
public class SearchResultsServlet extends AuthenticatedServlet {

  private static final double COMPLETE_MATCH = 10;

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String names = request.getParameter("names");
    String searchString = request.getParameter("searchString");
    String[] namesSplit = names.split(",");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<User> users = this.getUserSuggestions(namesSplit, searchString, datastore, response);
    ServletHelper.write(response, users, "application/json");
  }

  private List<User> getUserSuggestions(
      String[] namesSplit,
      String searchString,
      DatastoreService datastore,
      HttpServletResponse response)
      throws IOException {
    List<User> users = new ArrayList<User>();
    Map<User, Double> namesScore = new LinkedHashMap<User, Double>();
    UserService userService = UserServiceFactory.getUserService();
    String userId = "";
    if (userService.isUserLoggedIn()) {
      userId = userService.getCurrentUser().getUserId();
    }

    for (String name : namesSplit) {
      Filter propertyFilter =
          new FilterPredicate("fullName", FilterOperator.EQUAL, name.toUpperCase());
      Query query = new Query("User").setFilter(propertyFilter);
      PreparedQuery pq = datastore.prepare(query);
      for (Entity result : pq.asIterable()) {
        try {
          users.add(User.fromEntity(result));
          namesScore = this.runDijkstra(namesScore, userId, searchString, User.fromEntity(result));
        } catch (EntityNotFoundException e) {
          ErrorHandler.sendError(response, "Entity not found.");
        }
      }
    }

    return this.sortUsers(namesScore);
  }

  private Map<User, Double> runDijkstra(
      Map<User, Double> namesScore, String currUserId, String searchString, User destUser) {
    Dijkstra<UserVertex, UserEdge> dijkstra = new Dijkstra<UserVertex, UserEdge>();
    UserVertex srcVertex = new UserVertex(currUserId);
    UserVertex destVertex = new UserVertex(destUser.getUserId());
    double distance = dijkstra.runDijkstra(srcVertex, destVertex).getDistance();

    if (!namesScore.containsKey(destUser)) {
      namesScore.put(destUser, 1 / distance);
    }

    // If search string is a complete match with result name, increment score
    String destUserName = destUser.getFirstName() + " " + destUser.getLastName();
    if (searchString.toUpperCase().equals(destUserName.toUpperCase())) {
      namesScore.put(destUser, namesScore.get(destUser) + COMPLETE_MATCH);
    }

    return namesScore;
  }

  private List<User> sortUsers(Map<User, Double> namesScore) {
    List<User> sortedUsers = new ArrayList<User>();
    List<Map.Entry<User, Double>> entries =
        new ArrayList<Map.Entry<User, Double>>(namesScore.entrySet());

    Collections.sort(
        entries,
        new Comparator<Map.Entry<User, Double>>() {
          @Override
          public int compare(Map.Entry<User, Double> a, Map.Entry<User, Double> b) {
            return Double.compare(b.getValue(), a.getValue());
          }
        });

    for (Map.Entry<User, Double> entry : entries) {
      sortedUsers.add(entry.getKey());
    }

    return sortedUsers;
  }

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    // TODO Auto-generated method stub

  }
}
