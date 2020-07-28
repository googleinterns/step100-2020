package com.google.sps.servlets;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static com.google.sps.utils.TestUtils.assertEqualsJson;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.gson.Gson;
import com.google.sps.Objects.Group;
import com.google.sps.Objects.Tag;
import com.google.sps.Objects.Challenge;
import com.google.sps.Objects.Comment;
import com.google.sps.Objects.Post;

/**
 * Unit tests for {@link GroupTagsServlet}.
 */
 @RunWith(JUnit4.class)
public class GroupTagsServletTest {
  private static final Long GROUP_ID = (long) 1;

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
              new LocalDatastoreServiceTestConfig()
                  .setDefaultHighRepJobPolicyUnappliedJobPercentage(0),
              new LocalUserServiceTestConfig())
          .setEnvIsLoggedIn(true)
          .setEnvAttributes(
              new HashMap(
                  ImmutableMap.of(
                      "com.google.appengine.api.users.UserService.user_id_key", "user-id")));

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private DatastoreService datastore;
  private GroupTagsServlet groupTagsServlet;
  private ArrayList<Tag> tagsList;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();

    tagsList = new ArrayList<>();
    tagsList.add(new Tag("word", 0.6));
    tagsList.add(new Tag("bird", 0.5));
    tagsList.add(new Tag("curd", 0.4));

    datastore = DatastoreServiceFactory.getDatastoreService();
    populateDatabase(datastore);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    groupTagsServlet = new GroupTagsServlet();
  }

  @After
  public void tearDown() {
    // tear down work
  }

  @Test
  public void doGet_getGroupTags() throws Exception {
    when(mockRequest.getParameter("groupId")).thenReturn("1");
    
    groupTagsServlet.doGet("", mockRequest, mockResponse);

    String response = responseWriter.toString();
    String expectedResponse = new Gson().toJson(tagsList);

    assertTrue(assertEqualsJson(response, expectedResponse));
  }

  private void populateDatabase(DatastoreService datastore) {
    datastore.put(createGroupEntity());
  }

  /* Create a Group entity */
  private Entity createGroupEntity() {
    Entity groupEntity = new Entity("Group", GROUP_ID);
    groupEntity.setProperty("groupName", "Name");
    groupEntity.setProperty("headerImg", "");
    groupEntity.setProperty("memberIds", new ArrayList<String>());
    groupEntity.setProperty("posts", new ArrayList<Long>());
    groupEntity.setProperty("options", new ArrayList<Long>());
    groupEntity.setProperty("challenges", new ArrayList<Long>());
    groupEntity.setProperty("tags", Tag.createTagEntities(tagsList));
    return groupEntity;
  }
}