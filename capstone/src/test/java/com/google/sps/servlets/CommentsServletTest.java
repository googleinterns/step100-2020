package com.google.sps.servlets;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
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
import com.google.common.collect.ImmutableMap;
import com.google.sps.Objects.Comment;
import com.google.sps.Objects.Post;
import com.google.sps.Objects.User;
import com.google.sps.Objects.Badge;

public class CommentsServletTest {

  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";
  private static final String AUTHOR_ID = "123123123";
  private static final String POST_TEXT = "a great post";
  private static final String CHALLENGE_NAME = "run 4 miles";
  private static final String IMG = "";
  private static final long TIMESTAMP = 123123123;
  private static final long POST_ID = 1;
  private static final String COMMENT_TEXT = "this is a comment";

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

  private static final User CURRENT_USER =
      new User(
          USER_ID,
          "Test",
          "McTest",
          USER_EMAIL,
          /* phoneNumber= */ "123-456-7890",
          /* profilePic= */ "",
          /* address= */ "",
          /* latitude= */ 0,
          /* longitude= */ 0,
          /* badges= */ new LinkedHashSet<Badge>(),
          /* groups= */ new LinkedHashSet<Long>(),
          /* interests= */ new ArrayList<String>());

  private final Post POST_1 =
      new Post(
          POST_ID, /* postId */
          AUTHOR_ID, /* authorId */
          "TEST USER", /* authorName */
          "", /* authorPic */
          POST_TEXT, /* postText */
          new ArrayList<Comment>(), /* comments */
          CHALLENGE_NAME, /* challengeName */
          TIMESTAMP, /* timestamp */
          IMG, /* img */
          new HashSet<String>() /* likes */);

  private final Comment COMMENT_1 =
      new Comment(
          System.currentTimeMillis(), /* timestamp */
          COMMENT_TEXT, /* commentText */
          USER_ID, /* userId */
          "" /* userProfilePic */);

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private DatastoreService datastore;
  private CommentsServlet commentServlet;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    populateDatabase(datastore);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    commentServlet = new CommentsServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
    responseWriter = null;
    datastore = null;
    commentServlet = null;
  }

  private void populateDatabase(DatastoreService datastore) {
    // Add test data.
    datastore.put(CURRENT_USER.toEntity());
    datastore.put(POST_1.toEntity());
  }

  @Test
  public void doPost_addComment() throws Exception {
    when(mockRequest.getParameter("id")).thenReturn(Long.toString(POST_ID));
    when(mockRequest.getParameter("comment-text")).thenReturn(COMMENT_TEXT);

    commentServlet.doPost(mockRequest, mockResponse);

    Key postKey = KeyFactory.createKey("Post", POST_ID);
    Entity post = datastore.get(postKey);

    Post returnedPost = Post.fromEntity(post);
    assertTrue(returnedPost.getComments().get(0).getCommentText().equals(COMMENT_TEXT));
  }

  @Test
  public void doPost_userNotLoggedIn() throws IOException, EntityNotFoundException {
    helper.setEnvIsLoggedIn(false);
    when(mockRequest.getParameter("id")).thenReturn(Long.toString(POST_ID));
    when(mockRequest.getParameter("comment-text")).thenReturn(COMMENT_TEXT);

    commentServlet.doPost(mockRequest, mockResponse);
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(mockResponse).sendRedirect(captor.capture());

    assertEquals("/_ah/login?continue=%2F", captor.getValue());
  }

  @Test
  public void doPost_invalidPost() throws IOException, EntityNotFoundException {
    when(mockRequest.getParameter("id")).thenReturn(Long.toString(5));
    when(mockRequest.getParameter("comment-text")).thenReturn(COMMENT_TEXT);

    commentServlet.doPost(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertThat(response).contains("error");
  }
}
