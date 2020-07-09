package com.google.sps.Objects;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;

public final class Comment {

  private final long timestamp;
  private final String commentText;
  private final String userId;

  public Comment(long timestamp, String commentText, String userId) {
    this.timestamp = timestamp;
    this.commentText = commentText;
    this.userId = userId;
  }

  public static Comment getCommentEntity(EmbeddedEntity entity) {
    Long timestamp = (long) entity.getProperty("timestamp");
    String commentText = (String) entity.getProperty("commentText");
    String userId = (String) entity.getProperty("userId");
    return new Comment(timestamp, commentText, userId);
  }

  public static EmbeddedEntity toEntity(String commentText, String userId) {
    EmbeddedEntity commentEntity = new EmbeddedEntity();
    commentEntity.setProperty("timestamp", System.currentTimeMillis());
    commentEntity.setProperty("commentText", commentText);
    commentEntity.setProperty("userId", userId);
    return commentEntity;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getCommentText() {
    return commentText;
  }

  public String getUser() {
    return userId;
  }
}
