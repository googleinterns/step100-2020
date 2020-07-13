package com.google.sps.Objects;

import com.google.appengine.api.datastore.Entity;
import java.util.ArrayList;

public final class Group {

  private final ArrayList<String> memberIds;
  private final ArrayList<Challenge> challenges;
  private final ArrayList<Post> posts;
  private final Poll poll;
  private final String groupName;
  private final String headerImg;
  private final long groupId;

  public Group(ArrayList<String> memberIds, ArrayList<Challenge> challenges, ArrayList<Post> posts, 
      Poll poll, String groupName, String headerImg, long groupId) {
    this.memberIds = memberIds;
    this.challenges = challenges;
    this.posts = posts;
    this.poll = poll;
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

  public Poll getPoll() {
    return poll;
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
   * Creates and returns a Group Entity from the current Group object.
   */
  public Entity toEntity() {
    Entity groupEntity = new Entity("Group");
    groupEntity.setProperty("memberIds", memberIds);
    groupEntity.setProperty("challenges", challenges);
    groupEntity.setProperty("posts", posts);
    groupEntity.setProperty("poll", poll);
    groupEntity.setProperty("groupName", groupName);
    groupEntity.setProperty("headerImg", headerImg);
    groupEntity.setProperty("groupId", entity.getKey().getId());
    return groupEntity;
  }
}
