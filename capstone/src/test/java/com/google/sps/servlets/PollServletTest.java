package com.google.sps.servlets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
  private static final List<String> OPTION_TEXT =
      new ArrayList<String>(Arrays.asList("Run", "Jog", "Climb a tree", "Bungee jump"));

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
  private PollServlet pollServlet;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    // Add test data
    ImmutableList.Builder<Entity> option = ImmutableList.builder();
    for (String text : OPTION_TEXT) {
      option.add(createOption(text));
    }
    datastore.put(option.build());

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    pollServlet = new PollServlet();
  }

  private Entity createOption(String text) {
    Entity optionEntity = new Entity("Option");
    long timestamp = System.currentTimeMillis();
    optionEntity.setProperty("text", text);
    optionEntity.setProperty("votes", new ArrayList<String>());
    optionEntity.setProperty("timestamp", timestamp);
    return optionEntity;
  }

  @After
  public void tearDown() {
    helper.tearDown();
    responseWriter = null;
    datastore = null;
    pollServlet = null;
  }

  @Test
  public void doGet_userLoggedIn() throws IOException {
    pollServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains(USER_ID));
    for (String text : OPTION_TEXT) {
      assertTrue(response.contains(text));
    }
  }

  @Test
  public void doGet_userNotLoggedIn() throws IOException {
    helper.setEnvIsLoggedIn(false);

    pollServlet.doGet(mockRequest, mockResponse);
    String response = responseWriter.toString();

    assertTrue(response.contains("Oops an error happened!"));
  }

  @Test
  public void doPost_userLoggedIn() throws IOException, EntityNotFoundException {
    when(mockRequest.getParameter("text")).thenReturn(NEW_OPTION);

    pollServlet.doPost(mockRequest, mockResponse);
    // id of Option increments for each new Option that is added
    Key optionKey = KeyFactory.createKey("Option", OPTION_TEXT.size() + 1);
    Entity optionEntity = datastore.get(optionKey);
    long id = optionEntity.getKey().getId();
    Option option = new Option(id, NEW_OPTION, new ArrayList<String>());
    Option returnedOption = Option.fromEntity(optionEntity);

    assertEquals(option.getText(), returnedOption.getText());
    assertEquals(option.getId(), returnedOption.getId());
    assert option.getVotes().size() == returnedOption.getVotes().size();
  }

  @Test(expected = EntityNotFoundException.class)
  public void doPost_userNotLoggedIn() throws IOException, EntityNotFoundException {
    helper.setEnvIsLoggedIn(false);
    when(mockRequest.getParameter("text")).thenReturn(NEW_OPTION);

    pollServlet.doPost(mockRequest, mockResponse);
    // id of Option increments for each new Option that is added
    Key optionKey = KeyFactory.createKey("Option", OPTION_TEXT.size() + 1);
    Entity optionEntity = datastore.get(optionKey);
  }
}
