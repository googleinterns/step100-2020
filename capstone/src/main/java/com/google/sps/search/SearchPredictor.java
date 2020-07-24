package com.google.sps.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class SearchPredictor implements Serializable {

  private static final int FIVE = 5;
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
      String name = firstName + "@" + lastName;
      names.add(name);
    }
    return names;
  }

  private void populateTrie() {
    for (String fullName : names) {
      String[] split = fullName.split("@");
      // not robust for internationalization
      String firstName = split[0];
      String lastName = split[1];
      String properName = firstName + " " + lastName;
      firstNameTrie.insert(firstName, properName);
      lastNameTrie.insert(lastName, properName);
    }
  }

  // incomplete
  public List<String> suggest(String input) {
    Map<String, Integer> namesScore = new HashMap<String, Integer>();
    String[] split = input.split(" ");
    for (int i = 0; i < split.length; i++) {
      String partialName = split[i];
      System.out.println(partialName);
      Set<String> firstNameSuggestions = firstNameTrie.searchWithPrefix(partialName, partialName);
      Set<String> lastNameSuggestions = lastNameTrie.searchWithPrefix(partialName, partialName);
      // Matching prefix first name is weighted more heavily
      this.addToMap(namesScore, firstNameSuggestions, partialName, 2);
      this.addToMap(namesScore, lastNameSuggestions, partialName, 1);
    }
    System.out.println(namesScore);
    List<String> sortedNames = this.sortNames(namesScore);
    System.out.println(sortedNames);
    return sortedNames;
  }

  public List<String> sortNames(Map<String, Integer> namesScore) {
    List<String> sortedNames = new ArrayList<String>();

    List<Map.Entry<String, Integer>> entries =
        new ArrayList<Map.Entry<String, Integer>>(namesScore.entrySet());

    Collections.sort(
        entries,
        new Comparator<Map.Entry<String, Integer>>() {
          @Override
          public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
            return Integer.compare(b.getValue(), a.getValue());
          }
        });

    for (Map.Entry<String, Integer> entry : entries) {
      sortedNames.add(entry.getKey());
    }

    return sortedNames;
  }

  private Map<String, Integer> addToMap(
      Map<String, Integer> namesScore, Set<String> set, String partialName, int increment) {
    for (String name : set) {
      if (!namesScore.containsKey(name)) {
        namesScore.put(name, increment);
      } else {
        int score = namesScore.get(name) + 1;
        namesScore.put(name, score);
      }

      // If there is a complete name match for first or last name, increment score by 5
      String[] split = name.split(" ");
      partialName = partialName.toUpperCase();
      if (partialName.equals(split[0]) || partialName.equals(split[1])) {
        int score = namesScore.get(name) + FIVE;
        namesScore.put(name, score);
      }
    }
    return namesScore;
  }

  public Trie getFirstNameTrie() {
    return this.firstNameTrie;
  }
}
