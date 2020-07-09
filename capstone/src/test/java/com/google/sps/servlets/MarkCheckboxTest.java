package com.google.sps.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

public class MarkCheckboxTest {
  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";
  private static final String CHECKBOX_ID = "1";
  private static final String CHECKED = "true";
  private static final String TYPE = "Challenge";
  private static final String NEW_CHALLENGE = "Run";

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
  private StringWriter responseWriter;
  private DatastoreService datastore;
  private MarkCheckboxServlet markCheckboxServlet;
  private ChallengeServlet challengeServlet;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    markCheckboxServlet = new MarkCheckboxServlet();
    challengeServlet = new ChallengeServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
    responseWriter = null;
    datastore = null;
    markCheckboxServlet = null;
    challengeServlet = null;
  }

  @Test
  public void doPost_userLoggedIn_validChallenge() throws IOException, EntityNotFoundException {
    when(mockRequest.getParameter("name")).thenReturn(NEW_CHALLENGE);

    challengeServlet.doPost(mockRequest, mockResponse);

    when(mockRequest.getParameter("id")).thenReturn(CHECKBOX_ID);
    when(mockRequest.getParameter("checked")).thenReturn(CHECKED);
    when(mockRequest.getParameter("type")).thenReturn(TYPE);

    markCheckboxServlet.doPost(mockRequest, mockResponse);

    Key key = KeyFactory.createKey(TYPE, Long.parseLong(CHECKBOX_ID));
    Entity entity = datastore.get(key);
    Challenge challenge = Challenge.fromEntity(entity);

    assert challenge.getUsersCompleted().size() == 1;
    assertEquals(challenge.getUsersCompleted().get(0), USER_ID);
  }

  @Test(expected = EntityNotFoundException.class)
  public void doPost_userNotLoggedIn() throws IOException, EntityNotFoundException {
    helper.setEnvIsLoggedIn(false);
    when(mockRequest.getParameter("name")).thenReturn(NEW_CHALLENGE);

    challengeServlet.doPost(mockRequest, mockResponse);

    when(mockRequest.getParameter("id")).thenReturn(CHECKBOX_ID);
    when(mockRequest.getParameter("checked")).thenReturn(CHECKED);
    when(mockRequest.getParameter("type")).thenReturn(TYPE);

    markCheckboxServlet.doPost(mockRequest, mockResponse);

    Key key = KeyFactory.createKey(TYPE, Long.parseLong(CHECKBOX_ID));
    // trigger EntityNotfoundException
    Entity entity = datastore.get(key);
  }

  @Test
  public void doPost_noChallenges() throws IOException, EntityNotFoundException {
    when(mockRequest.getParameter("id")).thenReturn(CHECKBOX_ID);
    when(mockRequest.getParameter("checked")).thenReturn(CHECKED);
    when(mockRequest.getParameter("type")).thenReturn(TYPE);

    markCheckboxServlet.doPost(mockRequest, mockResponse);

    Key key = KeyFactory.createKey(TYPE, Long.parseLong(CHECKBOX_ID));
    Entity entity = datastore.get(key);
    assertNull(entity.getProperty("name"));
    assertNull(entity.getProperty("dueDate"));
  }
}
