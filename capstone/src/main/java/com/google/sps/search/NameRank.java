package com.google.sps.search;

public class NameRank {

  private String fullName;
  private int score;

  public NameRank(String fullName, int score) {}

  @Override
  public int hashCode() {
    return fullName.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    return this.fullName.equals(((NameRank) o).getFullName());
  }

  public String getFullName() {
    return this.fullName;
  }

  public int getScore() {
    return this.score;
  }

  public void addScore(int increment) {
    this.score += increment;
  }
}
