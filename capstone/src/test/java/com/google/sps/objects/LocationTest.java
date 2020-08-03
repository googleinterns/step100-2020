package com.google.sps.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.util.HashMap;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;
import com.google.common.collect.ImmutableMap;
import com.google.sps.Objects.Location;
import com.google.sps.Objects.Coordinate;

/** Unit tests for Location. */
public class LocationTest {

  private static final String USER_EMAIL = "test@mctest.com";
  private static final String USER_ID = "testy-mc-test";
  private static final String LOCATION_NAME = "SONIC Drive In";
  private static final String LOCATION_ADDRESS = "800 N Canal Blvd";
  private static final double LATITUDE = 29.814697;
  private static final double LONGITUDE = -90.814742;
  private static final double DISTANCE = 0.000555;

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

  private Location location;
  private DatastoreService datastore;
  private Coordinate coordinate;

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    coordinate = new Coordinate(LATITUDE, LONGITUDE);
    location =
        new Location(
            /* locationName */ LOCATION_NAME,
            /* locationAddress */ LOCATION_ADDRESS,
            /* coordinate */ coordinate, 
            /* distance */ DISTANCE);
  }

  @After
  public void tearDown() {
    helper.tearDown();
    location = null;
  }

  @Test
  public void getLocationName() {
    assertEquals(location.getLocationName(), LOCATION_NAME);
  }

  @Test
  public void getAddress() {
    assertEquals(location.getAddress(), LOCATION_ADDRESS);
  }

  @Test
  public void toFromEntityTest() throws Exception {
    Entity locationEntity = location.toEntity();
    Location returnedLocation = Location.fromEntity(locationEntity);

    assertTrue(returnedLocation.equals(location));
  }
}

