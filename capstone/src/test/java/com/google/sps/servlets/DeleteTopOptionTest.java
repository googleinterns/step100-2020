package com.google.sps.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.google.appengine.repackaged.com.google.common.collect.ImmutableMap;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

/**
 * Unit tests for {@link DeletePollOptionServlet}.
 *
 * @author lucyqu
 */
public class DeleteTopOptionTest {
  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";
  private static final String GROUP_ID = "1";
  private static final String GROUP_NAME = "Bakers";
  private static final String NEW_OPTION = "Make red velvet cake";
  private static final String NEW_OPTION_2 = "Make brownies";
  private static final List<Long> OPTION_ID = new ArrayList<Long>(Arrays.asList(2L));
  private static final List<Long> OPTION_IDS = new ArrayList<Long>(Arrays.asList(2L, 3L));

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
  private DeletePollOptionServlet deletePollOptionServlet;
  private StringWriter responseWriter;
  private DatastoreService datastore;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    deletePollOptionServlet = new DeletePollOptionServlet();

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
  }

  @After
  public void tearDown() {
    helper.tearDown();
    responseWriter = null;
    datastore = null;
    deletePollOptionServlet = null;
  }

  private Entity createGroup(String userId, String groupName) {
    ArrayList<String> members = new ArrayList<String>();
    members.add(userId);
    Entity groupEntity = new Entity("Group");
    groupEntity.setProperty("memberIds", members);
    groupEntity.setProperty("challenges", null);
    groupEntity.setProperty("posts", null);
    groupEntity.setProperty("options", new ArrayList<Long>());
    groupEntity.setProperty("groupName", groupName);
    groupEntity.setProperty("headerImg", "");
    return groupEntity;
  }

  private Entity createOption(String text) {
    Entity optionEntity = new Entity("Option");
    optionEntity.setProperty("text", text);
    optionEntity.setProperty("votes", new ArrayList<Long>());
    optionEntity.setProperty("timestamp", System.currentTimeMillis());
    return optionEntity;
  }

  @Test
  public void doPost_noOptions() throws IOException, EntityNotFoundException {
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME);
    datastore.put(groupEntity);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);

    deletePollOptionServlet.doPost(mockRequest, mockResponse);
    Key groupKey = KeyFactory.createKey("Group", Integer.parseInt(GROUP_ID));
    Entity returnedEntity = datastore.get(groupKey);

    assertNull(returnedEntity.getProperty("options"));
  }

  @Test
  public void doPost_withOneOption() throws IOException, EntityNotFoundException {
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME);
    groupEntity.setProperty("options", OPTION_ID);
    datastore.put(groupEntity);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);
    datastore.put(this.createOption(NEW_OPTION));

    deletePollOptionServlet.doPost(mockRequest, mockResponse);
    Key groupKey = KeyFactory.createKey("Group", Integer.parseInt(GROUP_ID));
    Entity returnedEntity = datastore.get(groupKey);

    assertNull(returnedEntity.getProperty("options"));
  }

  @Test
  public void doPost_withMultipleOptions() throws IOException, EntityNotFoundException {
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME);
    groupEntity.setProperty("options", OPTION_IDS);
    datastore.put(groupEntity);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);
    datastore.put(this.createOption(NEW_OPTION));
    datastore.put(this.createOption(NEW_OPTION_2));

    deletePollOptionServlet.doPost(mockRequest, mockResponse);
    Key groupKey = KeyFactory.createKey("Group", Integer.parseInt(GROUP_ID));
    Entity returnedEntity = datastore.get(groupKey);

    List<Long> options = (ArrayList<Long>) returnedEntity.getProperty("options");
    assertEquals(options.size(), 1);
    assertEquals(options.get(0), OPTION_IDS.get(1));
  }
}
