package com.google.sps.search.servlets;

import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.sps.Objects.User;
import com.google.sps.search.CsvReader;

@WebServlet("/name-test-data")
public class NameTestDataServlet extends HttpServlet {

  @Override
  public void init() {
    CsvReader reader = new CsvReader();
    List<String> userInfo = reader.parseCorpus("../../data/user_data.csv");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    int counter = 0;
    for (String str : userInfo) {
      String[] split = str.split(" ");
      String firstName = split[0];
      String lastName = split[1];
      String fullName = firstName + " " + lastName;
      String userId = firstName + "_" + lastName + counter;
      String email = userId + "@gmail.com";
      User user = new User(userId, firstName, lastName, fullName, email, "", "", null, null, null);
      datastore.put(user.toEntity());
      counter++;
    }
  }
}
