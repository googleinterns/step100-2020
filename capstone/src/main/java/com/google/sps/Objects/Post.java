package com.google.sps.Objects;

import java.util.ArrayList;

public final class Post {

  private final User author;
  private final String postText;
  private final ArrayList<Comment> comments;
  private final String challengeName;
  private final long timestamp;
  private final String img;
  private final ArrayList<User> likes;

  public Post(User author, String postText, ArrayList<Comment> comments, String challengeName,
      long timestamp, String img, ArrayList<User> likes) {
    this.timestamp = timestamp;
    this.postText = postText;
    this.author = author;
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

  public User getAuthor() {
    return author;
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

  public ArrayList<User> getLikes() {
    return likes;
  }
}
