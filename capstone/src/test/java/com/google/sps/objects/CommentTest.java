package com.google.sps.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;

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

/**
 * Unit tests for Comment.
 *
 */
public class CommentTest {

  private static final String USER_ID = "123123123";
  private static final String COMMENT_TEXT = "a great comment";
  private static final long TIMESTAMP = 4324344;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  private Comment comment;
  private DatastoreService datastore;

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    comment = 
      new Comment(
        TIMESTAMP, /* timestamp */ 
        COMMENT_TEXT, /* commentText */ 
        USER_ID /* userId */ );
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
  }
}
