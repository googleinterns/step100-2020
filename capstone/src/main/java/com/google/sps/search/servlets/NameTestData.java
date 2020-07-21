package com.google.sps.search.servlets;

import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.sps.Objects.User;
import com.google.sps.search.CsvReader;

@WebServlet("/name-test-data")
public class NameTestData extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) {
    CsvReader reader = new CsvReader();
    List<String> userInfo = reader.parseCorpus("../../data/user_data.csv");
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    for (String str : userInfo) {
      String[] split = str.split(" ");
      String firstName = split[0];
      String lastName = split[1];
      String userId = firstName + "_" + lastName;
      String email = userId + "@gmail.com";
      User user = new User(userId, firstName, lastName, email, "", "", null, null, null);
      datastore.put(user.toEntity());
    }
  }
}
