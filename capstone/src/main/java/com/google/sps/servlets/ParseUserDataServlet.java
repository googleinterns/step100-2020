package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Scanner;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.HashSet;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.PrintWriter;
import com.google.sps.Objects.User;
import com.google.sps.Objects.Badge;
import com.google.sps.Objects.response.PostResponse;
import com.google.sps.servlets.ServletHelper;
import error.ErrorHandler;

/**
 * Parses a TSV file of fake group data and populates the Datastore with Group and Post entities.
 */
@WebServlet("/parse-user-data")
public class ParseUserDataServlet extends HttpServlet {

  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void init() {
    Scanner scanner = new Scanner(
      getServletContext().getResourceAsStream("/WEB-INF/user-test-data.tsv"));

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] fields = line.split("\t");
      createUserEntity(fields);
    }

    scanner.close();
  }

  /**
   * Create all User entitys and add to datastore
   */
  private void createUserEntity(String[] fields) {
    String firstName = String.valueOf(fields[0]);
    String lastName = String.valueOf(fields[1]);
    String userId = String.valueOf(fields[2]);
    String email = String.valueOf(fields[3]);
    double latitude = Double.valueOf(fields[4]);
    double longitude = Double.valueOf(fields[5]);
    LinkedHashSet<Long> groupIds = getGroups(fields[6]);
  
    User testUser = new User(
      userId, firstName, lastName, email, /*phoneNumber*/ "0", 
      /* Profile Picture */ "", latitude, longitude,
      /* badges */ new LinkedHashSet<Badge>(), /* groups */ groupIds, 
      /* interests */ new ArrayList<String>());
    datastore.put(testUser.toEntity());
  }

  /**
   * Get the groupsIds of a user.
   */
  private LinkedHashSet<Long> getGroups(String groupString) {
    groupString = groupString.replaceAll("(\\s*,\\s*)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", ",");

    String[] groupsArray = groupString.split(",");
    Long[] groupsArrayLong = new Long[groupsArray.length];
    for (int i = 0; i < groupsArray.length; i++) {
      groupsArrayLong[i] = Long.parseLong(groupsArray[i]);
    }
    LinkedHashSet<Long> groupsList = new LinkedHashSet<Long>(Arrays.asList(groupsArrayLong));

    return groupsList;
  }
}