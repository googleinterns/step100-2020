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
import java.util.Arrays;
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
import com.google.sps.Objects.Challenge;

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

  private static final long GROUP_1_ID = 1234;
  private static final String GROUP_NAME = "The 3 Musketeers";
  private static final String HEADER_IMAGE = "";
  private static final String USER_1_ID = "22222";
  private static final String USER_2_ID = "333333";

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
    Entity group1 = createGroupEntity(GROUP_1_ID);
    datastore.put(group1);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    allGroupMembersServlet = new AllGroupMembersServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  /* Create a Group entity */
  private Entity createGroupEntity(long groupId) {
    Entity groupEntity = new Entity("Group", groupId);
    groupEntity.setProperty("groupId", groupId);
    groupEntity.setProperty("groupName", GROUP_NAME);
    groupEntity.setProperty("headerImg", HEADER_IMAGE);
    groupEntity.setProperty("memberIds", new ArrayList<String>(Arrays.asList(USER_1_ID, USER_2_ID)));
    groupEntity.setProperty("challenges", new ArrayList<Challenge>());
    return groupEntity;
  }

  //@Test
  public void doGet_getAllGroupMembers() throws IOException, EntityNotFoundException {
    when(mockRequest.getParameter("groupId")).thenReturn(Long.toString(GROUP_1_ID));
    allGroupMembersServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();
    System.out.println(response);

    assertTrue(response.contains(USER_1.getUserId()));
    assertTrue(response.contains(USER_2.getUserId()));
  }
}