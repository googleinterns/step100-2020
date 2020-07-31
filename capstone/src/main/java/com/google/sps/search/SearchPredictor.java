package com.google.sps.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SearchPredictor implements Serializable {

  private static final double COMPLETE_PARTIAL_NAME_MATCH = 5;
  private static final double COMPLETE_MATCH = 10;
  private static final double FIRST_NAME_MATCH = 2;
  private static final double LAST_NAME_MATCH = 1;
  private static final long serialVersionUID = 1L;
  private List<String> names;
  private Trie firstNameTrie;
  private Trie lastNameTrie;
  private DatabaseRetriever dbRetriever;

  public SearchPredictor() {
    this.dbRetriever = new DatabaseRetriever();
    this.names = this.getNamesFromDb();
    this.firstNameTrie = new Trie();
    this.lastNameTrie = new Trie();
    this.populateTries();
  }

  public List<String> getNamesFromDb() {
    //    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    //    Query query = new Query("User");
    //    PreparedQuery pq = datastore.prepare(query);
    //    List<String> names = new ArrayList<String>();
    //    for (Entity userEntity : pq.asIterable()) {
    //      String firstName = (String) userEntity.getProperty("firstName");
    //      String lastName = (String) userEntity.getProperty("lastName");
    //      // unique separator to account for names like Marie Rose Shapiro
    //      String name = firstName + "@" + lastName;
    //      names.add(name);
    //    }
    //    return names;
    return this.dbRetriever.getNamesFromDb();
  }

  private void populateTries() {
    System.out.println("populating tries with " + this.names);
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

  public void insertName(String firstName, String lastName) {
    String fullName = firstName + " " + lastName;
    firstNameTrie.insert(firstName, fullName);
    lastNameTrie.insert(lastName, fullName);
  }

  public List<String> suggest(String input) {
    Map<String, Double> namesScore = new HashMap<String, Double>();
    String[] firstAndLastName = input.split(" ");

    Set<String> whitespaceSuggestionsFirstName =
        firstNameTrie.whitespace(input, /* reversed */ false);
    Set<String> whitespaceSuggestionsLastName = lastNameTrie.whitespace(input, /* reversed */ true);
    this.addToMap(namesScore, whitespaceSuggestionsFirstName, COMPLETE_MATCH);
    this.addToMap(namesScore, whitespaceSuggestionsLastName, COMPLETE_MATCH);

    for (int i = 0; i < firstAndLastName.length; i++) {
      String partialName = firstAndLastName[i].toUpperCase();

      Set<String> firstNameSuggestions = firstNameTrie.searchWithPrefix(partialName, partialName);
      Set<String> lastNameSuggestions = lastNameTrie.searchWithPrefix(partialName, partialName);
      // Matching prefix first name is weighted more heavily
      this.addToMap(namesScore, firstNameSuggestions, partialName, FIRST_NAME_MATCH);
      this.addToMap(namesScore, lastNameSuggestions, partialName, LAST_NAME_MATCH);

      Map<String, Integer> ledSuggestionsFirstName = firstNameTrie.searchLed(partialName);
      Map<String, Integer> ledSuggestionsLastName = lastNameTrie.searchLed(partialName);
      this.addToMapWithEditDistance(namesScore, ledSuggestionsFirstName);
      this.addToMapWithEditDistance(namesScore, ledSuggestionsLastName);
    }

    return this.sortNames(namesScore);
  }

  /**
   * Sorts the suggestioned names in order of score from lowest to highest and returns the list of
   * names.
   *
   * @param namesScore map from name to score
   * @return list of names
   */
  public List<String> sortNames(Map<String, Double> namesScore) {
    List<String> sortedNames = new ArrayList<String>();

    List<Map.Entry<String, Double>> entries =
        new ArrayList<Map.Entry<String, Double>>(namesScore.entrySet());

    Collections.sort(
        entries,
        new Comparator<Map.Entry<String, Double>>() {
          @Override
          public int compare(Map.Entry<String, Double> a, Map.Entry<String, Double> b) {
            return Double.compare(b.getValue(), a.getValue());
          }
        });

    for (Map.Entry<String, Double> entry : entries) {
      sortedNames.add(entry.getKey());
    }

    return sortedNames;
  }

  /**
   * Adds names in the set to the map alongside its score.
   *
   * @param namesScore map from name to score
   * @param set set of suggested names
   * @param partialName either first or last name
   * @param increment score
   * @return map from name to score
   */
  private Map<String, Double> addToMap(
      Map<String, Double> namesScore, Set<String> set, String partialName, double increment) {
    for (String name : set) {
      if (!namesScore.containsKey(name)) {
        namesScore.put(name, increment);
      } else {
        double score = namesScore.get(name) + 1;
        namesScore.put(name, score);
      }

      // If there is a complete name match for first or last name, increment score by 5
      String[] firstAndLastName = name.split(" ");
      partialName = partialName.toUpperCase();
      if (partialName.equals(firstAndLastName[0].toUpperCase())
          || partialName.equals(firstAndLastName[1].toUpperCase())) {
        double score = namesScore.get(name) + COMPLETE_PARTIAL_NAME_MATCH;
        namesScore.put(name, score);
      }
    }
    return namesScore;
  }

  /**
   * Adds names in a set to the map alongside its score.
   *
   * @param namesScore map from name to score
   * @param set set of suggested names
   * @param increment score
   * @return map from name to score
   */
  private Map<String, Double> addToMap(
      Map<String, Double> namesScore, Set<String> set, double increment) {
    for (String name : set) {
      if (!namesScore.containsKey(name)) {
        namesScore.put(name, increment);
      } else {
        namesScore.put(name, namesScore.get(name) + 1);
      }
    }
    return namesScore;
  }

  /**
   * Adds names from the name to edit distance map to the name to score map.
   *
   * @param namesScore map from name to score
   * @param nameToLedMap map from name to its Levenshtein edit distance from input
   * @return map from name to score
   */
  private Map<String, Double> addToMapWithEditDistance(
      Map<String, Double> namesScore, Map<String, Integer> nameToLedMap) {
    for (String name : nameToLedMap.keySet()) {
      double score = 0;
      /* Get the reciprocal of the edit distance so that names with greater edit distance have a
      lower score */
      if (nameToLedMap.get(name) != 0) {
        score = 1 / nameToLedMap.get(name);
      }
      if (namesScore.containsKey(name)) {
        namesScore.put(name, namesScore.get(name) + score);
      } else {
        namesScore.put(name, score);
      }
    }
    return namesScore;
  }

  public Trie getFirstNameTrie() {
    return this.firstNameTrie;
  }
}
