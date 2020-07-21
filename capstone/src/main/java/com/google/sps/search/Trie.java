package com.google.sps.search;

import java.io.Serializable;
import java.util.Map;

public class Trie implements Serializable {

  private static final long serialVersionUID = 1L;
  private Map<String, Trie> children;
  int count;

  public Trie() {
    count = 0;
  }

  private void insert(String name) {}

  public void incrementCounter() {
    count++;
  }

  public int getCount() {
    return this.count;
  }

  private void buildFirstNameTrie() {}

  private void buildLastNameTrie() {}
}
