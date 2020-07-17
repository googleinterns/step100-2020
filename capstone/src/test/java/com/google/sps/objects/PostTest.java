package com.google.sps.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.sps.Objects.Comment;
import com.google.sps.Objects.Post;

/** Unit tests for Post. */
public class PostTest {

  private static final String POST_TEXT = "a great post";
  private static final String CHALLENGE_NAME = "run 4 miles";
  private static final String IMG = "";
  private static final long TIMESTAMP = 123123123;
  private static final long POST_ID = 4324344;
  private static final String USER_EMAIL = "test@mctest.com";
  private static final String USER_ID = "testy-mc-test";

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

  private Post post;
  private DatastoreService datastore;

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    post =
        new Post(
            /* postId */ POST_ID,
            /* authorId */ USER_ID,
            /* postText */ POST_TEXT,
            /* comments */ new ArrayList<Comment>(),
            /* challengeName */ CHALLENGE_NAME,
            /* timestamp */ TIMESTAMP,
            /* img */ IMG,
            /* likes */ new HashSet<String>());
  }

  @After
  public void tearDown() {
    helper.tearDown();
    post = null;
  }

  @Test
  public void getPostTextTest() {
    assertEquals(post.getPostText(), POST_TEXT);
  }

  @Test
  public void getImgtTest() {
    assertEquals(post.getImg(), IMG);
  }

  @Test
  public void getChallengeNameTest() {
    assertEquals(post.getChallengeName(), CHALLENGE_NAME);
  }

  @Test
  public void getCommentsTest() {
    assertEquals(post.getComments().size(), 0);
  }

  @Test
  public void getLikesTest() {
    assertEquals(post.getLikes().size(), 0);
    post.addLike("user 1");
    post.addLike("user 2");
    HashSet<String> testLikes = new HashSet<String>();
    testLikes.add("user 1");
    testLikes.add("user 2");

    assertEquals(testLikes, post.getLikes());
  }

  @Test
  public void addCommentTest() {
    Comment testComment1 =
        new Comment(
            /* userId */ 4324344, /* commentText */ "a great comment", /* userId */ "123123123");
    Comment testComment2 =
        new Comment(
            55555555, /* userId */
            "another great comment", /* commentText */
            "09090909" /* userId */);
    post.addComment(testComment1);
    post.addComment(testComment2);

    assertEquals(post.getComments().size(), 2);
    assertTrue(equalCommentCheck(post.getComments().get(0), testComment1));
    assertTrue(equalCommentCheck(post.getComments().get(1), testComment2));
  }

  public boolean equalCommentCheck(Comment comment1, Comment comment2) {
    if (!(comment1 instanceof Comment) || !(comment2 instanceof Comment)) {
      return false;
    }
    return comment1.getCommentText().equals(comment2.getCommentText())
        && comment1.getUser().equals(comment2.getUser());
  }

  @Test
  public void addLikeTest() {
    post.addLike("user 1");
    post.addLike("user 2");
    HashSet<String> likes = post.getLikes();

    assertEquals(likes.size(), 2);
    assertTrue(likes.contains("user 1"));
    assertTrue(likes.contains("user 2"));
  }

  @Test
  public void removeLikeOnceTest() {
    post.addLike("test user 1");
    HashSet<String> likes = post.getLikes();
    assertEquals(likes.size(), 1);

    post.removeLike("test user 1");

    likes = post.getLikes();
    assertEquals(likes.size(), 0);
  }

  @Test
  public void removeLikeMultipleTest() {
    post.addLike("test user 1");
    post.addLike("test user 2");
    HashSet<String> likes = post.getLikes();
    assertEquals(likes.size(), 2);

    post.removeLike("test user 1");
    likes = post.getLikes();
    assertEquals(likes.size(), 1);
    assertTrue(likes.contains("test user 2"));
  }

  //  @Test
  //  public void toAndFromPostEntityTest() throws Exception {
  //    Entity entity = post.toEntity();
  //    datastore.put(entity);
  //
  //    Key postKey = KeyFactory.createKey("Post", POST_ID);
  //    Entity retreivedPost = datastore.get(postKey);
  //
  //    Post returnedPost = Post.fromEntity(retreivedPost);
  //
  //    assertTrue(returnedPost.equals(post));
  //  }
}
