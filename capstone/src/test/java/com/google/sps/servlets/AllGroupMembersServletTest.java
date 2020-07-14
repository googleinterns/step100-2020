package com.google.sps.servlets;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedHashSet;

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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.sps.Objects.User;
import com.google.sps.Objects.Badge;

public class AllGroupMembersServletTest {

  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";


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

  private final User USER_1 =  new User("22222", "Test", "McTest", 
                          "testy@gmail.com", 
                          /* phoneNumber= */ "123-456-7890", 
                          /* profilePic= */ "", 
                          /* badges= */ new LinkedHashSet<Badge>(), 
                          /* groups= */ new LinkedHashSet<Long>(), 
                          /* interests= */ new ArrayList<String>()
  );

  private final User USER_2 =  new User("333333", "Test", "McTest2", 
                          "testy2@gmail.com", 
                          /* phoneNumber= */ "111-111-1111", 
                          /* profilePic= */ "", 
                          /* badges= */ new LinkedHashSet<Badge>(), 
                          /* groups= */ new LinkedHashSet<Long>(), 
                          /* interests= */ new ArrayList<String>()
  );

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private DatastoreService datastore;
  private AllGroupMembersServlet allGroupMembersServlet;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    // Add test data
    ImmutableList.Builder<Entity> users = ImmutableList.builder();
    users.add(USER_1.toEntity());
    users.add(USER_2.toEntity());
    datastore.put(users.build());

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    allGroupMembersServlet = new AllGroupMembersServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
    responseWriter = null;
    datastore = null;
    allGroupMembersServlet = null;
  }

  @Test
  public void doGet_getAllGroupMembers() throws IOException, EntityNotFoundException {
    allGroupMembersServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains(USER_1.getFirstName()));
    assertTrue(response.contains(USER_2.getFirstName()));
  }
}