package com.google.sps.Objects;

import java.util.ArrayList;

import com.google.appengine.api.datastore.Entity;

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

  /* Given a Group entity, creates and returns a Group object. */
  public static Group fromEntity(Entity entity) {
    ArrayList<String> memberIds = (ArrayList<String>) entity.getProperty("memberIds");
    ArrayList<Long> challenges = getPropertyList("challenges", entity);
    ArrayList<Long> posts = getPropertyList("posts", entity);
    ArrayList<Long> options = getPropertyList("options", entity);
    String groupName = (String) entity.getProperty("groupName");
    String headerImg = (String) entity.getProperty("headerImg");
    long groupId = entity.getKey().getId();

    return new Group(memberIds, challenges, posts, options, groupName, headerImg, groupId);
  }

  private static ArrayList<Long> getPropertyList(String property, Entity entity) {
    ArrayList<Long> propertyList =
        (entity.getProperty(property) == null)
            ? new ArrayList<Long>()
            : (ArrayList<Long>) entity.getProperty(property);
    return propertyList;
  }
}
