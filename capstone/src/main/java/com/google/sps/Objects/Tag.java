package com.google.sps.Objects;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;

import java.util.ArrayList;

public final class Tag implements Comparable<Tag> {

  private final String text;
  private final double score;

  public Tag(String text, double score) {
    this.text = text;
    this.score = score;
  }

  public static Tag fromEntity(EmbeddedEntity entity) {
    String text = (String) entity.getProperty("text");
    double score = (double) entity.getProperty("score");
    return new Tag(text, score);
  }

  public EmbeddedEntity toEntity() {
    EmbeddedEntity tokenEntity = new EmbeddedEntity();
    tokenEntity.setProperty("text", this.text);
    tokenEntity.setProperty("score", this.score);
    return tokenEntity;
  }

  public static ArrayList<EmbeddedEntity> createTagEntities(ArrayList<Tag> tags) {
    ArrayList<EmbeddedEntity> allTags = new ArrayList<>();
    for (Tag tag : tags) {
      allTags.add(tag.toEntity());
    } 
    return allTags;
  }

  public double getScore() {
    return score;
  }

  public String getText() {
    return text;
  }

  @Override
  public int compareTo(Tag o) {
    Double score = getScore();
    Double otherScore = o.getScore();
    return score.compareTo(otherScore);
  }
}