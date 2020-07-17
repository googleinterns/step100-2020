package com.google.sps.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

public class JoinGroupServletTest {

  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";
  private static final String GROUP_ID = "1";
  private static final String GROUP_NAME = "The Bachelors";
  private static final String FIRST_NAME = "Lucy";
  private static final String LAST_NAME = "Qu";

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
  private JoinGroupServlet joinGroupServlet;
  private StringWriter responseWriter;
  private DatastoreService datastore;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    joinGroupServlet = new JoinGroupServlet();

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
  }

  @After
  public void tearDown() {
    helper.tearDown();
    responseWriter = null;
    datastore = null;
    joinGroupServlet = null;
  }

  private Entity createGroup(String userId, String groupName, List<String> members) {
    Entity groupEntity = new Entity("Group");
    groupEntity.setProperty("memberIds", members);
    groupEntity.setProperty("challenges", null);
    groupEntity.setProperty("posts", null);
    groupEntity.setProperty("options", new ArrayList<Long>());
    groupEntity.setProperty("groupName", groupName);
    groupEntity.setProperty("headerImg", "");
    return groupEntity;
  }

  private Entity createUser(String userId, String firstName, String lastName) {
    Entity userEntity = new Entity("User", userId);
    userEntity.setProperty("userId", userId);
    userEntity.setProperty("firstName", firstName);
    userEntity.setProperty("lastName", lastName);
    userEntity.setProperty("email", "");
    userEntity.setProperty("phoneNumber", "");
    userEntity.setProperty("profilePic", "");
    userEntity.setProperty("badges", null);
    userEntity.setProperty("groups", null);
    userEntity.setProperty("interests", null);
    return userEntity;
  }

  @Test
  public void doGet_userIsMember() throws IOException {
    List<String> members = new ArrayList<String>();
    members.add(USER_ID);
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME, members);
    datastore.put(groupEntity);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);

    joinGroupServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains("true"));
  }

  @Test
  public void doGet_userIsNotMember() throws IOException {
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME, new ArrayList<String>());
    datastore.put(groupEntity);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);

    joinGroupServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains("false"));
  }

  @Test
  public void doGet_userNotLoggedIn() throws IOException {
    helper.setEnvIsLoggedIn(false);

    joinGroupServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains("Oops an error happened!"));
  }

  /**
   * Tests that the user's list of group ids updates accordingly with the new group.
   *
   * @throws IOException exception thrown if cannot read or write to file
   * @throws EntityNotFoundException exception thrown if entity not in database
   */
  @Test
  public void doPost_userGroupsListTest() throws IOException, EntityNotFoundException {
    List<String> members = new ArrayList<String>();
    members.add(USER_ID);
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME, members);
    datastore.put(groupEntity);
    Entity userEntity = this.createUser(USER_ID, FIRST_NAME, LAST_NAME);
    datastore.put(userEntity);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);

    joinGroupServlet.doPost(mockRequest, mockResponse);
    List<Long> groupIds = new ArrayList<Long>();
    Key userKey = KeyFactory.createKey("User", USER_ID);
    Entity userFromDatastore = datastore.get(userKey);
    groupIds = (ArrayList<Long>) userFromDatastore.getProperty("groups");

    assertEquals(1, groupIds.size());
    // cannot user assertEquals to compare long and Long
    assert groupIds.get(0) == Long.parseLong(GROUP_ID);
  }

  /**
   * Tests that the group entity's list of members gets updated accordingly to include new member.
   *
   * @throws IOException exception thrown if cannot read or write to file
   * @throws EntityNotFoundException exception thrown if entity is not in database
   */
  @Test
  public void doPost_groupMembersListTest() throws IOException, EntityNotFoundException {
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME, new ArrayList<String>());
    datastore.put(groupEntity);
    Entity userEntity = this.createUser(USER_ID, FIRST_NAME, LAST_NAME);
    datastore.put(userEntity);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);

    joinGroupServlet.doPost(mockRequest, mockResponse);
    List<String> memberIds = new ArrayList<String>();
    Key userKey = KeyFactory.createKey("Group", Long.parseLong(GROUP_ID));
    Entity userFromDatastore = datastore.get(userKey);
    memberIds = (ArrayList<String>) userFromDatastore.getProperty("memberIds");

    assertEquals(memberIds.size(), 1);
    assertEquals(USER_ID, memberIds.get(0));
  }

  @Test
  public void doPost_userNotLoggedIn() throws IOException, EntityNotFoundException {
    helper.setEnvIsLoggedIn(false);

    joinGroupServlet.doPost(mockRequest, mockResponse);

    String response = responseWriter.toString();
    assertTrue(response.contains("Oops an error happened!"));
  }
}
