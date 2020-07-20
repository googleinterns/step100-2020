package com.google.sps.servlets;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;
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
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.sps.Objects.Badge;
import com.google.sps.Objects.Group;
import com.google.sps.Objects.User;

/** Unit tests for {@link CreateGroupServlet}. */
@RunWith(JUnit4.class)
public class CreateGroupServletTest {
  private static final String USER_EMAIL = "test@mctest.com";
  private static final String USER_ID = "testy-mc-test";
  private static final String GROUP_NAME = "Pals";
  private static final long GROUP_ID = 1;
  private static final long GROUP_ID_2 = 2;

  private static final User USER_1 =
      new User(
          USER_ID,
          "Test",
          "McTest",
          USER_EMAIL,
          /* phoneNumber= */ "123-456-7890",
          /* profilePic= */ "",
          /* badges= */ new LinkedHashSet<Badge>(),
          /* groups= */ new LinkedHashSet<Long>(),
          /* interests= */ new ArrayList<String>());

  private static final Group GROUP_1 =
      new Group(
          /* memberIds= */ new ArrayList<String>(Arrays.asList(USER_ID)),
          /* challenges= */ new ArrayList<Long>(),
          /* posts= */ new ArrayList<Long>(),
          /* options= */ new ArrayList<Long>(),
          /* groupName= */ GROUP_NAME,
          /* headerImg= */ "",
          /* groupId= */ GROUP_ID);

  private static final Group GROUP_2 =
      new Group(
          /* memberIds= */ new ArrayList<String>(Arrays.asList(USER_ID)),
          /* challenges= */ new ArrayList<Long>(),
          /* posts= */ new ArrayList<Long>(),
          /* options= */ new ArrayList<Long>(),
          /* groupName= */ GROUP_NAME,
          /* headerImg= */ "",
          /* groupId= */ GROUP_ID_2);

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
  private CreateGroupServlet createGroupServlet;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    addUserToDatastore(datastore, USER_1);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    createGroupServlet = new CreateGroupServlet();
  }

  @After
  public void tearDown() {
    helper.setEnvIsLoggedIn(true);
    helper.tearDown();
  }

  @Test
  public void doPost_addGroup() throws Exception {
    when(mockRequest.getParameter("groupName")).thenReturn(GROUP_NAME);
    Key groupKey = KeyFactory.createKey("Group", GROUP_ID);
    Key userKey = KeyFactory.createKey("User", USER_ID);

    createGroupServlet.doPost(mockRequest, mockResponse);
    Entity group = datastore.get(groupKey);
    Entity user = datastore.get(userKey);

    String jsonDs = new Gson().toJson(Group.fromEntity(group));
    String jsonCurrent = new Gson().toJson(GROUP_1);
    assertEquals(jsonDs, jsonCurrent);
    assertThat(User.fromEntity(user).getGroups().contains(GROUP_ID));
  }

  @Test
  public void doPost_addMultipleGroups() throws Exception {
    when(mockRequest.getParameter("groupName")).thenReturn(GROUP_NAME);
    Key groupKey = KeyFactory.createKey("Group", GROUP_ID);
    Key groupKey2 = KeyFactory.createKey("Group", GROUP_ID_2);
    Key userKey = KeyFactory.createKey("User", USER_ID);

    createGroupServlet.doPost(mockRequest, mockResponse);
    createGroupServlet.doPost(mockRequest, mockResponse);
    Entity group1 = datastore.get(groupKey);
    Entity group2 = datastore.get(groupKey2);
    Entity user = datastore.get(userKey);

    String jsonDs1 = new Gson().toJson(Group.fromEntity(group1));
    String jsonCurrent1 = new Gson().toJson(GROUP_1);
    String jsonDs2 = new Gson().toJson(Group.fromEntity(group2));
    String jsonCurrent2 = new Gson().toJson(GROUP_2);
    assertEquals(jsonDs1, jsonCurrent1);
    assertEquals(jsonDs2, jsonCurrent2);
    assertThat(
        User.fromEntity(user).getGroups().contains(GROUP_ID)
            && User.fromEntity(user).getGroups().contains(GROUP_ID_2));
  }

  @Test
  public void doPost_userNotLoggedIn() throws Exception {
    helper.setEnvIsLoggedIn(false);
    when(mockRequest.getParameter("groupName")).thenReturn(GROUP_NAME);

    createGroupServlet.doPost(mockRequest, mockResponse);
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(mockResponse).sendRedirect(captor.capture());

    assertEquals("/_ah/login?continue=%2F", captor.getValue());
  }

  private void addUserToDatastore(DatastoreService datastore, User user) {
    datastore.put(user.toEntity());
  }
}
