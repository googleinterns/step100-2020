package com.google.sps.servlets;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;

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
import com.google.sps.Objects.Comment;

public class UpdateLikesServletTest {

  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";
  private static final String AUTHOR_ID = "123123123";
  private static final String POST_TEXT = "a great post";
  private static final String CHALLENGE_NAME = "run 4 miles";
  private static final String IMG = "";
  private static final long TIMESTAMP = 123123123;
  private static final long POST_ID = 1;

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
    AUTHOR_ID, /* authorId */ 
    POST_TEXT, /* postText */
    new ArrayList<Comment>(), /* comments */
    CHALLENGE_NAME, /* challengeName */
    TIMESTAMP, /* timestamp */
    IMG, /* img */
    new HashSet<String>() /* likes */);

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private DatastoreService datastore;
  private UpdateLikesServlet updateLikesServlet;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    populateDatabase(datastore);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    updateLikesServlet = new UpdateLikesServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
    responseWriter = null;
    datastore = null;
    updateLikesServlet = null;
  }

  @Test
  public void doPost_userNotLoggedIn() throws IOException, EntityNotFoundException {
    helper.setEnvIsLoggedIn(false);
    when(mockRequest.getParameter("id")).thenReturn(Long.toString(POST_ID));
    when(mockRequest.getParameter("liked")).thenReturn("true");

    updateLikesServlet.doPost(mockRequest, mockResponse);
    String response = responseWriter.toString();
    assertTrue(response.contains("Oops an error happened!"));
  }

  @Test
  public void doPost_invalidPost() throws IOException, EntityNotFoundException {
    when(mockRequest.getParameter("id")).thenReturn(Long.toString(5));
    when(mockRequest.getParameter("liked")).thenReturn("true");

    updateLikesServlet.doPost(mockRequest, mockResponse);
    String response = responseWriter.toString();
    assertThat(response).contains("error");
  }

  private void populateDatabase(DatastoreService datastore) {
    // Add test data.
    datastore.put(POST_1.createPostEntity());
  }

  @Test
  public void doPost_addLike() throws Exception {
    when(mockRequest.getParameter("id")).thenReturn(Long.toString(POST_ID));
    when(mockRequest.getParameter("liked")).thenReturn("true");

    updateLikesServlet.doPost(mockRequest, mockResponse);

    Key postKey = KeyFactory.createKey("Post", POST_ID);
    Entity post = datastore.get(postKey);

    POST_1.getLikes().add(USER_ID);

    String jsonOriginal = new Gson().toJson(POST_1);
    String jsonStored = new Gson().toJson(Post.getPostEntity(post));
    assertEquals(jsonOriginal, jsonStored);
  }

  @Test
  public void doPost_removeLike() throws Exception {
    POST_1.getLikes().add(USER_ID);
    POST_1.getLikes().add("test 2");

    when(mockRequest.getParameter("id")).thenReturn(Long.toString(POST_ID));
    when(mockRequest.getParameter("liked")).thenReturn("false");

    updateLikesServlet.doPost(mockRequest, mockResponse);

    Key postKey = KeyFactory.createKey("Post", POST_ID);
    Entity post = datastore.get(postKey);

    POST_1.getLikes().remove(USER_ID);

    String jsonOriginal = new Gson().toJson(POST_1);
    String jsonStored = new Gson().toJson(Post.getPostEntity(post));
    assertEquals(jsonOriginal, jsonStored);
  }
}