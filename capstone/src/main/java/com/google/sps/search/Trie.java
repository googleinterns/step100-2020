package com.google.sps.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class Trie implements Serializable {

  private static final long serialVerionID = 1L;
  private List<String> names;

  public Trie() {
    this.names = this.getNamesFromDb();
  }

  private List<String> getNamesFromDb() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("User");
    PreparedQuery pq = datastore.prepare(query);
    List<String> names = new ArrayList<String>();
    for (Entity userEntity : pq.asIterable()) {
      String firstName = (String) userEntity.getProperty("firstName");
      String lastName = (String) userEntity.getProperty("lastName");
      String name = firstName + " " + lastName;
      names.add(name);
    }
    return names;
  }

  private void buildFirstNameTrie() {}

  private void buildLastNameTrie() {}
}
