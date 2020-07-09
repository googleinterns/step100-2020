package com.google.sps.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.Objects.Post;
import com.google.sps.Objects.Comment;

/**
 * Unit tests for Post.
 *
 */
public class PostTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  private Post post;

  @Before
  public void setUp() {
    helper.setUp();
    post = 
      new Post(
        4324344, /* postId */ 
        "32493432", /* authorId */ 
        "a great post", /* postText */
        new ArrayList<Comment>(), /* comments */
        "run 4 miles", /* challengeName */
        123123123, /* timestamp */
        "", /* img */
        new HashSet<String>() /* likes */);
  }

  @After
  public void tearDown() {
    helper.tearDown();
    post = null;
  }

  @Test
  public void getPostTextTest() {
    assertEquals(post.getPostText(), "a great post");
  }

  @Test 
  public void getImgtTest() {
    assertEquals(post.getImg(), "");
  }

  @Test
  public void getChallengeNameTest() {
    assertEquals(post.getChallengeName(), "run 4 miles");
  }

  @Test
  public void getCommentsTest() {
    Comment testComment1 =  
      new Comment(
        4324344, /* userId */ 
        "a great comment", /* commentText */ 
        "123123123" /* userId */ );
    Comment testComment2 = 
      new Comment(
        55555555, /* userId */ 
        "another great comment", /* commentText */ 
        "09090909" /* userId */ );
    post.addComment(testComment1);
    post.addComment(testComment2);

    ArrayList<Comment> testComments = new ArrayList<Comment>();
    testComments.add(testComment1);
    testComments.add(testComment2);

    assertEquals(
      post.getComments().get(0).getCommentText(), 
      testComments.get(0).getCommentText()
    );
    assertEquals(
      post.getComments().get(1).getCommentText(), 
      testComments.get(1).getCommentText()
    );
  }

  @Test
  public void getLikesTest() {
    assert post.getLikes().size() == 0;
    post.addLike("user 1");
    post.addLike("user 2");
    HashSet<String> testLikes = new HashSet<String>();
    testLikes.add("user 1");
    testLikes.add("user 2");
    
    assert testLikes.containsAll(post.getLikes());
  }

  @Test
  public void addCommentTest() {
    Comment testComment1 =  
      new Comment(
        4324344, /* userId */ 
        "a great comment", /* commentText */ 
        "123123123" /* userId */ );
    Comment testComment2 = 
      new Comment(
        55555555, /* userId */ 
        "another great comment", /* commentText */ 
        "09090909" /* userId */ );
    post.addComment(testComment1);
    post.addComment(testComment2);

    assert post.getComments().size() == 2;
    assertEquals(
      post.getComments().get(0).getCommentText(), "a great comment"
    );
    assertEquals(
      post.getComments().get(1).getCommentText(), "another great comment"
    );
  }

  @Test
  public void addLikeTest() {
    post.addLike("user 1");
    post.addLike("user 2");
    HashSet<String> likes = post.getLikes();

    assert likes.size() == 2;
    assert likes.contains("user 1");
    assert likes.contains("user 2");
  }

  @Test
  public void removeLikeTest() {
    post.addLike("test user 1");
    post.addLike("test user 2");
    HashSet<String> likes = post.getLikes();
    assert likes.size() == 2;

    post.removeLike("test user 1");
    likes = post.getLikes();

    assert likes.size() == 1;
    assert likes.contains("test user 2");

    post.removeLike("test user 2");
    likes = post.getLikes();

    assert likes.size() == 0;
  }
}