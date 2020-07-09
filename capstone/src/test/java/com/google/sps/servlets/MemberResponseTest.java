package com.google.sps.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.gson.Gson;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.Objects.response.MemberResponse;
import com.google.sps.Objects.Badge;

/**
 * Unit tests for MemberResponse.
 *
 */
public class MemberResponseTest {

  private static final String PROFILE_PIC = "";
  private static final String FIRST_NAME = "Test";
  private static final String LAST_NAME = "User";
  private static final String USER_ID = "11111111";

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
  public void getBadgesTest() {
    assert memberResponse.getBadges().size() == 0;
    badge = new Badge(/* challenge name */ "workout", /* icon */ null, /* timestamp */ 11111111);
    memberResponse.getBadges().add(badge);
    assert memberResponse.getBadges().size() == 1;
  }

  @Test
  public void fromEntityTest() {
    Entity entity = new Entity("MemberResponse");
    entity.setProperty("profilePic", PROFILE_PIC);
    entity.setProperty("firstName", FIRST_NAME);
    entity.setProperty("lastName", LAST_NAME);
    entity.setProperty("userId", USER_ID);
    MemberResponse returnedMemberResponse = MemberResponse.fromEntity(entity, false /*includeBadges*/);
    
    assertTrue(returnedMemberResponse.equals(memberResponse));
  } 
}


