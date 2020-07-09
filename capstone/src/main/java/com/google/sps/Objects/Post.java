package com.google.sps.Objects;

import java.util.ArrayList;
import java.util.HashSet;
import com.google.appengine.api.datastore.Entity;

public final class Post {

  private final long postId;
  private final String authorId;
  private final String postText;
  private final ArrayList<Comment> comments;
  private final String challengeName;
  private final long timestamp;
  private final String img;
  private final HashSet<String> likes;
  
  public Post(
    long postId, 
    String authorId, 
    String postText, 
    ArrayList<Comment> comments, 
    String challengeName, 
    long timestamp, 
    String img, 
    HashSet<String> likes
  ) {
    this.postId = postId;
    this.timestamp = timestamp;
    this.postText = postText;
    this.authorId = authorId;
    this.comments = comments;
    this.challengeName = challengeName;
    this.img = img;
    this.likes = likes;
  }

  public static Post getPostEntity(Entity entity) {
    long postId = entity.getKey().getId();
    long timestamp = (long) entity.getProperty("timestamp");
    String authorId = (String) entity.getProperty("authorId");
    String postText = (String) entity.getProperty("postText");
    String challengeName = (String) entity.getProperty("challengeName");
    String img = (String) entity.getProperty("img");
    HashSet<String> likes = (entity.getProperty("likes") == null) 
      ? new HashSet<>() 
      : new HashSet<String>((ArrayList<String>) entity.getProperty("likes"));   
    ArrayList<Comment> comments = new ArrayList<>();
    return new Post(postId, authorId, postText, comments, challengeName, timestamp, img, likes);
  }

  @Override 
  public boolean equals(Object other) {
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof Post)) return false;
    Post post = (Post) other;
    return timestamp == post.timestamp &&
      authorId.equals(post.authorId) &&
      postText.equals(post.postText) &&
      challengeName.equals(post.challengeName) &&
      img.equals(post.img) &&
      likes.containsAll(post.likes) &&
      comments.containsAll(post.comments);
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

  public HashSet<String> getLikes() {
    return likes;
  }

  public void addComment(Comment newComment) {
    this.comments.add(newComment);
  }

  public void addLike(String userId) {
    this.likes.add(userId);
  }

  public void removeLike(String userId) {
    this.likes.remove(userId);
  }
}
