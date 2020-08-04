package com.google.sps.Objects;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;

import java.util.ArrayList;

public final class Comment {

  private final long timestamp;
  private final String commentText;
  private final String userId;
  private final String userProfilePic;

  public Comment(
    long timestamp, String commentText, String userId, String userProfilePic) {
    this.timestamp = timestamp;
    this.commentText = commentText;
    this.userId = userId;
    this.userProfilePic = userProfilePic;
  }

  public static Comment fromEntity(EmbeddedEntity entity) {
    Long timestamp = (long) entity.getProperty("timestamp");
    String commentText = (String) entity.getProperty("commentText");
    String userId = (String) entity.getProperty("userId");
    String userProfilePic = (String) entity.getProperty("userProfilePic");
    return new Comment(timestamp, commentText, userId, userProfilePic);
  }

  public EmbeddedEntity toEntity() {
    EmbeddedEntity commentEntity = new EmbeddedEntity();
    commentEntity.setProperty("timestamp", this.timestamp);
    commentEntity.setProperty("commentText", this.commentText);
    commentEntity.setProperty("userId", this.userId);
    commentEntity.setProperty("userProfilePic", this.userProfilePic);
    return commentEntity;
  }

  public static ArrayList<EmbeddedEntity> createCommentEntities(ArrayList<Comment> comments) {
    ArrayList<EmbeddedEntity> allComments = new ArrayList<>();
    for (Comment comment: comments) {
      allComments.add(comment.toEntity());
    } 
    return allComments;
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

  public String getProfilePic() {
    return userProfilePic;
  }
}
