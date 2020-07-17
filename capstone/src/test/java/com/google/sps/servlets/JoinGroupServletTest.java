package com.google.sps.servlets;

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
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;

public class JoinGroupServletTest {

  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";
  private static final String GROUP_ID = "1";
  private static final String GROUP_NAME = "The Bachelors";

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

  @Test
  public void doPost_userNotLoggedIn() throws IOException, EntityNotFoundException {
    helper.setEnvIsLoggedIn(false);

    joinGroupServlet.doPost(mockRequest, mockResponse);

    String response = responseWriter.toString();
    assertTrue(response.contains("Oops an error happened!"));
  }
}
