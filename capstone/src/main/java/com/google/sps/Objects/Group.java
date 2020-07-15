package com.google.sps.Objects;

import java.util.ArrayList;

public class Group {

  private final ArrayList<String> memberIds;
  private final ArrayList<Long> challengeIds;
  private final ArrayList<Long> postIds;
  private final ArrayList<Long> optionIds;
  private final String groupName;
  private final String headerImg;
  private final long groupId;

  public Group(
      ArrayList<String> memberIds,
      ArrayList<Long> challenges,
      ArrayList<Long> posts,
      ArrayList<Long> options,
      String groupName,
      String headerImg,
      long groupId) {
    this.memberIds = memberIds;
    this.challengeIds = challenges;
    this.postIds = posts;
    this.optionIds = options;
    this.groupName = groupName;
    this.headerImg = headerImg;
    this.groupId = groupId;
  }

  public ArrayList<String> getMemberIds() {
    return memberIds;
  }

  public ArrayList<Long> challenges() {
    return challengeIds;
  }

  public ArrayList<Long> getPosts() {
    return postIds;
  }

  public ArrayList<Long> getOptions() {
    return optionIds;
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
    this.challengeIds.add(newChallenge);
  }

  public void addMember(String memberId) {
    this.memberIds.add(memberId);
  }

  public void addPost(Long newPost) {
    this.postIds.add(newPost);
  }
}
