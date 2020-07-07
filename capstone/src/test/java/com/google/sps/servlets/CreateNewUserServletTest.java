package com.google.sps.servlets;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import com.google.sps.Objects.User;
import com.google.sps.Objects.Badge;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Unit tests for {@link CreateNewUserServlet}.
 */
 @RunWith(JUnit4.class)
public class CreateNewUserServletTest {
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

  private static ArrayList<String> INTERESTS_LIST = new ArrayList<String>( 
      Arrays.asList("Testing", "Dancing"));
  private static final User USER_1 = new User(USER_ID, "Test", "McTest", USER_EMAIL, 
                          /* phoneNumber= */ "123-456-7890", 
                          /* profilePic= */ "", 
                          /* badges= */ new LinkedHashSet<Badge>(), 
                          /* groups= */ new LinkedHashSet<Long>(), 
                          /* interests= */ INTERESTS_LIST);

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private DatastoreService datastore;
  private CreateNewUserServlet createNewUserServlet;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    createNewUserServlet = new CreateNewUserServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doPost_addNewUser() throws Exception {
    when(mockRequest.getParameter("first")).thenReturn(USER_1.getFirstName());
    when(mockRequest.getParameter("last")).thenReturn(USER_1.getLastName());
    when(mockRequest.getParameter("phone")).thenReturn(USER_1.getPhoneNumber());
    when(mockRequest.getParameter("interests")).thenReturn("Testing, Dancing");

    createNewUserServlet.doPost(mockRequest, mockResponse);

    Key userKey = KeyFactory.createKey("User", USER_ID);
    Entity user = datastore.get(userKey);
    assertThat(user.getProperty("userId")).isEqualTo(USER_ID);
    assertThat(user.getProperty("firstName")).isEqualTo(USER_1.getFirstName());
    assertThat(user.getProperty("lastName")).isEqualTo(USER_1.getLastName());
    assertThat(user.getProperty("phoneNumber")).isEqualTo(USER_1.getPhoneNumber());
    assertThat(user.getProperty("email")).isEqualTo(USER_EMAIL);
    assertThat(user.getProperty("interests")).isEqualTo(INTERESTS_LIST);
    assertThat(user.getProperty("badges")).isEqualTo(null);
    assertThat(user.getProperty("groups")).isEqualTo(null);

    //  TODO: can override User object's equal method to check if two 
    // User objects are equal and use that to assert whether a new user was 
    // correctly added/retrieved.
    // call User.fromEntity(user) and check if they're equal
  }

  @Test(expected = EntityNotFoundException.class)
  public void doPost_userNotLoggedIn() throws Exception {
    helper.setEnvIsLoggedIn(false);

    when(mockRequest.getParameter("first")).thenReturn(USER_1.getFirstName());
    when(mockRequest.getParameter("last")).thenReturn(USER_1.getLastName());
    when(mockRequest.getParameter("phone")).thenReturn(USER_1.getPhoneNumber());
    when(mockRequest.getParameter("interests")).thenReturn("Testing, Dancing");

    createNewUserServlet.doPost(mockRequest, mockResponse);

    Key userKey = KeyFactory.createKey("User", USER_ID);
    datastore.get(userKey); 

    helper.setEnvIsLoggedIn(true);
  }
}