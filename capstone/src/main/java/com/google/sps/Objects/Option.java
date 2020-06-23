package com.google.sps.Objects;

import java.util.ArrayList;

public final class Option {

  private final String text;
  private ArrayList<String> userVotes;

  public Option(String text) {
    this.text = text;
    this.userVotes = new ArrayList<String>();
  }

  public String getText() {
    return this.text;
  }

  public void addVote(String id) {
    this.userVotes.add(id);
  }

  public ArrayList<String> getVotes() {
    return userVotes;
  }
}