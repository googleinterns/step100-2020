package com.google.sps.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.gson.Gson;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.Objects.Comment;
import com.google.sps.Objects.Post;
import com.google.sps.Objects.User;
import com.google.sps.Objects.Badge;

/**
 * Unit tests for Comment.
 *
 */
public class CommentTest {

  private static final String USER_ID = "123123123";
  private static final String USER_EMAIL = "test@mctest.com";
  private static final String COMMENT_TEXT = "a great comment";
  private static final long TIMESTAMP = 4324344;
  private static final String PROFILE_PIC = "";

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

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

  private Comment comment;
  private DatastoreService datastore;

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(CURRENT_USER.toEntity());
    comment = 
      new Comment(
        TIMESTAMP, /* timestamp */ 
        COMMENT_TEXT, /* commentText */ 
        USER_ID, /* userId */ 
        PROFILE_PIC /* userProfilePic */);
  }

  @After
  public void tearDown() {
    helper.tearDown();
    comment = null;
  }

  @Test
  public void getCommentTextTest() {
    assertEquals(comment.getCommentText(), COMMENT_TEXT);
  }

  @Test
  public void getUserTest() {
    assertEquals(comment.getUser(), USER_ID);
  }

  @Test
  public void toAndFromCommentEntityTest() {
    EmbeddedEntity entity = comment.toEntity();
    Comment returnedComment = Comment.fromEntity(entity);

    assertTrue(returnedComment.getCommentText().equals(COMMENT_TEXT));
    assertTrue(returnedComment.getUser().equals(USER_ID));
    assertTrue(returnedComment.getProfilePic().equals(PROFILE_PIC));
  }
}
