package com.google.sps.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.Objects.Badge;
import com.google.sps.Objects.response.MemberResponse;

/** Unit tests for MemberResponse. */
public class MemberResponseTest {

  private static final String PROFILE_PIC = "";
  private static final String FIRST_NAME = "Test";
  private static final String LAST_NAME = "User";
  private static final String USER_ID = "11111111";
  private static final String CHALLENGE_NAME = "workout";
  private static final long BADGE_TIMESTAMP = 2222222;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  private MemberResponse memberResponse;
  private Badge badge;

  @Before
  public void setUp() {
    helper.setUp();
    memberResponse =
        new MemberResponse(
            PROFILE_PIC, /* profilePic */
            FIRST_NAME, /* firstName */
            LAST_NAME, /* lastName */
            new LinkedHashSet<Badge>(), /* badges */
            USER_ID /*userId */);
  }

  @After
  public void tearDown() {
    helper.tearDown();
    memberResponse = null;
  }

  @Test
  public void getFirstNameTest() {
    assertEquals(memberResponse.getFirstName(), FIRST_NAME);
  }

  @Test
  public void getLastNameTest() {
    assertEquals(memberResponse.getLastName(), LAST_NAME);
  }

  @Test
  public void getStringURLTest() {
    assertEquals(memberResponse.getStringURL(), PROFILE_PIC);
  }

  @Test
  public void getBadgesTest_noBadge() {
    assertEquals(memberResponse.getBadges().size(), 0);
    badge =
        new Badge(
            /* badge id */ 0,
            /* challenge name */ CHALLENGE_NAME,
            /* icon */ null,
            /* timestamp */ BADGE_TIMESTAMP);
    memberResponse.getBadges().add(badge);
    assertEquals(memberResponse.getBadges().size(), 1);
  }

  @Test
  public void fromEntityTest() {
    Entity entity = new Entity("MemberResponse");
    entity.setProperty("profilePic", PROFILE_PIC);
    entity.setProperty("firstName", FIRST_NAME);
    entity.setProperty("lastName", LAST_NAME);
    entity.setProperty("userId", USER_ID);
    MemberResponse returnedMemberResponse =
        MemberResponse.fromEntity(entity, false /*includeBadges*/);

    assertTrue(returnedMemberResponse.equals(memberResponse));
  }
}
