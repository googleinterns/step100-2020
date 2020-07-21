package com.google.sps.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Trie implements Serializable {

  private static final long serialVersionUID = 1L;
  private Map<String, Trie> children;
  private boolean isEnd;

  public Trie() {
    children = new HashMap<String, Trie>();
    isEnd = false;
  }

  public void insert(String name, String fullName) {
    if (name.contentEquals("")) {
      isEnd = true;
      // add in the full name here
    } else {
      String firstChar = name.substring(0, 1);
      if (!(children.containsKey(firstChar))) {
        children.put(firstChar, new Trie());
      }
      children.get(firstChar).insert(name.substring(1), fullName);
    }
  }

  public Set<String> findAllWithPrefix() {
    return null;
  }
}
