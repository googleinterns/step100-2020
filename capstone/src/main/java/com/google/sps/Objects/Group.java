package com.google.sps.Objects;

import java.util.ArrayList;

public final class Group {

  private final ArrayList<String> memberIds;
  private final ArrayList<Challenge> challenges;
  private final ArrayList<Post> posts;
  private final Poll poll;
  private final String groupName;
  private final String headerImg;

  public Group(ArrayList<String> memberIds, ArrayList<Challenge> challenges, ArrayList<Post> posts, 
      Poll poll, String groupName, String headerImg) {
    this.memberIds = memberIds;
    this.challenges = challenges;
    this.posts = posts;
    this.poll = poll;
    this.groupName = groupName;
    this.headerImg = headerImg;
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

  public void addChallenge(Challenge newChallenge) {
    this.challenges.add(newChallenge);
  }

  public void addMember(String memberId) {
    this.memberIds.add(memberId);
  }

  public void addPost(Post newPost) {
    this.posts.add(newPost);
  }
}
