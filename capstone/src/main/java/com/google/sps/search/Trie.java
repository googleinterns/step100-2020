package com.google.sps.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class representing the Trie data structure. Contains children to indicate child nodes in the data
 * structure and whether current node is the end of the name.
 *
 * @author lucyqu
 */
public class Trie implements Serializable {

  private static final long serialVersionUID = 1L;
  private Map<Character, Trie> children;
  private Set<String> fullNames;
  private static final int MAX_LED = 2;

  /**
   * Constructor that sets the children of trie node and whether current node marks the end of the
   * name.
   */
  public Trie() {
    this.children = new HashMap<Character, Trie>();
    this.fullNames = new HashSet<String>();
  }

  /**
   * Inserts name into trie, with full name being the last node appended to the end of the name.
   *
   * @param name user first name or last name
   * @param fullName user full name.
   */
  public void insert(String name, String fullName) {
    if (name.contentEquals("")) {
      fullNames.add(fullName);
    } else {
      Character firstChar = Character.toUpperCase(name.charAt(0));
      if (!(this.children.containsKey(firstChar))) {
        this.children.put(firstChar, new Trie());
      }
      this.children.get(firstChar).insert(name.substring(1), fullName);
    }
  }

  /**
   * Returns set of strings with names that match the given prefix.
   *
   * @param prefix prefix to search in trie
   * @param totalPrefix parameter needed to build string
   * @return set of strings with matching prefix
   */
  public Set<String> searchWithPrefix(String prefix, String totalPrefix) {
    if (prefix.equals("")) {
      return findAll(new TreeSet<String>(), totalPrefix);
    } else {
      Character firstChar = Character.toUpperCase(prefix.charAt(0));
      if (this.children.containsKey(firstChar)) {
        return this.children.get(firstChar).searchWithPrefix(prefix.substring(1), totalPrefix);
      } else {
        return Collections.emptySet();
      }
    }
  }

  /**
   * Helper method used to find all words from a certain node.
   *
   * @param names set of matching names
   * @param prefix characters from the trie built up to current node
   * @return set of full names formed from node in Trie
   */
  private Set<String> findAll(Set<String> names, String prefix) {
    Map<Character, Trie> possibilities = children;
    /* If current node is the end of the a first name or last name, add the full name nodes to set of names to be returned. */
    if (this.getIsName()) {
      for (String fullName : this.fullNames) {
        StringBuilder sb = new StringBuilder();
        sb.append(fullName);
        names.add(sb.toString());
      }
    }
    for (Character letter : possibilities.keySet()) {
      StringBuilder sb = new StringBuilder();
      sb.append(prefix);
      sb.append(letter);
      possibilities.get(letter).findAll(names, sb.toString());
    }
    return names;
  }

  /**
   * Checks whether the input can be split into a full name by splitting input at each index and
   * checking whether the newly created first and last name exist in the set of full names in the
   * trie. For example, input "andrewsweet" can be split in "Andrew Sweet," and if this name is in
   * the trie, then the name will be added to the set. Boolean reversed indicates whether to search
   * for the name but first and last name are reversed. For example, "sweetandrew" is reversed and
   * it can be split but into "Sweet Andrew," for which we can check that the reverse ("Andrew
   * Sweet") is in the trie.
   *
   * @param input user input
   * @param reversed boolean representing whether name is reversed
   * @return Set of name suggestions
   */
  public Set<String> whitespace(String input, boolean reversed) {
    Set<String> fullNames = new TreeSet<String>();

    for (int i = 0; i < input.length(); i++) {
      String firstName = input.substring(0, i);
      String lastName = input.substring(i);
      String fullName = "";
      if (reversed) {
        fullName = lastName + " " + firstName;
      } else {
        fullName = firstName + " " + lastName;
      }
      if (this.hasFullName(firstName, fullName)) {
        fullNames.add(fullName);
      }
    }
    return fullNames;
  }

  public Set<String> findLedWithinRoot(String input) {
    Set<String> fullNames = new TreeSet<String>();
    return fullNames;
  }

