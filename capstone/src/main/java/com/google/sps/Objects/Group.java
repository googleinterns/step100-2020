package com.google.sps.Objects;

import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList;

public final class Group {

  private final ArrayList<String> memberIds;
  private final ArrayList<Challenge> challenges;
  private final ArrayList<Post> posts;
  private final ArrayList<Option> options;
  private final String groupName;
  private final String headerImg;
  private final long groupId;

  public Group(ArrayList<String> memberIds, ArrayList<Challenge> challenges, ArrayList<Post> posts, 
      ArrayList<Option> options, String groupName, String headerImg, long groupId) {
    this.memberIds = memberIds;
    this.challenges = challenges;
    this.posts = posts;
    this.options = options;
    this.groupName = groupName;
    this.headerImg = headerImg;
    this.groupId = groupId;
  }

  public ArrayList<String> getMemberIds() {
    return memberIds;
  }

  public ArrayList<Challenge> challenges() {
    return challenges;
  }

  public ArrayList<Post> getPosts() {
    return posts;
  }

  public ArrayList<Options> getOptions() {
    return options;
  }

  public String getGroupName() {
    return groupName;
  }

  public String getHeaderImg() {
    return headerImg;
  }

  public long getGroupId() {
    return groupId;
  }

  public void addChallenge(Challenge newChallenge) {
    this.challenges.add(newChallenge);
  }

  public void addMember(String memberId) {
    this.memberIds.add(memberId);
  }

  public void addPost(Post newPost) {
    this.posts.add(newPost);
  }

  /**
   * Creates and returns a Group object given a Group entity.
   */
  public static Group fromEntity(Entity entity) {
    long groupId = entity.getKey().getId();
    long timestamp = (long) entity.getProperty("timestamp");
    String authorId = (String) entity.getProperty("authorId");
    String postText = (String) entity.getProperty("postText");
    String challengeName = (String) entity.getProperty("challengeName");
    String img = (String) entity.getProperty("img");
    HashSet<String> likes = (entity.getProperty("likes") == null) 
      ? new HashSet<>() 
      : new HashSet<String>((ArrayList<String>) entity.getProperty("likes"));   
    ArrayList<Comment> comments = new ArrayList<>();
    return new Group(postId, authorId, postText, comments, challengeName, timestamp, img, likes);
  }
}
