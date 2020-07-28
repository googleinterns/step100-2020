package com.google.sps.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
import com.google.sps.Objects.Challenge;
import com.google.sps.Objects.User;
import com.google.sps.Objects.response.UserGroupResponse;
import com.google.sps.utils.TestUtils;

/** Unit tests for {@link UserGroupsServlet}. */
@RunWith(JUnit4.class)
public class UserGroupsServletTest {
  private static final String USER_EMAIL = "test@mctest.com";
  private static final String USER_ID = "testy-mc-test";

  private static final long GROUP_1_ID = 1234;
  private static final long GROUP_2_ID = 5678;
  private static final String GROUP_NAME = "The 3 Musketeers";
  private static final String HEADER_IMAGE = "";

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

  private static final ArrayList<String> INTERESTS_LIST =
      new ArrayList<String>(Arrays.asList("Testing", "Dancing"));
  private static final LinkedHashSet<Long> GROUPS_LIST =
      new LinkedHashSet<Long>(Arrays.asList(GROUP_1_ID, GROUP_2_ID));
  private static final User USER_1 =
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
          /* groups= */ GROUPS_LIST,
          /* interests= */ INTERESTS_LIST);

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private DatastoreService datastore;
  private UserGroupsServlet userGroupsServlet;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    populateDatabase(datastore);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    userGroupsServlet = new UserGroupsServlet();
  }

  @After
  public void tearDown() {
    helper.setEnvIsLoggedIn(true);
    helper.tearDown();
  }

  @Test
  public void doGet_retrieveGroups() throws Exception {
    userGroupsServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    ArrayList<UserGroupResponse> expectedGroups = createExpectedGroups();
    String expectedResponse = new Gson().toJson(expectedGroups);

    assertTrue(TestUtils.assertEqualsJson(response, expectedResponse));
  }

  @Test
  public void doGet_userNotLoggedIn() throws Exception {
    helper.setEnvIsLoggedIn(false);

    userGroupsServlet.doGet(mockRequest, mockResponse);
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(mockResponse).sendRedirect(captor.capture());

    assertEquals("/_ah/login?continue=%2F", captor.getValue());
  }

  private void populateDatabase(DatastoreService datastore) {
    // Add test data.
    datastore.put(USER_1.toEntity());
    Entity group1 = createGroupEntity(GROUP_1_ID);
    Entity group2 = createGroupEntity(GROUP_2_ID);
    datastore.put(group1);
    datastore.put(group2);
  }

  private void removeUserFromDatastore(DatastoreService datastore, User user) {
    Key entityKey = KeyFactory.createKey("User", user.getUserId());
    datastore.delete(entityKey);
  }

  /* Create a Group entity that has all information needed for a UserGroupResponse */
  private Entity createGroupEntity(long groupId) {
    Entity groupEntity = new Entity("Group", groupId);
    groupEntity.setProperty("groupId", groupId);
    groupEntity.setProperty("groupName", GROUP_NAME);
    groupEntity.setProperty("headerImg", HEADER_IMAGE);
    groupEntity.setProperty("challenges", new ArrayList<Challenge>());
    return groupEntity;
  }

  /* Create a list of the expected UserGroupResponses */
  private ArrayList<UserGroupResponse> createExpectedGroups() {
    ArrayList<UserGroupResponse> groups = new ArrayList<>();
    UserGroupResponse response1 =
        new UserGroupResponse(new ArrayList<Challenge>(), GROUP_NAME, HEADER_IMAGE, GROUP_1_ID);
    UserGroupResponse response2 =
        new UserGroupResponse(new ArrayList<Challenge>(), GROUP_NAME, HEADER_IMAGE, GROUP_2_ID);
    groups.add(response1);
    groups.add(response2);
    return groups;
  }
}
