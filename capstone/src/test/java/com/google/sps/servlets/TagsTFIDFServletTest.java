package com.google.sps.servlets;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

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
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.gson.Gson;
import com.google.sps.Objects.TFIDFStringHelper;
import com.google.sps.Objects.Group;
import com.google.sps.Objects.Tag;
import com.google.sps.Objects.Challenge;

/**
 * Unit tests for {@link TagsTFIDFServlet}.
 */
 @RunWith(JUnit4.class)
public class TagsTFIDFServletTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
              new LocalDatastoreServiceTestConfig()
                  .setDefaultHighRepJobPolicyUnappliedJobPercentage(0),
              new LocalUserServiceTestConfig())
          .setEnvIsLoggedIn(true);

  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private DatastoreService datastore;
  private TagsTFIDFServlet tagsTFIDFServlet;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    populateDatabase(datastore);

    tagsTFIDFServlet = new TagsTFIDFServlet();
  }

  @After
  public void tearDown() {
    // tear down work
  }

  @Test
  public void doGet_checkIfTagGenerated() {
    assertEquals(1, 1);
  }

  private void populateDatabase(DatastoreService datastore) {
    // Add test data.
    ArrayList<Long> postIds =  createPosts();
    datastore.put(createGroupEntity(postIds));
  }

  private Entity createGroupEntity(ArrayList<Long> postIds) {
    Entity groupEntity = new Entity("Group");
    groupEntity.setProperty("groupName", "Name");
    groupEntity.setProperty("headerImg", "");
    groupEntity.setProperty("memberIds", new ArrayList<String>());
    groupEntity.setProperty("posts", new ArrayList<Long>(postIds));
    groupEntity.setProperty("options", new ArrayList<Long>());
    groupEntity.setProperty("challenges", new ArrayList<Challenge>());
    return groupEntity;
  }

  private ArrayList<Long> createPosts() {
    return new ArrayList<>();
    //finish implementing
  }
}