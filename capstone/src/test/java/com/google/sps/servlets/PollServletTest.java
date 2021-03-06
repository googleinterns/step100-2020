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
import java.util.List;

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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.sps.Objects.Option;

/**
 * Unit tests for {@link PollServlet}.
 *
 * @author lucyqu
 */
@RunWith(JUnit4.class)
public class PollServletTest {

  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";
  private static final String NEW_OPTION = "Do a 5k";
  private static final long NEW_OPTION_ID = 2;
  private static final List<String> OPTION_TEXT =
      new ArrayList<String>(Arrays.asList("Run", "Jog", "Climb a tree", "Bungee jump"));
  private static final List<Long> OPTION_IDS = new ArrayList<Long>(Arrays.asList(2L));
  private static final String GROUP_NAME = "Runners Club";
  private static final String GROUP_ID = "1";

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
  private PollServlet pollServlet;
  private StringWriter responseWriter;
  private DatastoreService datastore;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    pollServlet = new PollServlet();

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
  }

  @After
  public void tearDown() {
    helper.tearDown();
    responseWriter = null;
    datastore = null;
    pollServlet = null;
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

  private Entity createOption() {
    Entity optionEntity = new Entity("Option");
    optionEntity.setProperty("text", NEW_OPTION);
    optionEntity.setProperty("votes", new ArrayList<Long>());
    optionEntity.setProperty("timestamp", System.currentTimeMillis());
    return optionEntity;
  }

  private void putGroupInDb() {
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME);
    datastore.put(groupEntity);
  }

  @Test
  public void doGet_userLoggedIn_noOptions() throws IOException {
    this.putGroupInDb();
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);

    pollServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains(USER_ID));
  }

  @Test
  public void doGet_userLoggedIn_withOption() throws IOException {
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME);
    groupEntity.setProperty("options", OPTION_IDS);
    datastore.put(groupEntity);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);
    datastore.put(this.createOption());

    pollServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains(NEW_OPTION));
  }

  @Test
  public void doGet_userNotLoggedIn() throws IOException {
    helper.setEnvIsLoggedIn(false);

    pollServlet.doGet(mockRequest, mockResponse);
    ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    verify(mockResponse).sendRedirect(captor.capture());

    assertEquals("/_ah/login?continue=%2F", captor.getValue());
  }

  /**
   * Tests that the option is correctly stored into datastore.
   *
   * @throws IOException exception thrown if cannot read or write from file
   * @throws EntityNotFoundException exception thrown if cannot find entity in datastore
   */
  @Test
  public void doPost_userLoggedIn_optionsTest() throws IOException, EntityNotFoundException {
    this.putGroupInDb();
    when(mockRequest.getParameter("text")).thenReturn(NEW_OPTION);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);

    pollServlet.doPost(mockRequest, mockResponse);
    Key optionKey = KeyFactory.createKey("Option", NEW_OPTION_ID);
    Entity optionEntity = datastore.get(optionKey);
    long id = optionEntity.getKey().getId();
    Option option = new Option(id, NEW_OPTION, new ArrayList<String>());
    Option returnedOption = Option.fromEntity(optionEntity);
    String optionJson = new Gson().toJson(option);
    String returnedOptionJson = new Gson().toJson(returnedOption);

    assertEquals(optionJson, returnedOptionJson);
  }

  /**
   * Tests that the options list for the group entity is updated properly when user inputs new
   * option.
   *
   * @throws IOException exception thrown if cannot read or write from file
   * @throws EntityNotFoundException exception thrown if cannot find entity in datastore
   */
  @Test
  public void doPost_userLoggedIn_groupOptionsListTest()
      throws IOException, EntityNotFoundException {
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME);
    datastore.put(groupEntity);
    when(mockRequest.getParameter("text")).thenReturn(NEW_OPTION);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);

    pollServlet.doPost(mockRequest, mockResponse);
    List<Long> options = new ArrayList<Long>();
    options.add(NEW_OPTION_ID);
    groupEntity.setProperty("options", options);
    Key groupKey = KeyFactory.createKey("Group", Integer.parseInt(GROUP_ID));
    Entity groupFromDatastore = datastore.get(groupKey);
    String groupFromDatastoreJson = new Gson().toJson(groupFromDatastore);
    String groupJson = new Gson().toJson(groupEntity);

    assertEquals(groupFromDatastoreJson, groupJson);
  }

  @Test(expected = EntityNotFoundException.class)
  public void doPost_userNotLoggedIn() throws IOException, EntityNotFoundException {
    helper.setEnvIsLoggedIn(false);
    when(mockRequest.getParameter("text")).thenReturn(NEW_OPTION);

    pollServlet.doPost(mockRequest, mockResponse);
    // id of Option increments for each new Option that is added
    Key optionKey = KeyFactory.createKey("Option", OPTION_TEXT.size() + 1);
    // trigger EntityNotfoundException
    datastore.get(optionKey);
  }
}
