package com.google.sps.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.Objects.Badge;
import com.google.sps.Objects.Challenge;

/**
 * Unit tests for {@link Challenge}.
 *
 * @author lucyqu
 */
public class ChallengeTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  private Challenge challenge;
  private Badge badge;

  @Before
  public void setUp() {
    helper.setUp();
    badge = new Badge(/* challenge name */ "Run 3 miles", /* icon */ null, /* timestamp */ 234567);
    challenge =
        new Challenge(
            "Run 3 miles", /* challenge name */
            123456, /* due date */
            badge, /* badge */
            new ArrayList<String>(), /* completed users */
            100 /* challenge id */);
  }

  @After
  public void tearDown() {
    helper.tearDown();
    challenge = null;
  }

  @Test
  public void getDueDateTest() {
    assert challenge.getDueDate() == 123456;
  }

  @Test
  public void getChallengeName() {
    assertEquals(challenge.getChallengeName(), "Run 3 miles");
  }

  @Test
  public void getBadgeTest() {
    assertEquals(challenge.getBadge(), badge);
  }

  @Test
  public void getUsersCompletedTest() {
    assert challenge.getUsersCompleted().size() == 0;
  }

  @Test
  public void addCompletedUserTest() {
    challenge.addCompletedUser("1");
    challenge.addCompletedUser("2");
    List<String> completedUsers = challenge.getUsersCompleted();

    assert completedUsers.size() == 2;
    assertEquals(completedUsers.get(0), "1");
    assertEquals(completedUsers.get(1), "2");
  }

  @Test
  public void getIsCompletedTest() {
    assertFalse(challenge.getIsCompleted("1"));
    challenge.addCompletedUser("1");
    assertTrue(challenge.getIsCompleted("1"));
    challenge.addCompletedUser("2");
    challenge.addCompletedUser("3");
    assertFalse(challenge.getIsCompleted("4"));
  }

  @Test
  public void fromEntityTest() {
    Entity entity = new Entity("Challenge");
    String challengeName = "Run half a marathon";
    entity.setProperty("name", challengeName);
    entity.setProperty("votes", new ArrayList<String>());
    long dueDate = 123456;
    entity.setProperty("dueDate", dueDate);
    Challenge returnedChallenge = Challenge.fromEntity(entity);

    assertEquals(returnedChallenge.getChallengeName(), challengeName);
    assert returnedChallenge.getUsersCompleted().size() == 0;
    assert returnedChallenge.getDueDate() == dueDate;
  }

  @Test
  public void toEntityTest() {
    Entity entity = challenge.toEntity();
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(entity);
    List<String> votes = (ArrayList<String>) entity.getProperty("votes");

    assertEquals(entity.getProperty("name"), "Run 3 miles");
    assert votes.size() == 0;
    assert (long) entity.getProperty("dueDate") == 123456;
  }
}
