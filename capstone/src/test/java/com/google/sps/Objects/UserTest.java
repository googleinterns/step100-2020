package com.google.sps.servlets;

import static org.junit.Assert.assertTrue;

import com.google.sps.Objects.User;
import com.google.sps.Objects.Badge;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.common.collect.ImmutableMap;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Unit tests for {@link User}.
 */
 @RunWith(JUnit4.class)
public class UserTest {
  private static final String USER_EMAIL = "test@mctest.com";
  private static final String USER_ID = "testy-mc-test";

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

  private static final ArrayList<String> INTERESTS_LIST = new ArrayList<String>( 
      Arrays.asList("Testing", "Dancing"));
  private static final User USER_1 = new User(
                          USER_ID, 
                          "Test", 
                          "McTest", 
                          USER_EMAIL, 
                          /* phoneNumber= */ "123-456-7890", 
                          /* profilePic= */ "", 
                          /* badges= */ new LinkedHashSet<Badge>(), 
                          /* groups= */ new LinkedHashSet<Long>(), 
                          /* interests= */ INTERESTS_LIST);

  private DatastoreService datastore;

  @Before
  public void setUp() throws Exception {
    datastore = DatastoreServiceFactory.getDatastoreService();
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void toFromEntityTest() throws Exception {
    Entity userEntity = USER_1.toEntity();
    datastore.put(userEntity);

    Key userKey = KeyFactory.createKey("User", USER_ID);
    Entity retrievedEntity = datastore.get(userKey);

    User retrievedUser = User.fromEntity(retrievedEntity);

    String jsonRetrieved = new Gson().toJson(retrievedUser);
    String jsonOriginal = new Gson().toJson(USER_1);
    
    assertTrue(jsonRetrieved.equals(jsonOriginal));
  }
}