  public List<String> searchLed(String input) {
    input = input.toUpperCase();

    Map<String, Integer> suggestionsMap = new HashMap<String, Integer>();

    int inputLength = input.length();
    List<Integer> currRow = new ArrayList<Integer>();

    for (int i = 0; i <= inputLength; i++) {
      currRow.add(i);
    }

    Map<String, Integer> suggestedNames = new HashMap<String, Integer>();
    for (Character c : this.children.keySet()) {
      this.searchLedRecursive(this.children.get(c), c, input, currRow, suggestionsMap);
      suggestedNames.putAll(suggestionsMap);
    }

    suggestedNames
        .entrySet()
        .forEach(
            entry -> {
              System.out.println(entry.getKey() + " " + entry.getValue());
            });

    return this.sortNames(suggestedNames);
  }

  private void searchLedRecursive(
      Trie currNode,
      Character currChar,
      String input,
      List<Integer> prevRow,
      Map<String, Integer> suggestions) {

    int numColumns = input.length() + 1;
    List<Integer> currRow = new ArrayList<Integer>();
    currRow.add(prevRow.get(0) + 1);

    for (int j = 1; j < numColumns; j++) {
      if (input.charAt(j - 1) == currChar) {
        currRow.add(prevRow.get(j - 1));
      } else {
        currRow.add(1 + Math.min(Math.min(prevRow.get(j), currRow.get(j - 1)), prevRow.get(j - 1)));
      }
    }

    /*
     * If current row indicates end of name and is less than or equal to max LED, add all full names
     * to set of suggestions.
     */
    int ledDistance = currRow.get(currRow.size() - 1);
    if (ledDistance <= MAX_LED && currNode.getIsName()) {
      for (String name : currNode.fullNames) {
        suggestions.put(name, ledDistance);
      }
    }

    /*
     * If any entry in the current row are less than or equal to the maximum allowed LED
     * distance, recursively search each branch of the trie from current character.
     */
    int minRowVal = Collections.min(currRow);
    if (minRowVal <= MAX_LED) {
      for (Character c : currNode.children.keySet()) {
        this.searchLedRecursive(currNode.children.get(c), c, input, currRow, suggestions);
      }
    }
  }

  private List<String> sortNames(Map<String, Integer> namesScore) {
    System.out.println("in sorting names");

    List<String> sortedNames = new ArrayList<String>();

    List<Map.Entry<String, Integer>> entries =
        new ArrayList<Map.Entry<String, Integer>>(namesScore.entrySet());

    Collections.sort(
        entries,
        new Comparator<Map.Entry<String, Integer>>() {
          @Override
          public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
            return Integer.compare(a.getValue(), b.getValue());
          }
        });

    for (Map.Entry<String, Integer> entry : entries) {
      System.out.println(entry.getKey());
      sortedNames.add(entry.getKey());
    }

    System.out.println(sortedNames);
    return sortedNames;
  }

  /**
   * Helper method that checks whether the trie contains the full name by recurring down the tree
   * using the name that is passed in.
   *
   * @param name either first or last name
   * @param fullName both first and last name
   * @return boolean whether full name is in trie.
   */
  private boolean hasFullName(String name, String fullName) {
    if (name.equals("")) {
      if (this.fullNames.contains(fullName)) {
        return true;
      } else {
        return false;
      }
    } else {
      char firstChar = name.charAt(0);
      if (children.containsKey(firstChar)) {
        return children.get(firstChar).hasFullName(name.substring(1), fullName);
      } else {
        return false;
      }
    }
  }

  /**
   * Returns children of current Trie node.
   *
   * @return map from string to Trie object
   */
  public Map<Character, Trie> getChildren() {
    return this.children;
  }

  /**
   * Returns whether current node is the end of the name.
   *
   * @return boolean
   */
  public boolean getIsName() {
    return !this.fullNames.isEmpty();
  }

  public Set<String> getFullNames() {
    return this.fullNames;
  }
}
