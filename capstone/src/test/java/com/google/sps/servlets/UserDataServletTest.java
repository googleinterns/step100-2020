package com.google.sps.servlets;

import static com.google.common.truth.Truth.assertThat;
import static com.google.sps.utils.TestUtils.assertEqualsJson;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.sps.Objects.Badge;
import com.google.sps.Objects.User;

/** Unit tests for {@link UserDataServlet}. */
@RunWith(JUnit4.class)
public class UserDataServletTest {
  private static final String USER_EMAIL = "test@mctest.com";
  private static final String USER_ID = "testy-mc-test";

  // Set no eventual consistency, that way queries return all results.
  // https://cloud.google.com/appengine/docs/java/tools/localunittesting
  // #Java_Writing_High_Replication_Datastore_tests
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

  private static final ArrayList<String> INTERESTS_LIST =
      new ArrayList<String>(Arrays.asList("Testing", "Dancing"));
  private static final User USER_1 =
      new User(
          USER_ID,
          "Test",
          "McTest",
          "TEST MCTEST",
          USER_EMAIL,
          /* phoneNumber= */ "123-456-7890",
          /* profilePic= */ "",
          /* badges= */ new LinkedHashSet<Badge>(),
          /* groups= */ new LinkedHashSet<Long>(),
          /* interests= */ INTERESTS_LIST);

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private DatastoreService datastore;
  private UserDataServlet userDataServlet;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    populateDatabase(datastore);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    userDataServlet = new UserDataServlet();
  }

  @After
  public void tearDown() {
    helper.setEnvIsLoggedIn(true);
    helper.tearDown();
  }

  @Test
  public void doGet_retrieveUserData() throws Exception {
    userDataServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();
    String expectedResponse = new Gson().toJson(USER_1);

    assertTrue(assertEqualsJson(response, expectedResponse));
  }

  @Test
  public void doGet_userNotLoggedIn() throws Exception {
    helper.setEnvIsLoggedIn(false);

    userDataServlet.doGet(mockRequest, mockResponse);
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(mockResponse).sendRedirect(captor.capture());

    assertEquals("/_ah/login?continue=%2F", captor.getValue());
  }

  @Test
  public void doGet_userNotFound() throws Exception {
    removeUserFromDatastore(datastore, USER_1);

    userDataServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertThat(response).contains("error");
  }

  private void populateDatabase(DatastoreService datastore) {
    // Add test data.
    datastore.put(USER_1.toEntity());
  }

  private void removeUserFromDatastore(DatastoreService datastore, User user) {
    Key entityKey = KeyFactory.createKey("User", user.getUserId());
    datastore.delete(entityKey);
  }
}
