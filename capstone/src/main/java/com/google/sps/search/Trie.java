package com.google.sps.search;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
      children.put(fullName, new Trie());
    } else {
      String firstChar = name.substring(0, 1).toUpperCase();
      if (!(children.containsKey(firstChar))) {
        children.put(firstChar, new Trie());
      }
      children.get(firstChar).insert(name.substring(1), fullName);
    }
  }

  public Set<String> searchWithPrefix(String prefix, String totalPrefix) {
    if (prefix.equals("")) {
      return findAll(new TreeSet<String>(), totalPrefix);
    }
    return null;
  }

  public Set<String> findAll(Set<String> names, String prefix) {
    if (isEnd) {
      names.add(prefix);
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
