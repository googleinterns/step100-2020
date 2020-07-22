package com.google.sps.search;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
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
  private Map<String, Trie> children;
  private boolean isEnd;

  /**
   * Constructor that sets the children of trie node and whether current node marks the end of the
   * name.
   */
  public Trie() {
    this.children = new HashMap<String, Trie>();
    this.isEnd = false;
  }

  /**
   * Inserts name into trie, with full name being the last node appending to the end of the name.
   *
   * @param name user first name or last name
   * @param fullName user full name.
   */
  public void insert(String name, String fullName) {
    if (name.contentEquals("")) {
      this.isEnd = true;
      this.children.put(fullName, new Trie());
    } else {
      String firstChar = name.substring(0, 1).toUpperCase();
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
      String firstChar = prefix.substring(0, 1).toUpperCase();
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
    Map<String, Trie> possibilities = children;
    if (isEnd) {
      StringBuilder sb = new StringBuilder();
      for (String fullName : possibilities.keySet()) {
        sb.append(fullName);
        names.add(sb.toString());
      }
    }
    for (String letter : possibilities.keySet()) {
      StringBuilder sb = new StringBuilder();
      sb.append(prefix);
      sb.append(letter);
      possibilities.get(letter).findAll(names, sb.toString());
    }
    return names;
  }

  /**
   * Returns children of current Trie node.
   *
   * @return map from string to Trie object
   */
  public Map<String, Trie> getChildren() {
    return this.children;
  }

  /**
   * Returns whether current node is the end of the name.
   *
   * @return boolean
   */
  public boolean getIsEnd() {
    return this.isEnd;
  }
}
