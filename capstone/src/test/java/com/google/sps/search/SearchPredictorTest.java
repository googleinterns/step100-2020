package com.google.sps.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.sps.database.DatabaseRetriever;

public class SearchPredictorTest {

  private static final String USER_EMAIL = "test@test.com";
  private static final String USER_ID = "test";

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
  @Mock private Serializable serial;
  @Spy private DatabaseRetriever dbRetriever;
  private DatastoreService datastore;

  @Before
  public void setUp() throws IOException {
    MockitoAnnotations.initMocks(this);
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
  }

  @After
  public void tearDown() {
    helper.tearDown();
    datastore = null;
  }

  //  @Test
  public void searchPrefixTest() {
    List<String> names = new ArrayList<String>();
    names.add("Lucy Qu");
    names.add("Lucinda Ang");
    names.add("Jack Doe");
    when(dbRetriever.getNamesFromDb()).thenReturn(names);

    SearchPredictor searchPredictor = new SearchPredictor();
    List<String> suggestions = searchPredictor.suggest("Luc");

    assertEquals(2, suggestions.size());
    assertTrue(suggestions.contains("Lucy Qu"));
    assertTrue(suggestions.contains("Lucinda Ang"));
    assertFalse(suggestions.contains("Jack Doe"));
  }
}
