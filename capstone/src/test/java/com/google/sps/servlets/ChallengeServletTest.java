package com.google.sps.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.sps.Objects.Challenge;

public class ChallengeServletTest {

  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";
  private static final String NEW_CHALLENGE = "Bike 20 miles";
  private static final String CHALLENGE_NAME = "Run";
  private static final long CHALLENGE_ID = 2;
  private static final long DUE_DATE = 2594865260645L;
  private static final long PAST_DUE_DATE = 1234;
  private static final String GROUP_NAME = "Runners Club";
  private static final String GROUP_ID = "1";
  private static final List<Long> CHALLENGE_IDS = new ArrayList<Long>(Arrays.asList(2L));

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

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  @Spy private ChallengeServlet challengeServlet;
  private StringWriter responseWriter;
  private DatastoreService datastore;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
  }

  @After
  public void tearDown() {
    helper.tearDown();
    responseWriter = null;
    datastore = null;
    challengeServlet = null;
  }

  /**
   * Creates challenge entity.
   *
   * @param text challenge name
   * @return entity
   */
  private Entity createChallenge(String text, long dueDate) {
    Entity challengeEntity = new Entity("Challenge");
    challengeEntity.setProperty("name", text);
    challengeEntity.setProperty("dueDate", dueDate);
    challengeEntity.setProperty("votes", null);
    challengeEntity.setProperty("timestamp", System.currentTimeMillis());
    return challengeEntity;
  }

  private Entity createGroup(String userId, String groupName) {
    ArrayList<String> members = new ArrayList<String>();
    members.add(userId);
    Entity groupEntity = new Entity("Group");
    groupEntity.setProperty("memberIds", members);
    groupEntity.setProperty("challenges", null);
    groupEntity.setProperty("posts", null);
    groupEntity.setProperty("options", new ArrayList<Long>());
    groupEntity.setProperty("groupName", groupName);
    groupEntity.setProperty("headerImg", "");
    return groupEntity;
  }

  @Test
  public void doGet_noChallenges() throws IOException {
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME);
    datastore.put(groupEntity);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);

    challengeServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();
    String emptyString = "\"\"\n";

    assertEquals(emptyString, response);
  }

  @Test
  public void doGet_withChallenge() throws IOException {
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME);
    groupEntity.setProperty("challenges", CHALLENGE_IDS);
    datastore.put(groupEntity);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);
    Entity challengeEntity = this.createChallenge(CHALLENGE_NAME, DUE_DATE);
    datastore.put(challengeEntity);

    challengeServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains(CHALLENGE_NAME));
    assertTrue(response.contains(String.valueOf(DUE_DATE)));
    assertTrue(response.contains("false"));
  }

  @Test
  public void doGet_withChallenge_passedDueDate() throws IOException {
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME);
    groupEntity.setProperty("challenges", CHALLENGE_IDS);
    datastore.put(groupEntity);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);
    Entity challengeEntity = this.createChallenge(CHALLENGE_NAME, PAST_DUE_DATE);
    datastore.put(challengeEntity);

    challengeServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();
    String emptyString = "\"\"\n";

    assertEquals(emptyString, response);
  }

  @Test
  public void doGet_userNotLoggedIn() throws IOException {
    helper.setEnvIsLoggedIn(false);

    challengeServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains("Oops an error happened!"));
  }

  @Test
  public void doPost_userLoggedIn() throws IOException, EntityNotFoundException {
    when(mockRequest.getParameter("name")).thenReturn(NEW_CHALLENGE);
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME);
    datastore.put(groupEntity);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);

    challengeServlet.doPost(mockRequest, mockResponse);
    Key challengeKey = KeyFactory.createKey("Challenge", CHALLENGE_ID);
    Entity entity = datastore.get(challengeKey);
    long id = entity.getKey().getId();
    Challenge challenge =
        new Challenge(
            NEW_CHALLENGE, /* challenge name*/
            DUE_DATE, /* due date */
            null, /* badge */
            null, /* users completed */
            id /* id of challenge */);
    Challenge returnedChallenge = Challenge.fromEntity(entity);

    assertEquals(challenge.getChallengeName(), returnedChallenge.getChallengeName());
  }

  @Test
  public void updateChallengesListTest() throws IOException, EntityNotFoundException {
    Entity challengeEntity = this.createChallenge(CHALLENGE_NAME, DUE_DATE);
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME);
    datastore.put(groupEntity);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);

    challengeServlet.updateChallengesList(mockRequest, mockResponse, datastore, challengeEntity);
    Key groupKey = KeyFactory.createKey("Group", Integer.parseInt(GROUP_ID));
    Entity returnedEntity = datastore.get(groupKey);
    List<Long> returnedChallenges = (ArrayList<Long>) returnedEntity.getProperty("challenges");

    assertEquals(returnedChallenges.size(), 1);
    // Cannot use assertEquals for this assertion because returning long
    assert returnedChallenges.get(0) == 0L;
  }

  @Test(expected = EntityNotFoundException.class)
  public void doPost_userNotLoggedIn() throws IOException, EntityNotFoundException {
    helper.setEnvIsLoggedIn(false);
    when(mockRequest.getParameter("name")).thenReturn(CHALLENGE_NAME);

    challengeServlet.doPost(mockRequest, mockResponse);
    Key key = KeyFactory.createKey("Challenge", CHALLENGE_ID);
    // trigger EntityNotfoundException
    Entity challengeEntity = datastore.get(key);
  }
}
