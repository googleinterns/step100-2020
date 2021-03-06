package com.google.sps.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.sps.Objects.Badge;
import com.google.sps.Objects.Challenge;
import com.google.sps.Objects.Tag;
import com.google.sps.Objects.User;

public class AllGroupMembersServletTest {

  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";
  private static final String OTHER_ID = "other";
  private static final String OTHER_EMAIL = "other@test.com";

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

  private static final String GROUP_1_ID = "1";
  private static final String GROUP_NAME = "The 3 Musketeers";
  private static final String HEADER_IMAGE = "";

  private static final User CURRENT_USER =
      new User(
          USER_ID,
          "Test",
          "McTest",
          USER_EMAIL,
          /* phoneNumber= */ "123-456-7890",
          /* profilePic= */ "",
          /* address= */ "",
          /* latitude= */ 0,
          /* longitude= */ 0,
          /* badges= */ new LinkedHashSet<Badge>(),
          /* groups= */ new LinkedHashSet<Long>(),
          /* interests= */ new ArrayList<String>());

  private static final User OTHER_USER =
      new User(
          OTHER_ID,
          "Test Two",
          "McTest",
          OTHER_EMAIL,
          /* phoneNumber= */ "123-456-0000",
          /* profilePic= */ "",
          /* address= */ "",
          /* latitude= */ 0,
          /* longitude= */ 0,
          /* badges= */ new LinkedHashSet<Badge>(),
          /* groups= */ new LinkedHashSet<Long>(),
          /* interests= */ new ArrayList<String>());

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
    populateDatabase(datastore);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    allGroupMembersServlet = new AllGroupMembersServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  private void populateDatabase(DatastoreService datastore) {
    // Add test data.
    Entity group1 = createGroupEntity();
    datastore.put(group1);
    datastore.put(CURRENT_USER.toEntity());
    datastore.put(OTHER_USER.toEntity());
  }

  /* Create a Group entity */
  private Entity createGroupEntity() {
    Entity groupEntity = new Entity("Group");
    groupEntity.setProperty("groupName", GROUP_NAME);
    groupEntity.setProperty("headerImg", HEADER_IMAGE);
    groupEntity.setProperty("memberIds", new ArrayList<String>(Arrays.asList(USER_ID, OTHER_ID)));
    groupEntity.setProperty("locationIds", new ArrayList<Long>());
    groupEntity.setProperty("posts", null);
    groupEntity.setProperty("options", new ArrayList<Long>());
    groupEntity.setProperty("tags", new ArrayList<Tag>());    
    groupEntity.setProperty("challenges", new ArrayList<Challenge>());
    groupEntity.setProperty("midLatitude", 0.0);
    groupEntity.setProperty("midLongitude", 0.0);
    return groupEntity;
  }

  @Test
  public void doGet_getAllGroupMembers() throws IOException, EntityNotFoundException {
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_1_ID);

    allGroupMembersServlet.doGet(mockRequest, mockResponse);

    String response = responseWriter.toString();
    assertTrue(response.contains(OTHER_ID));
    assertTrue(!response.contains(USER_ID));
  }

  @Test
  public void doGet_userNotLoggedIn() throws Exception {
    helper.setEnvIsLoggedIn(false);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_1_ID);

    allGroupMembersServlet.doGet(mockRequest, mockResponse);
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(mockResponse).sendRedirect(captor.capture());

    assertEquals("/_ah/login?continue=%2F", captor.getValue());
  }
}
