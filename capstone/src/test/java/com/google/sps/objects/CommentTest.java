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

  @Before
  public void setUp() {
    helper.setUp();
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
  public void getCommentEntityTest() {
    EmbeddedEntity entity = new EmbeddedEntity();
    entity.setProperty("userId", USER_ID);
    entity.setProperty("commentText", COMMENT_TEXT);
    entity.setProperty("timestamp", TIMESTAMP);
    
    Comment returnedComment = Comment.getCommentEntity(entity);

    String jsonRetrieved = new Gson().toJson(returnedComment);
    String jsonOriginal = new Gson().toJson(comment);
    assertTrue(jsonRetrieved.equals(jsonOriginal));
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

    assertEquals(commentEntitys.get(0).getProperty("commentText"), COMMENT_TEXT);
    assertEquals(commentEntitys.get(0).getProperty("userId"), USER_ID);
  }
}
