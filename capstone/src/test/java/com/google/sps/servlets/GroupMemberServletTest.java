package com.google.sps.servlets;

import static com.google.common.truth.Truth.assertThat;
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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.sps.Objects.Badge;
import com.google.sps.Objects.Challenge;
import com.google.sps.Objects.Group;
import com.google.sps.Objects.User;
import com.google.sps.Objects.Tag;

public class GroupMemberServletTest {

  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";
  private static final String GROUP_1_ID = "1";
  private static final String GROUP_NAME = "The 3 Musketeers";
  private static final String HEADER_IMAGE = "";
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
  private GroupMemberServlet groupMemberServlet;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    populateDatabase(datastore);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    groupMemberServlet = new GroupMemberServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
    responseWriter = null;
    datastore = null;
    groupMemberServlet = null;
  }

  /* Create a Group entity */
  private Entity createGroupEntity() {
    Entity groupEntity = new Entity("Group");
    groupEntity.setProperty("groupName", GROUP_NAME);
    groupEntity.setProperty("headerImg", HEADER_IMAGE);
    groupEntity.setProperty("memberIds", new ArrayList<String>(Arrays.asList(USER_ID)));
    groupEntity.setProperty("locationIds", new ArrayList<Long>());
    groupEntity.setProperty("posts", null);
    groupEntity.setProperty("options", new ArrayList<Long>());
    groupEntity.setProperty("challenges", new ArrayList<Challenge>());
    groupEntity.setProperty("midLatitude", 0.0);
    groupEntity.setProperty("midLongitude", 0.0);
    groupEntity.setProperty("tags", new ArrayList<Tag>());    
    return groupEntity;
  }

  private void populateDatabase(DatastoreService datastore) {
    // Add test data.
    CURRENT_USER.addGroup(Long.parseLong(GROUP_1_ID));
    datastore.put(CURRENT_USER.toEntity());
    datastore.put(OTHER_USER.toEntity());
    datastore.put(createGroupEntity());
  }

  @Test
  public void doGet_userNotLoggedIn() throws IOException, EntityNotFoundException {
    helper.setEnvIsLoggedIn(false);
    when(mockRequest.getParameter("id")).thenReturn(USER_ID);

    groupMemberServlet.doGet(mockRequest, mockResponse);
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(mockResponse).sendRedirect(captor.capture());

    assertEquals("/_ah/login?continue=%2F", captor.getValue());
  }

  @Test
  public void doGet_invalidMember() throws IOException, EntityNotFoundException {
    when(mockRequest.getParameter("id")).thenReturn("not an id");

    groupMemberServlet.doGet(mockRequest, mockResponse);

    String response = responseWriter.toString();
    assertTrue(response.contains("Oops an error happened!"));
  }

  @Test
  public void doGet_validMember() throws IOException, EntityNotFoundException {
    when(mockRequest.getParameter("id")).thenReturn(USER_ID);
    Key userKey = KeyFactory.createKey("User", USER_ID);

    groupMemberServlet.doGet(mockRequest, mockResponse);
    Entity user = datastore.get(userKey);

    String jsonDs = new Gson().toJson(User.fromEntity(user));
    String jsonCurrent = new Gson().toJson(CURRENT_USER);
    assertEquals(jsonDs, jsonCurrent);
  }

  @Test
  public void doPost_userNotLoggedIn() throws IOException, EntityNotFoundException {
    helper.setEnvIsLoggedIn(false);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_1_ID);
    when(mockRequest.getParameter("email")).thenReturn(USER_EMAIL);

    groupMemberServlet.doPost(mockRequest, mockResponse);
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(mockResponse).sendRedirect(captor.capture());

    assertEquals("/_ah/login?continue=%2F", captor.getValue());
  }

  @Test
  public void doPost_invalidGroup() throws IOException, EntityNotFoundException {
    when(mockRequest.getParameter("groupId")).thenReturn("1122");
    when(mockRequest.getParameter("email")).thenReturn(USER_EMAIL);

    groupMemberServlet.doPost(mockRequest, mockResponse);

    String response = responseWriter.toString();
    assertThat(response).contains("error");
  }

  @Test
  public void doPost_invalidMember() throws IOException, EntityNotFoundException {
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_1_ID);
    when(mockRequest.getParameter("email")).thenReturn("notamember@gmail.com");

    groupMemberServlet.doPost(mockRequest, mockResponse);

    String response = responseWriter.toString();
    assertThat(response).contains("error");
  }

  @Test
  public void doPost_memberNotInGroup_email() throws IOException, EntityNotFoundException {
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_1_ID);
    when(mockRequest.getParameter("email")).thenReturn(OTHER_EMAIL);
    Key groupKey = KeyFactory.createKey("Group", Long.parseLong(GROUP_1_ID));
    Key userKey = KeyFactory.createKey("User", OTHER_ID);

    groupMemberServlet.doPost(mockRequest, mockResponse);
    Entity group = datastore.get(groupKey);
    Entity user = datastore.get(userKey);

    String jsonDs = new Gson().toJson(Group.fromEntity(group));
    assertTrue(jsonDs.contains(OTHER_ID));
    assertThat(User.fromEntity(user).getGroups().contains(GROUP_1_ID));
  }

  @Test
  public void doPost_memberNotInGroup_userId() throws IOException, EntityNotFoundException {
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_1_ID);
    when(mockRequest.getParameter("userId")).thenReturn(USER_ID);
    Key groupKey = KeyFactory.createKey("Group", Long.parseLong(GROUP_1_ID));
    Key userKey = KeyFactory.createKey("User", USER_ID);

    groupMemberServlet.doPost(mockRequest, mockResponse);
    Entity group = datastore.get(groupKey);
    Entity user = datastore.get(userKey);

    String jsonDs = new Gson().toJson(Group.fromEntity(group));
    assertTrue(jsonDs.contains(USER_ID));
    assertThat(User.fromEntity(user).getGroups().contains(GROUP_1_ID));
  }
}
