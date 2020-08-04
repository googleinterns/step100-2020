package com.google.sps.servlets;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Scanner;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import java.io.PrintWriter;
import com.google.sps.Objects.User;
import com.google.sps.Objects.Post;
import com.google.sps.Objects.Comment;
import com.google.sps.Objects.Group;
import com.google.sps.Objects.Tag;
import com.google.sps.Objects.response.PostResponse;
import com.google.sps.servlets.ServletHelper;
import error.ErrorHandler;

/**
 * Parses a TSV file of fake group data and populates the Datastore with Group and Post entities.
 * To be used for testing only!
 */
@WebServlet("/parse-test-group-data")
public class ParseTestGroupDataServlet extends HttpServlet {

  DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  @Override
  public void init() {
    Scanner scanner = new Scanner(
      getServletContext().getResourceAsStream("/WEB-INF/group-test-data.tsv"));

    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] fields = line.split("\t");
      createGroupEntity(fields);
    }

    scanner.close();
  }

  /**
   * Create a Group entity given a row of the TSV and add to Datastore.
   */
  private void createGroupEntity(String[] fields) {
    String groupName = String.valueOf(fields[0]);
    ArrayList<String> memberIds = getMembers(fields[1]);
    ArrayList<Long> postIds = getPosts(fields, memberIds);
     
    Entity groupEntity = createGroupEntity(groupName, memberIds, postIds);
    datastore.put(groupEntity);
  }

  /**
   * Create a list of postIds for a Group.
   */
  private ArrayList<Long> getPosts(String[] fields, ArrayList<String> members) {
    ArrayList<Long> postIds = new ArrayList<>();
    for (int i = 2; i <= 6; i++) {
      long postId = createPost(fields[i], getRandomMember(members));
      postIds.add(postId);
    }
    return postIds;
  }

  /**
   * Create a Post object and add it to the datastore given information from TSV file.
   */
  private long createPost(String postText, String author) {
    String authorName = author;
    String authorPic = "";
    String challengeName = "Challenge Name";
    String img = null;
    HashSet<String> likes = new HashSet<>();
    ArrayList<Comment> comments = new ArrayList<>();

    // Creates entity with submitted data and add to database
    Post post = new Post(0, authorName, authorPic, postText, comments, challengeName, System.currentTimeMillis(), img, likes);
    Entity postEntity = post.toEntity();
    datastore.put(postEntity);
    return postEntity.getKey().getId();
  }

  /**
   * Create and return a groupEntity given information from TSV file.
   */
  private Entity createGroupEntity(String groupName, ArrayList<String> members, 
      ArrayList<Long> posts) {
    Entity groupEntity = new Entity("Group");
    groupEntity.setProperty("memberIds", members);
    groupEntity.setProperty("challenges", new ArrayList<Long>());
    groupEntity.setProperty("posts", posts);
    groupEntity.setProperty("options", new ArrayList<Long>());
    groupEntity.setProperty("tags", new ArrayList<Tag>());
    groupEntity.setProperty("groupName", groupName);
    groupEntity.setProperty("headerImg", "");
    return groupEntity;
  }

  /**
   * Get the memberIds of a group.
   */
  private ArrayList<String> getMembers(String list) {
    // Trim any whitespace immediately following a comma.
    list = list.replaceAll("(\\s*,\\s*)(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", ",");

    // Convert the input to an array list.
    String[] membersArray = list.split(",");
    ArrayList<String> membersList = new ArrayList<String>();
    membersList.addAll(Arrays.asList(membersArray));

    return membersList;
  }

  /**
   * Return a random member from a list of members.
   */
  private String getRandomMember(ArrayList<String> members) {
    Random rand = new Random();
    String member = members.get(rand.nextInt(members.size()));
    return member;
  }
}