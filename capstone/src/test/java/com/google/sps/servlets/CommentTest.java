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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.Objects.Comment;

/**
 * Unit tests for Comment.
 *
 */
public class CommentTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  private Comment comment;

  @Before
  public void setUp() {
    helper.setUp();
    comment = 
      new Comment(
        4324344, /* userId */ 
        "a great comment", /* commentText */ 
        "123123123" /* userId */ );
  }

  @After
  public void tearDown() {
    helper.tearDown();
    comment = null;
  }

  @Test
  public void getCommentTextTest() {
    assertEquals(comment.getCommentText(), "a great comment");
  }

  @Test
  public void getUserTest() {
    assertEquals(comment.getUser(), "123123123");
  }

  @Test
  public void getCommentEntityTest() {
    EmbeddedEntity entity = new EmbeddedEntity();
    String commentText = "a great comment";
    String userId = "123123123";
    long timestamp = 4324344;
    entity.setProperty("userId", userId);
    entity.setProperty("commentText", commentText);
    entity.setProperty("timestamp", timestamp);
    Comment returnedComment = Comment.getCommentEntity(entity);

    assertEquals(returnedComment.getCommentText(), commentText);
    assertEquals(returnedComment.getUser(), userId);
    assert returnedComment.getTimestamp() == timestamp;
  }

  @Test
  public void toEntityTest() {
    EmbeddedEntity entity = comment.toEntity(comment.getCommentText(), comment.getUser());
    ArrayList<EmbeddedEntity> allComments = new ArrayList<>();
    allComments.add(entity);
    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("comments", allComments);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    ArrayList<EmbeddedEntity> commentEntitys = (ArrayList<EmbeddedEntity>) commentEntity.getProperty("comments");
    assertEquals(commentEntitys.get(0).getProperty("commentText"), "a great comment");
    assertEquals(commentEntitys.get(0).getProperty("userId"), "123123123");
  }
}
