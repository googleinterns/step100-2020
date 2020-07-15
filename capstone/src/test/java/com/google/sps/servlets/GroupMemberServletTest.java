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
import java.util.ArrayList;
import java.util.HashSet;
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
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.sps.Objects.User;
import com.google.sps.Objects.Challenge;

public class GroupMemberServletTest {

  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";
  private static final long GROUP_1_ID = 1234;
  private static final String GROUP_NAME = "The 3 Musketeers";
  private static final String HEADER_IMAGE = "";
 
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
  private GroupMemberServlet groupMemberServlet;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

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
  private Entity createGroupEntity(long groupId) {
    Entity groupEntity = new Entity("Group", groupId);
    groupEntity.setProperty("groupId", groupId);
    groupEntity.setProperty("groupName", GROUP_NAME);
    groupEntity.setProperty("headerImg", HEADER_IMAGE);
    groupEntity.setProperty("members", new ArrayList<String>());
    groupEntity.setProperty("challenges", new ArrayList<Challenge>());
    return groupEntity;
  }

  private void populateDatabase(DatastoreService datastore) {
    // Add test data.
    Entity group1 = createGroupEntity(GROUP_1_ID);
    datastore.put(group1);
  }

  //@Test
  public void doPost_userNotLoggedIn() throws IOException, EntityNotFoundException {
    helper.setEnvIsLoggedIn(false);
    when(mockRequest.getParameter("groupId")).thenReturn(Long.toString(GROUP_1_ID));

    groupMemberServlet.doPost(mockRequest, mockResponse);
    String response = responseWriter.toString();
    assertTrue(response.contains("Oops an error happened!"));
  }

  //@Test
  public void doPost_invalidPost() throws IOException, EntityNotFoundException {
    populateDatabase(datastore);
    when(mockRequest.getParameter("groupId")).thenReturn("1122");

    groupMemberServlet.doPost(mockRequest, mockResponse);
    String response = responseWriter.toString();
    assertThat(response).contains("error");
  }

  //TODO: implement group member tests 
  @Test
  public void doPost_addGroupMember() throws IOException, EntityNotFoundException {}

  @Test
  public void doGet_invalidMember() throws IOException, EntityNotFoundException {}

  @Test
  public void doGet_validMember() throws IOException, EntityNotFoundException {}
}

