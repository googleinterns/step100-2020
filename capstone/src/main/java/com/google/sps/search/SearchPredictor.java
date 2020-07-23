package com.google.sps.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class SearchPredictor implements Serializable {

  private static final long serialVersionUID = 1L;
  private List<String> names;
  private Trie firstNameTrie;
  private Trie lastNameTrie;

  public SearchPredictor() {
    this.names = this.getNamesFromDb();
    this.firstNameTrie = new Trie();
    this.lastNameTrie = new Trie();
    this.populateTrie();
  }

  public List<String> getNamesFromDb() {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query = new Query("User");
    PreparedQuery pq = datastore.prepare(query);
    List<String> names = new ArrayList<String>();
    for (Entity userEntity : pq.asIterable()) {
      String firstName = (String) userEntity.getProperty("firstName");
      String lastName = (String) userEntity.getProperty("lastName");
      // unique separator to account for names like Marie Rose Shapiro
      String name = firstName + "+" + lastName;
      names.add(name);
    }
    return names;
  }

  private void populateTrie() {
    for (String fullName : names) {
      String[] split = fullName.split("+");
      // not as robust for internationalization
      String firstName = split[0];
      String lastName = split[1];
      firstNameTrie.insert(firstName, fullName);
      lastNameTrie.insert(lastName, fullName);
    }
  }

  // incomplete
  public Set<String> suggest(String phrase) {
    Set<String> names = new HashSet<String>();
    names.addAll(firstNameTrie.searchWithPrefix(phrase, phrase));
    names.addAll(lastNameTrie.searchWithPrefix(phrase, phrase));
    return names;
  }

  public Trie getFirstNameTrie() {
    return this.firstNameTrie;
  }
}
