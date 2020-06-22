package com.google.sps.Objects;

import java.util.ArrayList;

public final class Option {

  private final String text;
  private ArrayList<String> votes;

  public Option(String text) {
    this.text = text;
    this.votes = new ArrayList<String>();
  }

  public String getText() {
    return this.text;
  }

  public void addVote(String id) {
    this.votes.add(id);
  }

  public ArrayList<String> getVotes() {
    return votes;
  }
}