package com.google.sps.Objects;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;

import java.util.ArrayList;

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

  public static ArrayList<EmbeddedEntity> createCommentEntities(ArrayList<Comment> comments) {
    ArrayList<EmbeddedEntity> allComments = new ArrayList<>();
    for (Comment comment: comments) {
      allComments.add(
        toEntity(comment.getCommentText(), comment.getUser())
      );
    } 
    return allComments;
  }

  @Override
  public boolean equals(Object other) {
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof Comment)) return false;
    Comment comment = (Comment) other;
    return commentText.equals(comment.commentText) &&
      userId.equals(comment.userId);
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
