package com.google.sps.servlets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Scanner;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.sps.Objects.Badge;
import com.google.sps.Objects.User;

/**
 * Parses a TSV file of fake group data and populates the Datastore with Group and Post entities.
 */
@WebServlet("/parse-user-data")
public class ParseUserDataServlet extends HttpServlet {

  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void init() {
    Scanner scanner =
        new Scanner(getServletContext().getResourceAsStream("/WEB-INF/user-test-data.tsv"));

    scanner.nextLine();

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] fields = line.split("\t");
      assert fields.length == 7;
      createUserEntity(fields);
    }

    scanner.close();
  }

  /** Create all User entitys and add to datastore */
  private void createUserEntity(String[] fields) {
    String firstName = String.valueOf(fields[0]);
    String lastName = String.valueOf(fields[1]);
    String userId = String.valueOf(fields[2]);
    String email = String.valueOf(fields[3]);
    double latitude = Double.valueOf(fields[4]);
    double longitude = Double.valueOf(fields[5]);
    LinkedHashSet<Long> groupIds = getGroups(fields[6]);

    User testUser =
        new User(
            userId,
            firstName,
            lastName,
            email,
            /*phoneNumber*/ "0",
            /* Profile Picture */ "",
            /* Address */ "",
            latitude,
            longitude,
            /* badges */ new LinkedHashSet<Badge>(),
            /* groups */ groupIds,
            /* interests */ new ArrayList<String>());
    datastore.put(testUser.toEntity());
  }

  /** Get the groupIds for all user groups. */
  private LinkedHashSet<Long> getGroups(String groupNamesString) {
    groupNamesString =
        groupNamesString.replaceAll("(\\s*,\\s*)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", ",");

    String[] groupsArray = groupNamesString.split(",");
    Long[] groupsArrayLong = new Long[groupsArray.length];
    for (int i = 0; i < groupsArray.length; i++) {
      groupsArrayLong[i] = getGroupId(groupsArray[i]);
    }
    LinkedHashSet<Long> groupsList = new LinkedHashSet<Long>(Arrays.asList(groupsArrayLong));

    return groupsList;
  }

  /** Get the groupId from Group entity */
  private Long getGroupId(String groupName) {
    Filter findGroupEntity = new FilterPredicate("groupName", FilterOperator.EQUAL, groupName);
    Query query = new Query("Group").setFilter(findGroupEntity);
    PreparedQuery pq = datastore.prepare(query);
    if (pq.asSingleEntity() == null) {
      return 0L;
    }
    return pq.asSingleEntity().getKey().getId();
  }
}
