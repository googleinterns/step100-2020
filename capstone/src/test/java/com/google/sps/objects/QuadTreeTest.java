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

  private DatastoreService datastore;
  private QuadTree quadTree;
  private BoundingBox bounds;

  @Before
  public void setUp() {
    helper.setUp();
    datastore = DatastoreServiceFactory.getDatastoreService();
    bounds = new BoundingBox(39.8, -80.2, 40.2, -79.8);
    quadTree = 
      new QuadTree(
        /* NODE_CAPACITY */ 2,
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
    Location LOC_1 = new Location("Test1", "address1", new Coordinate(40.00, -80.1), 0.0);
    Location LOC_2 = new Location("Test2", "address2", new Coordinate(40.00, -80.00), 0.0);
    quadTree.insert(LOC_1);
    quadTree.insert(LOC_2);
    
    assertTrue(quadTree.numPoints == 2);
    assertTrue(quadTree.level == 0);
    assertTrue(quadTree.getChildren().size() == 0);
    
    assertTrue(quadTree.getLocations().contains(LOC_1));
    assertTrue(quadTree.getLocations().contains(LOC_2));
  }

  @Test 
  public void insertTest_oneSplit() {
    Location LOC_1 = new Location("Test1", "address1", new Coordinate(40.00, -80.1), 0.0);
    Location LOC_2 = new Location("Test2", "address2", new Coordinate(40.00, -80.00), 0.0);
    Location LOC_3 = new Location("Test3", "address3", new Coordinate(39.9, -80.1), 0.0);
    Location LOC_4 = new Location("Test4", "address4", new Coordinate(39.9, -79.9), 0.0);
    Location LOC_5 = new Location("Test5", "address5", new Coordinate(40.01, -80.1), 0.0);
    Location LOC_6 = new Location("Test6", "address6", new Coordinate(40.01, -79.9), 0.0);
    quadTree.insert(LOC_1);
    quadTree.insert(LOC_2);
    quadTree.insert(LOC_3);
    quadTree.insert(LOC_4);
    quadTree.insert(LOC_5);
    quadTree.insert(LOC_6);

    assertTrue(quadTree.numPoints == 6);
    assertTrue(quadTree.getChildren().size() == 4);

    // level 0
    assertTrue(quadTree.level == 0);
    assertTrue(quadTree.getLocations().contains(LOC_1));
    assertTrue(quadTree.getLocations().contains(LOC_2));

    // level 1 (after 1 split)
    assertTrue(quadTree.getTopLeftTree().level == 1);
    assertTrue(quadTree.getTopRightTree().getLocations().contains(LOC_6));
    assertTrue(quadTree.getTopLeftTree().getLocations().contains(LOC_5));
    assertTrue(quadTree.getBottomRightTree().getLocations().contains(LOC_4));
    assertTrue(quadTree.getBottomLeftTree().getLocations().contains(LOC_3));
  }


  @Test 
  public void insertTest_twoSplits() {
    Location LOC_1 = new Location("Test1", "address1", new Coordinate(40.00, -80.1), 0.0);
    Location LOC_2 = new Location("Test2", "address2", new Coordinate(40.00, -80.00), 0.0);
    Location LOC_3 = new Location("Test3", "address3", new Coordinate(40.19, -80.1), 0.0);
    Location LOC_4 = new Location("Test4", "address4", new Coordinate(40.19, -80.05), 0.0);
    Location LOC_5 = new Location("Test5", "address5", new Coordinate(40.19, -80.01), 0.0);
    
    quadTree.insert(LOC_1);
    quadTree.insert(LOC_2);
    quadTree.insert(LOC_3);
    quadTree.insert(LOC_4);
    quadTree.insert(LOC_5);

    assertTrue(quadTree.numPoints == 5);
    assertTrue(quadTree.getChildren().size() == 4);

    // level 0 
    assertTrue(quadTree.level == 0);
    assertTrue(quadTree.getLocations().contains(LOC_1));
    assertTrue(quadTree.getLocations().contains(LOC_2));

    // level 1 (after 1 split)
    assertTrue(quadTree.getTopLeftTree().level == 1);
    assertTrue(quadTree.getTopLeftTree().getLocations().contains(LOC_3));
    assertTrue(quadTree.getTopLeftTree().getLocations().contains(LOC_4));

    // level 2 (after 2 splits)
    assertTrue(quadTree.getTopLeftTree().getTopRightTree().level == 2);
    assertTrue(quadTree.getTopLeftTree().getTopRightTree().getLocations().contains(LOC_5));
  }

  @Test 
  public void nearestNeighborTest() {
    Location LOC_1 = new Location("Test1", "address1", new Coordinate(40.00, -80.00), 0.0);
    Location LOC_2 = new Location("Test2", "address2", new Coordinate(40.19, -79.78), 0.0);
    Location LOC_3 = new Location("Test3", "address3", new Coordinate(40.01, -80.00), 0.0);

    quadTree.insert(LOC_3);
    quadTree.insert(LOC_2);

    Location closest = quadTree.nearestNeighbor(LOC_1);

    assertTrue(closest == LOC_3);
  }

  @Test 
  public void nearestKNeighborTest_oneNeighbor() {
    Location LOC_1 = new Location("Test1", "address1", new Coordinate(40.00, -80.00), 0.0);
    Location LOC_2 = new Location("Test2", "address2", new Coordinate(40.19, -79.78), 0.0);
    Location LOC_3 = new Location("Test3", "address3", new Coordinate(40.01, -80.00), 0.0);

    quadTree.insert(LOC_2);
    quadTree.insert(LOC_3);

    ArrayList<Location> closest = quadTree.findKNearestNeighbors(LOC_1, 1);

    assertTrue(closest.get(0) == LOC_3);
  }

  //@Test 
  public void nearestKNeighborTest_twoNeighbors() {
    Location LOC_1 = new Location("Test1", "address1", new Coordinate(40.00, -80.00), 0.0);
    Location LOC_2 = new Location("Test2", "address2", new Coordinate(40.19, -79.78), 0.0);
    Location LOC_3 = new Location("Test3", "address3", new Coordinate(40.01, -80.00), 0.0);
    Location LOC_4 = new Location("Test4", "address4", new Coordinate(40.05, -80.00), 0.0);

    quadTree.insert(LOC_2);
    quadTree.insert(LOC_3);
    quadTree.insert(LOC_4);

    ArrayList<Location> closest = quadTree.findKNearestNeighbors(LOC_1, 2);

    assertTrue(closest.get(0) == LOC_3);
    assertTrue(closest.get(1) == LOC_4);
  }

  //@Test 
  public void nearestKNeighborTest_threeNeighbors() {
    Location LOC_1 = new Location("Test1", "address1", new Coordinate(40.00, -80.00), 0.0);
    Location LOC_2 = new Location("Test2", "address2", new Coordinate(40.19, -79.78), 0.0);
    Location LOC_3 = new Location("Test3", "address3", new Coordinate(40.01, -80.00), 0.0);
    Location LOC_4 = new Location("Test4", "address4", new Coordinate(40.05, -80.00), 0.0);

    quadTree.insert(LOC_2);
    quadTree.insert(LOC_3);
    quadTree.insert(LOC_4);

    ArrayList<Location> closest = quadTree.findKNearestNeighbors(LOC_1, 3);

    assertTrue(closest.get(0) == LOC_3);
    assertTrue(closest.get(1) == LOC_4);
    assertTrue(closest.get(2) == LOC_2);
  }
}