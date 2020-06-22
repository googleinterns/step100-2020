package com.google.sps.Objects;

import java.util.ArrayList;

public final class Option {

  private final String text;
  private ArrayList<Integer> votes;

  public Option(String text) {
    this.text = text;
    this.votes = new ArrayList<Integer>();
  }

  public String getText() {
    return this.text;
  }

  public void addVote(int id) {
    this.votes.add(id);
  }

  public ArrayList<Integer> getVotes() {
    return votes;
  }
}