package com.google.sps.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashSet;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.Objects.Badge;
import com.google.sps.Objects.response.MemberResponse;
import com.google.sps.Objects.Post;
import com.google.sps.Objects.User;
import com.google.sps.Objects.Badge;

/** Unit tests for MemberResponse. */
public class MemberResponseTest {

  private static final String PROFILE_PIC = "";
  private static final String FIRST_NAME = "Test";
  private static final String LAST_NAME = "User";
  private static final String USER_EMAIL = "test@mctest.com";
  private static final String USER_ID = "11111111";
  private static final String CHALLENGE_NAME = "workout";
  private static final long BADGE_TIMESTAMP = 2222222;
  private static final long BADGE_ID = 12;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  private static final User CURRENT_USER =
      new User(
          USER_ID,
          FIRST_NAME,
          LAST_NAME,
          USER_EMAIL,
          /* phoneNumber= */ "123-456-7890",
          /* profilePic= */ "",
          /* address= */ "",
          /* latitude= */ 0,
          /* longitude= */ 0,
          /* badges= */ new LinkedHashSet<Badge>(),
          /* groups= */ new LinkedHashSet<Long>(),
          /* interests= */ new ArrayList<String>());

  private MemberResponse memberResponse;
  private Badge badge;
  private DatastoreService datastore;

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(CURRENT_USER.toEntity());
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

    badge = new Badge(
      /* badge id */ BADGE_ID,
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
