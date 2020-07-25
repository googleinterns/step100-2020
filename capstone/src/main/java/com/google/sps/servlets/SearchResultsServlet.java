package com.google.sps.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;

@WebServlet("/search-results")
public class SearchResultsServlet extends AuthenticatedServlet {

  @Override
  public void doGet(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    String name = request.getParameter("name");
    String[] split = name.split(" ");

    String firstName = split[0].toUpperCase();
    String lastName = split[1].toUpperCase();

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Filter propertyFilter = new FilterPredicate("firstName", FilterOperator.EQUAL, firstName);
    Query query = new Query("User").setFilter(propertyFilter);
    PreparedQuery pq = datastore.prepare(query);
    for (Entity result : pq.asIterable()) {
      System.out.println(result + "!!!!!!!!!!!");
    }
  }

  @Override
  public void doPost(String userId, HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    // TODO Auto-generated method stub

  }
}
