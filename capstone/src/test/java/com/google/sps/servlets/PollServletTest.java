package com.google.sps.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

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
  private static final List<Long> OPTION_IDS = new ArrayList<Long>(Arrays.asList(1L, 2L));
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
  @Spy private PollServlet pollServlet;
  private StringWriter responseWriter;
  private DatastoreService datastore;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
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

  private void addOptionsToDb() {
    ImmutableList.Builder<Entity> option = ImmutableList.builder();
    for (String text : OPTION_TEXT) {
      option.add(createOption(text));
    }
    datastore.put(option.build());
  }

  private Entity createOption(String text) {
    Entity optionEntity = new Entity("Option");
    long timestamp = System.currentTimeMillis();
    optionEntity.setProperty("text", text);
    optionEntity.setProperty("votes", new ArrayList<String>());
    optionEntity.setProperty("timestamp", timestamp);
    return optionEntity;
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

  private List<Option> getOptions() {
    List<Option> options = new ArrayList<Option>();
    return options;
  }

  @Test
  public void doGet_userLoggedIn_noOptions() throws IOException {
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME);
    datastore.put(groupEntity);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);
    doReturn(groupEntity).when(pollServlet).getGroupEntity(mockRequest, mockResponse, datastore);

    pollServlet.doGet(mockRequest, mockResponse);

    String response = responseWriter.toString();
    assertTrue(response.contains(USER_ID));
  }

  @Test
  public void doGet_userNotLoggedIn() throws IOException {
    helper.setEnvIsLoggedIn(false);

    pollServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains("Oops an error happened!"));
  }

  /**
   * Tests that the option is correctly stored into datastore.
   *
   * @throws IOException exception thrown if cannot read or write from file
   * @throws EntityNotFoundException exception thrown if cannot find entity in datastore
   */
  @Test
  public void doPost_userLoggedIn_optionsTest() throws IOException, EntityNotFoundException {
    Entity groupEntity = this.createGroup(USER_ID, GROUP_NAME);
    datastore.put(groupEntity);
    when(mockRequest.getParameter("text")).thenReturn(NEW_OPTION);
    when(mockRequest.getParameter("groupId")).thenReturn(GROUP_ID);
    doReturn(groupEntity).when(pollServlet).getGroupEntity(mockRequest, mockResponse, datastore);

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
    doReturn(groupEntity).when(pollServlet).getGroupEntity(mockRequest, mockResponse, datastore);

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
    Entity optionEntity = datastore.get(optionKey);
  }
}
