package com.google.sps.servlets;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static com.google.sps.utils.TestUtils.assertEqualsJson;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.sps.Objects.Post;
import com.google.sps.Objects.Group;
import com.google.sps.Objects.Comment;
import com.google.sps.Objects.Challenge;
import com.google.sps.Objects.User;
import com.google.sps.Objects.Badge;

public class GroupPostDataServletTest {

  private static final String POST_TEXT = "a great post";
  private static final String CHALLENGE_NAME = "run 4 miles";
  private static final String IMG = "";
  private static final long TIMESTAMP = 123123123;
  private static final long POST_ID = 2;
  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";
  private static final String GROUP_1_ID = "1";
  private static final String GROUP_NAME = "The 3 Musketeers";
  private static final String HEADER_IMAGE = "";
 
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

  private final Post POST_1 = new Post(
    POST_ID, /* postId */ 
    USER_ID, /* authorId */ 
    POST_TEXT, /* postText */
    new ArrayList<Comment>(), /* comments */
    CHALLENGE_NAME, /* challengeName */
    TIMESTAMP, /* timestamp */
    IMG, /* img */
    new HashSet<String>() /* likes */);

  private static final User CURRENT_USER = new User(USER_ID, "Test", "McTest", 
    USER_EMAIL, 
    /* phoneNumber= */ "123-456-7890", 
    /* profilePic= */ "", 
    /* badges= */ new LinkedHashSet<Badge>(), 
    /* groups= */ new LinkedHashSet<Long>(), 
    /* interests= */ new ArrayList<String>());

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private DatastoreService datastore;
  private GroupPostDataServlet groupPostDataServlet;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    populateDatabase(datastore);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    groupPostDataServlet = new GroupPostDataServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
    responseWriter = null;
    datastore = null;
    groupPostDataServlet = null;
  }

  private void populateDatabase(DatastoreService datastore) {
    // Add test data.
    CURRENT_USER.addGroup(Long.parseLong(GROUP_1_ID));
    datastore.put(CURRENT_USER.toEntity());
    datastore.put(createGroupEntity());
    datastore.put(POST_1.toEntity());
  }

  /* Create a Group entity */
  private Entity createGroupEntity() {
    Entity groupEntity = new Entity("Group");
    groupEntity.setProperty("groupName", GROUP_NAME);
    groupEntity.setProperty("headerImg", HEADER_IMAGE);
    groupEntity.setProperty("memberIds", new ArrayList<String>(Arrays.asList(USER_ID)));
    groupEntity.setProperty("posts", new ArrayList<Long>(Arrays.asList(POST_ID)));
    groupEntity.setProperty("options", new ArrayList<Long>());
    groupEntity.setProperty("challenges", new ArrayList<Challenge>());
    return groupEntity;
  }

  @Test
  public void doGet_userLoggedIn() throws Exception {
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_1_ID);

    groupPostDataServlet.doGet(mockRequest, mockResponse);

    String response = responseWriter.toString();
    assertTrue(response.contains(POST_TEXT));
  } 

  @Test
  public void doGet_userNotLoggedIn() throws Exception {
    helper.setEnvIsLoggedIn(false);

    groupPostDataServlet.doGet(mockRequest, mockResponse);
    
    String response = responseWriter.toString();
    assertTrue(response.contains("Oops an error happened!"));
  }

  @Test
  public void doPost_userNotLoggedIn() throws Exception {
    helper.setEnvIsLoggedIn(false);

    groupPostDataServlet.doPost(mockRequest, mockResponse);
    
    String response = responseWriter.toString();
    assertTrue(response.contains("Oops an error happened!"));
  }
}