package com.google.sps.servlets;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableMap;
import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import com.google.gson.Gson;
import com.google.sps.Objects.TFIDFStringHelper;
import com.google.sps.Objects.Group;
import com.google.sps.Objects.Tag;
import com.google.sps.Objects.Challenge;
import com.google.sps.Objects.Post;
import com.google.sps.Objects.Comment;
import com.google.sps.Objects.response.TagContextResponse;


/**
 * Unit tests for {@link TagsTFIDFServlet}.
 */
 @RunWith(JUnit4.class)
public class TagsContextServletTest {
  private final long GROUP_ID = 10;
  private final String USER_ID = "User";
  private final String USER_EMAIL = "test@test.com";

  private final String POST_TEXT_1 = "This is a great post!";
  private final String POST_TEXT_2 = "This is another post!";
  private final String CHALLENGE_NAME = "Not a Post.";
  private final String IMG = "";
  private final long TIMESTAMP = 123123123;

  private final Post POST_1 = 
      new Post(
          0, /* postId */
          USER_ID, /* authorId */
          "", /* authorPic */
          POST_TEXT_1, /* postText */
          new ArrayList<Comment>(), /* comments */
          CHALLENGE_NAME, /* challengeName */
          TIMESTAMP, /* timestamp */
          IMG, /* img */
          new HashSet<String>() /* likes */);
  
  private final Post POST_2 = 
      new Post(
          0, /* postId */
          USER_ID, /* authorId */
          "", /* authorPic */
          POST_TEXT_2, /* postText */
          new ArrayList<Comment>(), /* comments */
          CHALLENGE_NAME, /* challengeName */
          TIMESTAMP, /* timestamp */
          IMG, /* img */
          new HashSet<String>() /* likes */);

  private final Challenge CHALLENGE_1 = 
      new Challenge(
          CHALLENGE_NAME, /* challengeName */
          12345, /* dueDate */
          null, /* badge */
          new ArrayList<String>(), /* usersCompleted */
          0 /* id */);

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
              new LocalDatastoreServiceTestConfig()
                  .setDefaultHighRepJobPolicyUnappliedJobPercentage(0),
              new LocalUserServiceTestConfig())
          .setEnvEmail(USER_EMAIL)
          .setEnvIsLoggedIn(true)
          .setEnvAuthDomain("gmail.com")
          .setEnvAttributes(
              new HashMap(
                  ImmutableMap.of(
                      "com.google.appengine.api.users.UserService.user_id_key", USER_ID)));

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private DatastoreService datastore;
  private TagsContextServlet tagsContextServlet;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();

    datastore = DatastoreServiceFactory.getDatastoreService();
    populateDatabase(datastore);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    tagsContextServlet = new TagsContextServlet();
  }

  @After
  public void tearDown() {
    // tear down work
  }

  @Test
  public void doGet_returnOnePost() throws Exception {
    when(mockRequest.getParameter("groupId")).thenReturn(Long.toString(GROUP_ID));
    when(mockRequest.getParameter("tag")).thenReturn("a great");

    tagsContextServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains(POST_TEXT_1) && 
              !response.contains(POST_TEXT_2));
  }

  @Test
  public void doGet_returnAll() throws Exception {
    when(mockRequest.getParameter("groupId")).thenReturn(Long.toString(GROUP_ID));
    when(mockRequest.getParameter("tag")).thenReturn("post");

    tagsContextServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains(POST_TEXT_1) && 
               response.contains(POST_TEXT_2) &&
               response.contains(CHALLENGE_NAME));
  }

  @Test
  public void doGet_returnNone() throws Exception {
    when(mockRequest.getParameter("groupId")).thenReturn(Long.toString(GROUP_ID));
    when(mockRequest.getParameter("tag")).thenReturn("comment");

    tagsContextServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(!response.contains(POST_TEXT_1) && 
               !response.contains(POST_TEXT_2));
  }

  private void populateDatabase(DatastoreService datastore) {
    Entity groupEntity = new Entity("Group", GROUP_ID);
    groupEntity.setProperty("groupName", "Name");
    groupEntity.setProperty("headerImg", "");
    groupEntity.setProperty("memberIds", new ArrayList<String>());
    groupEntity.setProperty("posts", addPosts());
    groupEntity.setProperty("options", new ArrayList<Long>());
    groupEntity.setProperty("challenges", addChallenges());
    groupEntity.setProperty("tags", new ArrayList<Tag>());
    datastore.put(groupEntity);
  }

  private ArrayList<Long> addPosts() {
    ArrayList<Long> postIds = new ArrayList<>();

    Entity postEntity1 = POST_1.toEntity();
    Entity postEntity2 = POST_2.toEntity();

    datastore.put(postEntity1);
    datastore.put(postEntity2);

    postIds.add(postEntity1.getKey().getId());
    postIds.add(postEntity2.getKey().getId());

    return postIds;
  }

  private ArrayList<Long> addChallenges() {
    ArrayList<Long> challengeIds = new ArrayList<>();

    Entity challengeEntity = CHALLENGE_1.toEntity();

    datastore.put(challengeEntity);

    challengeIds.add(challengeEntity.getKey().getId());

    return challengeIds;
  }
} 