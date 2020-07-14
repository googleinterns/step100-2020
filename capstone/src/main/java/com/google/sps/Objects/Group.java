package com.google.sps.Objects;

import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList;

public final class Group {

  private final ArrayList<String> memberIds;
  private final ArrayList<Long> challenges;
  private final ArrayList<Long> posts;
  private final ArrayList<Long> options;
  private final String groupName;
  private final String headerImg;
  private final long groupId;

  public Group(ArrayList<String> memberIds, ArrayList<Long> challenges, ArrayList<Long> posts, 
      ArrayList<Long> options, String groupName, String headerImg, long groupId) {
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

  public ArrayList<Long> challenges() {
    return challenges;
  }

  public ArrayList<Long> getPosts() {
    return posts;
  }

  public ArrayList<Long> getOptions() {
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

  public void addChallenge(Long newChallenge) {
    this.challenges.add(newChallenge);
  }

  public void addMember(String memberId) {
    this.memberIds.add(memberId);
  }

  public void addPost(Long newPost) {
    this.posts.add(newPost);
  }

  /* Given a Group entity, creates and returns a Group object. */
  public static Group fromEntity(Entity entity) {
    ArrayList<String> memberIds = (ArrayList<String>) entity.getProperty("memberIds");
    ArrayList<Long> challenges = (entity.getProperty("challenges") == null) 
      ? new ArrayList<Long>() 
      : (ArrayList<Long>) entity.getProperty("challenges");   
    ArrayList<Long> posts = (entity.getProperty("posts") == null) 
      ? new ArrayList<Long>() 
      : (ArrayList<Long>) entity.getProperty("posts");   
    ArrayList<Long> options = (entity.getProperty("options") == null) 
      ? new ArrayList<Long>() 
      : (ArrayList<Long>) entity.getProperty("options");   
    String groupName = (String) entity.getProperty("groupName");
    String headerImg = (String) entity.getProperty("headerImg");
    long groupId = entity.getKey().getId();

    return new Group(memberIds, challenges, posts, options, groupName, headerImg, groupId);
  }
}
