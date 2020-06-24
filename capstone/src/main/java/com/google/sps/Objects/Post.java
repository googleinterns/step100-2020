package com.google.sps.Objects;

import java.util.ArrayList;

public final class Post {

  private final String authorId;
  private final String postText;
  private final ArrayList<Comment> comments;
  private final String challengeName;
  private final long timestamp;
  private final String img;
  private final ArrayList<String> likes;

  public Post(String authorId, String postText, ArrayList<Comment> comments, String challengeName, long timestamp, String img, ArrayList<String> likes) {
    this.timestamp = timestamp;
    this.postText = postText;
    this.authorId = authorId;
    this.comments = comments;
    this.challengeName = challengeName;
    this.img = img;
    this.likes = likes;
  }

  public long getTimestamp() {
    return timestamp;
  }

  public String getPostText() {
    return postText;
  }

  public String getAuthorId() {
    return authorId;
  }

  public ArrayList<Comment> getComments() {
    return comments;
  }

  public String getChallengeName() {
    return challengeName;
  }

  public String getImg() {
    return img;
  }

  public ArrayList<String> getLikes() {
    return likes;
  }

  public void addComment(Comment newComment) {
    this.comments.add(newComment);
  }

  public void addNewLike(User newLike) {
    this.likes.add(newLike);
  }
}
