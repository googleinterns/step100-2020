package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
import com.google.sps.Objects.User;

import error.ErrorHandler;

@WebServlet("/search-results")
public class SearchResultsServlet extends AuthenticatedServlet {

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    System.out.println(request.getRequestURL());
    String names = request.getParameter("names");
    String[] namesSplit = names.split(",");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    List<User> users = this.getUserSuggestions(namesSplit, datastore, response);
    ServletHelper.write(response, users, "application/json");
  }

  private List<User> getUserSuggestions(
      String[] namesSplit, DatastoreService datastore, HttpServletResponse response)
      throws IOException {
    List<User> users = new ArrayList<User>();

    for (String name : namesSplit) {
      Filter propertyFilter =
          new FilterPredicate("fullName", FilterOperator.EQUAL, name.toUpperCase());
      Query query = new Query("User").setFilter(propertyFilter);
      PreparedQuery pq = datastore.prepare(query);
      for (Entity result : pq.asIterable()) {
        System.out.println(result);
        try {
          users.add(User.fromEntity(result));
        } catch (EntityNotFoundException e) {
          ErrorHandler.sendError(response, "Entity not found.");
        }
      }
    }

    return users;
  }

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    // TODO Auto-generated method stub

  }
}
