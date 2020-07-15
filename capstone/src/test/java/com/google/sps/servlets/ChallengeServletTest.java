package com.google.sps.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.sps.Objects.Challenge;

public class ChallengeServletTest {

  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";
  private static final String NEW_CHALLENGE = "Bike 20 miles";
  private static final String CHALLENGE_NAME = "Run";
  private static final long CHALLENGE_ID = 2;
  private static final long DUE_DATE = 12345;

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

    // Add test data
    ImmutableList.Builder<Entity> challenge = ImmutableList.builder();
    challenge.add(createChallenge(CHALLENGE_NAME));
    datastore.put(challenge.build());

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
  }

  /**
   * Creates challenge entity.
   *
   * @param text challenge name
   * @return entity
   */
  private Entity createChallenge(String text) {
    Entity challengeEntity = new Entity("Challenge");
    challengeEntity.setProperty("name", text);
    challengeEntity.setProperty("dueDate", DUE_DATE);
    challengeEntity.setProperty("votes", new ArrayList<String>());
    challengeEntity.setProperty("timestamp", System.currentTimeMillis());
    return challengeEntity;
  }

  @After
  public void tearDown() {
    helper.tearDown();
    responseWriter = null;
    datastore = null;
    challengeServlet = null;
  }

  @Test
  public void doGet_testChallengeName() throws IOException {
    challengeServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains(CHALLENGE_NAME));
  }

  @Test
  public void doGet_testUserNotCompleted() throws IOException {
    challengeServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains("false"));
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

    challengeServlet.doPost(mockRequest, mockResponse);
    // id of Challenge increments for each new Challenge that is added
    Key challengeKey = KeyFactory.createKey("Challenge", CHALLENGE_ID);
    Entity entity = datastore.get(challengeKey);
    long id = entity.getKey().getId();
    Challenge challenge =
        new Challenge(
            NEW_CHALLENGE, /* challenge name*/
            DUE_DATE, /* due date */
            null, /* badge */
            new ArrayList<String>(), /* users completed */
            id /* id of challenge */);
    Challenge returnedChallenge = Challenge.fromEntity(entity);

    assertEquals(returnedChallenge.getChallengeName(), challenge.getChallengeName());
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
