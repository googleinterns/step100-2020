package com.google.sps.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.ArrayList;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.sps.Objects.Coordinate;
import com.google.sps.Objects.GroupLocation;
import com.google.sps.Objects.User;
import com.google.sps.Objects.Badge;
import com.google.sps.Objects.Challenge;

/**
 * Unit tests for GroupLocation.
 *
*/
public class GroupLocationTest {

  private static final String USER_ID = "123123123";
  private static final String USER_EMAIL = "test@mctest.com";
  private static final String OTHER_ID = "other";
  private static final String OTHER_EMAIL = "other@test.com";
  private static final String OTHER_ID2 = "other2";
  private static final String OTHER_EMAIL2 = "other2@test.com";
  private static final String OTHER_ID3 = "other3";
  private static final String OTHER_EMAIL3 = "other3@test.com";

  private static final double COORD1_LAT = 40.7721984;
  private static final double COORD1_LNG = -73.97933858;
  private static final double COORD2_LAT = 40.83389292;
  private static final double COORD2_LNG = -73.95543518;
  private static final double COORD3_LAT = 40.76680496;
  private static final double COORD3_LNG = -73.96009353;
  private static final double MID_LAT = 40.790965887749465;
  private static final double MID_LNG = -73.96495858110758;

  private static final String GROUP_1_ID = "1";
  private static final String GROUP_NAME = "The 3 Musketeers";
  private static final String HEADER_IMAGE = "";

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

  private static final User USER_1 = new User(
                          USER_ID, 
                          "Test", 
                          "McTest", 
                          USER_EMAIL, 
                          /* phoneNumber= */ "123-456-7890", 
                          /* profilePic= */ "",
                          /* address= */ "", 
                          /* latitude= */ COORD1_LAT,
                          /* longitude= */ COORD1_LNG,
                          /* badges= */ new LinkedHashSet<Badge>(), 
                          /* groups= */ new LinkedHashSet<Long>(), 
                          /* interests= */ new ArrayList<String>());

  private static final User OTHER_USER = new User(
                          OTHER_ID,
                          "Test Two",
                          "McTest",
                          OTHER_EMAIL,
                          /* phoneNumber= */ "123-456-1111",
                          /* profilePic= */ "",
                          /* address= */ "",
                          /* latitude= */ COORD2_LAT,
                          /* longitude= */ COORD2_LNG,
                          /* badges= */ new LinkedHashSet<Badge>(),
                          /* groups= */ new LinkedHashSet<Long>(),
                          /* interests= */ new ArrayList<String>());

  private static final User OTHER_USER2 = new User(
                          OTHER_ID2,
                          "Test TwoTwo",
                          "McTest",
                          OTHER_EMAIL2,
                          /* phoneNumber= */ "123-456-2222",
                          /* profilePic= */ "",
                          /* address= */ "",
                          /* latitude= */ COORD3_LAT,
                          /* longitude= */ COORD3_LNG,
                          /* badges= */ new LinkedHashSet<Badge>(),
                          /* groups= */ new LinkedHashSet<Long>(),
                          /* interests= */ new ArrayList<String>()); 

  private static final User OTHER_USER3 = new User(
                          OTHER_ID3,
                          "Test TwoTwo",
                          "McTest",
                          OTHER_EMAIL3,
                          /* phoneNumber= */ "123-456-2222",
                          /* profilePic= */ "",
                          /* address= */ "",
                          /* latitude= */ 0.0,
                          /* longitude= */ 0.0,
                          /* badges= */ new LinkedHashSet<Badge>(),
                          /* groups= */ new LinkedHashSet<Long>(),
                          /* interests= */ new ArrayList<String>());                       
  Coordinate COORDINATE_1 = new Coordinate(COORD1_LAT, COORD1_LNG);
  Coordinate COORDINATE_2 = new Coordinate(COORD2_LAT, COORD2_LNG);
  Coordinate COORDINATE_3 = new Coordinate(COORD3_LAT, COORD3_LNG);

  ArrayList<Coordinate> groupCoordinates = new ArrayList(Arrays.asList(COORDINATE_1, COORDINATE_2, COORDINATE_3));

  private GroupLocation groupLocation;
  private DatastoreService datastore;

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();

    // Add test data
    populateDatabase(datastore);
    groupLocation = new GroupLocation(Long.parseLong(GROUP_1_ID));
  }

  @After
  public void tearDown() {
    helper.tearDown();
    groupLocation = null;
  }

  private void populateDatabase(DatastoreService datastore) {
    // Add test data.
    Entity group1 = createGroupEntity();
    datastore.put(group1);
    datastore.put(USER_1.toEntity());
    datastore.put(OTHER_USER.toEntity());
    datastore.put(OTHER_USER2.toEntity());
  }

  /* Create a Group entity */
  private Entity createGroupEntity() {
    Entity groupEntity = new Entity("Group");
    groupEntity.setProperty("groupName", GROUP_NAME);
    groupEntity.setProperty("headerImg", HEADER_IMAGE);
    groupEntity.setProperty("memberIds", new ArrayList<String>(Arrays.asList(USER_ID, OTHER_ID , OTHER_ID2)));
    groupEntity.setProperty("posts", null);
    groupEntity.setProperty("options", new ArrayList<Long>());
    groupEntity.setProperty("challenges", new ArrayList<Challenge>());
    groupEntity.setProperty("midLatitude", 0.0);
    groupEntity.setProperty("midLongitude", 0.0);
    return groupEntity;
  }

  @Test 
  public void findGroupMidPointTest() throws Exception {
    Coordinate coordReturned = groupLocation.findGroupMidPoint(Long.parseLong(GROUP_1_ID));

    assertTrue(coordReturned.getLat() == MID_LAT);
    assertTrue(coordReturned.getLng() == MID_LNG);
  }

  @Test 
  public void findGroupMidPointTest_invalidGroup() throws Exception {
    Coordinate coordReturned = groupLocation.findGroupMidPoint(34L);

    assertTrue(coordReturned == null);
  }

  @Test 
  public void findGroupMidPointTest_userWithMissingCoordinates() throws Exception {
    datastore.put(OTHER_USER3.toEntity());

    Coordinate coordReturned = groupLocation.findGroupMidPoint(Long.parseLong(GROUP_1_ID));

    assertTrue(coordReturned.getLat() == MID_LAT);
    assertTrue(coordReturned.getLng() == MID_LNG);
  }
}