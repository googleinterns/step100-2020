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
import com.google.sps.Objects.QuadTree;
import com.google.sps.Objects.BoundingBox;
import com.google.sps.Objects.Location;
import com.google.sps.Objects.Coordinate;

/**
 * Unit tests for QuadTree.
 *
*/
public class QuadTreeTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig()
              .setDefaultHighRepJobPolicyUnappliedJobPercentage(0));
  
  private static final Location LOC_1 = new Location("Test1", "address1", new Coordinate(40.48862915, -80.14141311), 0.0);
  private static final Location LOC_2 = new Location("Test2", "address2", new Coordinate(40.50170965, -80.09777729), 0.0);
  private static final Location LOC_3 = new Location("Test3", "address3", new Coordinate(40.57455069, -80.00992621), 0.0); 
  private static final Location LOC_4 = new Location("Test4", "address4", new Coordinate(40.29651063, -79.98241256), 0.0); 
  private static final Location LOC_5 = new Location("Test5", "address5", new Coordinate(40.49303469, -80.10254273), 0.0); 
  private static final Location LOC_6 = new Location("Test6", "address6", new Coordinate(40.50245938, -80.08270203), 0.0); 
  private static final Location LOC_7 = new Location("Test7", "address7", new Coordinate(40.4513275, -79.86228567), 0.0); 
  private static final Location LOC_8 = new Location("Test8", "address8", new Coordinate(40.41427404,	-79.99527299), 0.0); 

  private DatastoreService datastore;
  private QuadTree quadTree;
  private BoundingBox bounds;

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    bounds = new BoundingBox(40.19651063, -80.24141311, 40.67455069, -79.76228567);
    quadTree = 
      new QuadTree(
        /* NODE_CAPACITY */ 4,
        /* level */ 0,
        /* BoundingBox */ bounds);
  }

  @After
  public void tearDown() {
    helper.tearDown();
    quadTree = null;
  }

  @Test 
  public void insertTest_noSplits() {
    quadTree.insert(LOC_1);
    quadTree.insert(LOC_2);
    quadTree.insert(LOC_3);
    quadTree.insert(LOC_4);
    
    assertTrue(quadTree.numPoints == 4);
    assertTrue(quadTree.level == 0);
    assertTrue(quadTree.getChildren().size() == 0);
    
    assertTrue(quadTree.getLocations().contains(LOC_1));
    assertTrue(quadTree.getLocations().contains(LOC_2));
    assertTrue(quadTree.getLocations().contains(LOC_3));
    assertTrue(quadTree.getLocations().contains(LOC_4));
  }

  @Test 
  public void insertTest_oneSplit() {
    quadTree.insert(LOC_1);
    quadTree.insert(LOC_2);
    quadTree.insert(LOC_3);
    quadTree.insert(LOC_4);
    quadTree.insert(LOC_5);
    quadTree.insert(LOC_6);
    quadTree.insert(LOC_7);
    quadTree.insert(LOC_8);

    assertTrue(quadTree.numPoints == 8);
    assertTrue(quadTree.getChildren().size() == 4);

    assertTrue(quadTree.getTopLeftTree().getLocations().contains(LOC_5));
    assertTrue(quadTree.getTopLeftTree().getLocations().contains(LOC_6));
    assertTrue(quadTree.getTopRightTree().getLocations().contains(LOC_7));
    assertTrue(quadTree.getBottomRightTree().getLocations().contains(LOC_8));
  }


  @Test 
  public void nearestNeighborTest() {
    quadTree.insert(LOC_1);
    quadTree.insert(LOC_2);
    quadTree.insert(LOC_5);
    quadTree.insert(LOC_7);
    quadTree.insert(LOC_8);
    quadTree.insert(LOC_3);

    Location closest = quadTree.nearestNeighbor(LOC_6);

    assertTrue(closest == LOC_2);
  }

  @Test 
  public void nearestKNeighborTest_oneNeighbor() {
    quadTree.insert(LOC_1);
    quadTree.insert(LOC_2);
    quadTree.insert(LOC_5);
    quadTree.insert(LOC_7);
    quadTree.insert(LOC_8);
    quadTree.insert(LOC_3);

    ArrayList<Location> closest = quadTree.findKNearestNeighbors(LOC_6, 1);

    assertTrue(closest.get(0) == LOC_2);
  }

  @Test 
  public void nearestKNeighborTest_twoNeighbors() {
    quadTree.insert(LOC_1);
    quadTree.insert(LOC_2);
    quadTree.insert(LOC_5);
    quadTree.insert(LOC_7);
    quadTree.insert(LOC_8);
    quadTree.insert(LOC_3);

    ArrayList<Location> closest = quadTree.findKNearestNeighbors(LOC_6, 2);

    assertTrue(closest.get(0) == LOC_2);
    assertTrue(closest.get(1) == LOC_5);
  }

  @Test 
  public void nearestKNeighborTest_threeNeighbors() {
    quadTree.insert(LOC_1);
    quadTree.insert(LOC_2);
    quadTree.insert(LOC_5);
    quadTree.insert(LOC_7);
    quadTree.insert(LOC_8);
    quadTree.insert(LOC_3);

    ArrayList<Location> closest = quadTree.findKNearestNeighbors(LOC_6, 3);

    assertTrue(closest.get(0) == LOC_2);
    assertTrue(closest.get(1) == LOC_5);
    assertTrue(closest.get(2) == LOC_1);
  }
}