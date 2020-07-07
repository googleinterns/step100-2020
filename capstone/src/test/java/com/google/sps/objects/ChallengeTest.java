package com.google.sps.objects;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.Objects.Badge;
import com.google.sps.Objects.Challenge;

/**
 * This class tests the method in the Challenge class.
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
    badge = new Badge("Run 3 miles", null, 234567);
    challenge = new Challenge("Run 3 miles", 123456, badge, new ArrayList<String>());
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
  public void addUserCompletedTest() {
    challenge.addUserCompleted("1");
    challenge.addUserCompleted("2");
    List<String> completedUsers = challenge.getUsersCompleted();
    assert completedUsers.size() == 2;
    assertEquals(completedUsers.get(0), "1");
    assertEquals(completedUsers.get(1), "2");
  }

  @Test
  public void fromEntityTest() {
    Entity entity = new Entity("Challenge");
    //    entity.setProperty("usersCompleted", value);
    entity.setProperty("dueDate", 123456);
  }
}
