package com.google.sps.search;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Trie implements Serializable {

  private static final long serialVersionUID = 1L;
  private Map<String, Trie> children;
  private boolean isEnd;

  public Trie() {
    this.children = new HashMap<String, Trie>();
    this.isEnd = false;
  }

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

  public Map<String, Trie> getChildren() {
    return this.children;
  }

  public boolean getIsEnd() {
    return this.isEnd;
  }
}
