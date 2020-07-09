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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.sps.Objects.response.MemberResponse;
import com.google.sps.Objects.Badge;

/**
 * Unit tests for MemberResponse.
 *
 */
public class MemberResponseTest {
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  private MemberResponse memberResponse;

  @Before
  public void setUp() {
    helper.setUp();
    memberResponse = 
      new MemberResponse(
        "", /* profilePic */ 
        "Test", /* firstName */
        "User", /* lastName */
        new LinkedHashSet<Badge>(), /* badges */
        "11111111" /*userId */);
  }

  @After
  public void tearDown() {
    helper.tearDown();
    memberResponse = null;
  }

  @Test
  public void getFirstNameTest() {
    assertEquals(memberResponse.getFirstName(), "Test");
  }

  @Test
  public void getLastNameTest() {
    assertEquals(memberResponse.getLastName(), "User");
  }

  @Test
  public void getStringURLTest() {
    assertEquals(memberResponse.getStringURL(), "");
  }

  @Test
  public void getBadgesTest() {
    assert memberResponse.getBadges().size() == 0;
    Badge badge = new Badge(/* challenge name */ "workout", /* icon */ null, /* timestamp */ 11111111);
    memberResponse.getBadges().add(badge);
    assert memberResponse.getBadges().size() == 1;
  }

  @Test
  public void fromEntityTest() {
    Entity entity = new Entity("MemberResponse");
    String profilePic = "";
    String firstName = "Test";
    String lastName = "User";
    String userId = "11111111";
    entity.setProperty("profilePic", profilePic);
    entity.setProperty("firstName", firstName);
    entity.setProperty("lastName", lastName);
    entity.setProperty("userId", userId);
    MemberResponse returnedMemberResponse = MemberResponse.fromEntity(entity, false);

    assertEquals(returnedMemberResponse.getFirstName(), firstName);
    assertEquals(returnedMemberResponse.getLastName(), lastName);
    assertEquals(returnedMemberResponse.getStringURL(), profilePic);
    assertEquals(returnedMemberResponse.getUserId(), userId);
  } 
}


