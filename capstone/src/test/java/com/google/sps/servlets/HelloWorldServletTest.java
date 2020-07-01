package com.google.sps.servlets;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableList;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link HelloWorldServlet}.
 */
@RunWith(JUnit4.class)
public class HelloWorldServletTest {

  // Set no eventual consistency, that way queries return all results.
  // https://cloud.google.com/appengine/docs/java/tools/localunittesting
  // #Java_Writing_High_Replication_Datastore_tests
  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));

  private static final ImmutableList<String> TEST_NAMES =
      ImmutableList.<String>builder()
          .add("Andrew")
          .add("Nico")
          .build();
  private static final String NEW_USER = "Molly";
  private static final String MISSING_USER = "Adam";
  private static final String HELLO_WORLD = "helloWorld";

  @Mock
  private HttpServletRequest mockRequest;
  @Mock
  private HttpServletResponse mockResponse;
  private StringWriter responseWriter;
  private DatastoreService datastore;
  private HelloWorldServlet helloWorldServlet;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    populateDatabase(datastore);

    // Set up a fake HTTP response.
    responseWriter = new StringWriter();
    when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));

    helloWorldServlet = new HelloWorldServlet();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void doGet_writesHelloWorld() throws Exception {
    helloWorldServlet.doGet(mockRequest, mockResponse);

    String response = responseWriter.toString();
    for (String name : TEST_NAMES) {
      assertThat(response).contains(name);
    }
    assertThat(response).contains(HELLO_WORLD);
  }

  @Test
  public void doGet_failsHelloWorldWithExtraUser() throws Exception {
    helloWorldServlet.doGet(mockRequest, mockResponse);

    String response = responseWriter.toString();
    assertFalse(response.contains(MISSING_USER));
  }

  @Test
  public void doPost_addNewUser() throws Exception {
    when(mockRequest.getParameter("user")).thenReturn(NEW_USER);

    helloWorldServlet.doPost(mockRequest, mockResponse);

    Key userKey = KeyFactory.createKey("User", NEW_USER);

    Entity user = datastore.get(userKey);
    assertThat(user.getProperty("name")).isEqualTo(NEW_USER);
  }

  private void populateDatabase(DatastoreService datastore) {
    // Add test data.
    ImmutableList.Builder<Entity> people = ImmutableList.builder();
    for (String name : TEST_NAMES) {
      people.add(createUser(name));
    }
    datastore.put(people.build());
    datastore.put(createHelloWorld());
  }

  private Entity createUser(String name) {
    // Setting key so we can retrieve the User using the name as the Key
    Entity userEntity = new Entity("User", name);
    userEntity.setProperty("name", name);
    return userEntity;
  }

  private Entity createHelloWorld() {
    Entity helloWorldEntity = new Entity("Hello World");
    helloWorldEntity.setProperty("id", "helloWorld");
    return helloWorldEntity;
  }
}