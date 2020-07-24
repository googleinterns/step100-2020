package com.google.sps.Objects;

import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.sps.servlets.ServletHelper;

public final class Group {

  private final ArrayList<String> memberIds;
  private final ArrayList<Long> challengeIds;
  private final ArrayList<Long> postIds;
  private final ArrayList<Long> optionIds;
  private final ArrayList<Tag> tags;
  private final String groupName;
  private final String headerImg;
  private final long groupId;

  public Group(
      ArrayList<String> memberIds,
      ArrayList<Long> challenges,
      ArrayList<Long> posts,
      ArrayList<Long> options,
      ArrayList<Tag> tags,
      String groupName,
      String headerImg,
      long groupId) {
    this.memberIds = memberIds;
    this.challengeIds = challenges;
    this.postIds = posts;
    this.optionIds = options;
    this.tags = tags;
    this.groupName = groupName;
    this.headerImg = headerImg;
    this.groupId = groupId;
  }

  public static Entity getGroupEntity(
      HttpServletRequest request, HttpServletResponse response, DatastoreService datastore)
      throws IOException {
    String groupIdString = request.getParameter("groupId");
    long groupId = Long.parseLong(groupIdString);
    Entity groupEntity = ServletHelper.getEntityFromId(response, groupId, datastore, "Group");
    return groupEntity;
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

  public ArrayList<Tag> getTags() {
    return tags;
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

  public void addPost(Tag newTag) {
    this.tags.add(newTag);
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

    ArrayList<Tag> tags = new ArrayList<>();
    if (entity.getProperty("tags") != null) {
      createTagList(tags, entity);
    }

    return new Group(memberIds, challenges, posts, options, tags, groupName, headerImg, groupId);
  }

  private static ArrayList<Long> getPropertyList(String property, Entity entity) {
    ArrayList<Long> propertyList =
        (entity.getProperty(property) == null)
            ? new ArrayList<Long>()
            : (ArrayList<Long>) entity.getProperty(property);
    return propertyList;
  }

  private static void createTagList(ArrayList<Tag> tags, Entity groupEntity) {
    ArrayList<EmbeddedEntity> tagEntities = 
        (ArrayList<EmbeddedEntity>) groupEntity.getProperty("tags");
    for (EmbeddedEntity tag: tagEntities) {
      tags.add(Tag.fromEntity(tag));
    }
  }
}
