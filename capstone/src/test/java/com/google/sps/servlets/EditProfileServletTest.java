package com.google.sps.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.sps.Objects.Badge;
import com.google.sps.Objects.User;

/** Unit tests for {@link EditProfileServlet}. */
@RunWith(JUnit4.class)
public class EditProfileServletTest {
  private static final String CURRENT_USER_EMAIL = "test@mctest.com";
  private static final String CURRENT_USER_ID = "testy-mc-test";
  private static final String EDIT_EMAIL = "test2@mctest.com";
  private static final String EDIT_FIRST = "Mister";
  private static final String EDIT_LAST = "McTest";
  private static final String FULL_NAME = "MISTER MCTEST";
  private static final String EDIT_PHONE = "808-808-8080";
  private static final String EDIT_ADDRESS = "1234 Rainbow Avenue, Candy, CA, 4321";
  private static final ArrayList<String> INTERESTS_LIST = new ArrayList<String>( 
      Arrays.asList("Testing", "Dancing"));

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
              new LocalDatastoreServiceTestConfig()
                  .setDefaultHighRepJobPolicyUnappliedJobPercentage(0),
              new LocalUserServiceTestConfig())
          .setEnvEmail(CURRENT_USER_EMAIL)
          .setEnvIsLoggedIn(true)
          .setEnvAuthDomain("gmail.com")
          .setEnvAttributes(
              new HashMap(
                  ImmutableMap.of(
                      "com.google.appengine.api.users.UserService.user_id_key", CURRENT_USER_ID)));

  private static final User CURRENT_USER = new User(CURRENT_USER_ID, "Test", "McTest", 
                          CURRENT_USER_EMAIL, 
                          /* phoneNumber= */ "123-456-7890", 
                          /* profilePic= */ "",
                          /* address= */ "", 
                          /* latitude= */ 0,
                          /* longitude= */ 0,
                          /* badges= */ new LinkedHashSet<Badge>(), 
                          /* groups= */ new LinkedHashSet<Long>(), 
                          /* interests= */ INTERESTS_LIST);
  
  private static final User EDIT_USER = new User(CURRENT_USER_ID, EDIT_FIRST, EDIT_LAST, 
                          EDIT_EMAIL, 
                          /* phoneNumber= */ EDIT_PHONE, 
                          /* profilePic= */ "", 
                          /* address= */ EDIT_ADDRESS,
                          /* latitude= */ 0,
                          /* longitude= */ 0,
                          /* badges= */ new LinkedHashSet<Badge>(), 
                          /* groups= */ new LinkedHashSet<Long>(), 
                          /* interests= */ INTERESTS_LIST);                        


  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private DatastoreService datastore;
  private EditProfileServlet editProfileServlet;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    addUserToDatastore(datastore, CURRENT_USER);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    editProfileServlet = new EditProfileServlet();
  }

  @After
  public void tearDown() {
    helper.setEnvIsLoggedIn(true);
    helper.tearDown();
  }

  @Test
  public void doPost_editProfile() throws Exception {
    when(mockRequest.getParameter("first")).thenReturn(EDIT_FIRST);
    when(mockRequest.getParameter("last")).thenReturn(EDIT_LAST);
    when(mockRequest.getParameter("email")).thenReturn(EDIT_EMAIL);
    when(mockRequest.getParameter("phone")).thenReturn(EDIT_PHONE);
    when(mockRequest.getParameter("address")).thenReturn(EDIT_ADDRESS);
    when(mockRequest.getParameter("interests")).thenReturn("Testing, Dancing");

    editProfileServlet.doPost(mockRequest, mockResponse);
    Key userKey = KeyFactory.createKey("User", CURRENT_USER_ID);
    Entity user = datastore.get(userKey);

    String jsonOriginal = new Gson().toJson(CURRENT_USER);
    String jsonStored = new Gson().toJson(User.fromEntity(user));
    String jsonExpected = new Gson().toJson(EDIT_USER);

    assertEquals(jsonStored, jsonExpected);
    assertFalse(jsonStored.equals(jsonOriginal));
  }

  private void addUserToDatastore(DatastoreService datastore, User user) {
    datastore.put(user.toEntity());
  }
}
