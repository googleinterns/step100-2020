package com.google.sps.Objects;
import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.text.DecimalFormat;
import java.io.Serializable;

import com.google.sps.Objects.Location;
import com.google.sps.Objects.Coordinate;
import com.google.sps.Objects.BoundingBox;

public class QuadTree implements Serializable {
  private final int NODE_CAPACITY;
  private List<QuadTree> children;
  private List<Location> locations;
  private QuadTree topLeftTree, topRightTree, botLeftTree, botRightTree; 
  private BoundingBox bounds;

  // For keeping track of splits and number of points within each quadtree
  public int level;
  public int numPoints;

  public QuadTree(int NODE_CAPACITY, int level, BoundingBox bounds) {
    this.NODE_CAPACITY = NODE_CAPACITY;
    this.bounds = bounds;
    this.children = new ArrayList<QuadTree>();
    this.locations = new ArrayList<Location>();
    this.level = level;
    this.numPoints = 0;
  
    this.topLeftTree  = null; 
    this.topRightTree = null; 
    this.botLeftTree  = null; 
    this.botRightTree = null; 
  }

  public List<QuadTree> getChildren() {
    return this.children;
  }

  public List<Location> getLocations() {
    return this.locations;
  }

  public QuadTree getTopLeftTree() {
    return topLeftTree;
  }

  public QuadTree getTopRightTree() {
    return topRightTree;
  }

  public QuadTree getBottomLeftTree() {
    return botLeftTree;
  }

  public QuadTree getBottomRightTree() {
    return botRightTree;
  }

  public double euclidianDistance(Location loc1, Location loc2) {
    return Math.sqrt(Math.pow(loc1.getLatitude() - loc2.getLatitude(), 2) + Math.pow(loc1.getLongitude() - loc2.getLongitude(), 2));
  }

  public boolean insert(Location node) { 
    if (node == null || !bounds.containsPoint(node)) return false; 

    if (locations.size() < NODE_CAPACITY) {
      locations.add(node);
      numPoints++;
      return true;
    }

    // Exceeded the capacity so split into four quadrants
    if (topLeftTree == null) {
      split();
    }

    // Add point to correct quadrant 
    for (QuadTree qt: children) {
      if (qt.insert(node)) {
        numPoints ++;
        return true;
      }
    }

    // Node is not in this bounding box
    return false;
  }

  void split() {
    double xMin = bounds.getXMin();
    double yMin = bounds.getYMin();
    double xMax = bounds.getXMax();
    double yMax = bounds.getYMax();

    // Find mid x and y within bounds 
    double xOffset = xMin + (xMax - xMin) / 2;
    double yOffset = yMin + (yMax - yMin) / 2;

    /*
    * Split space into 4 quadrant trees and create new bounding boxes 
    * 
    *xMax    ************************
    *        *          *           *
    *        * topLeft  *  topRight *
    *        *          *           *
    *xOffset ************************ 
    *        *          *           *
    *        * botLeft  *  botRight *
    *        *          *           *
    *xMin    ************************
    *        yMin     yOffset     yMax
    */
    botRightTree = new QuadTree(NODE_CAPACITY, level + 1, 
        new BoundingBox(xMin, yOffset, xOffset, yMax));
    topRightTree = new QuadTree(NODE_CAPACITY, level + 1, 
        new BoundingBox(xOffset, yOffset, xMax, yMax));
    botLeftTree = new QuadTree(NODE_CAPACITY, level + 1, 
        new BoundingBox(xMin, yMin, xOffset, yOffset));
    topLeftTree = new QuadTree(NODE_CAPACITY, level + 1, 
        new BoundingBox(xOffset, yMin, xMax, yOffset));

    children.add(topRightTree);
    children.add(topLeftTree);
    children.add(botLeftTree);
    children.add(botRightTree);
  }

  public Location nearestNeighbor(Location loc) {
    Location firstInList = this.locations.get(0);
    return nearestNeighbor(loc, firstInList);
  }

  public Location nearestNeighbor(Location loc, Location closest) {

    double closestDistance = euclidianDistance(loc, closest);

    if (!bounds.intersectsCircle(loc, closestDistance)) {
      return closest;
    }

    // Check distance between location and points in this box
    for (Location location: locations) {
      if (euclidianDistance(loc, location) < closestDistance) {
        closest = location;
        closestDistance = euclidianDistance(loc, location);
      }
    }

    // Check distance between location and points within all children
    if (!(topLeftTree == null)) {
      for (QuadTree qt : children) {
        closest = qt.nearestNeighbor(loc, closest);
      }
    }
    return closest;
  }

  public ArrayList<Location> findKNearestNeighbors(Location loc, int k) {
    // Order PriorityQueue by distance 
    PriorityQueue<Location> pq = new PriorityQueue<Location>(k, Comparator.comparingDouble( locationPQ -> -euclidianDistance(loc, locationPQ)));

    pq = findKNearestNeighborsHelper(loc, k, pq);

    ArrayList<Location> nearestLocations = new ArrayList<>();

    while (!pq.isEmpty()) {
      nearestLocations.add(0, pq.poll());
    }

    return nearestLocations;
  }

  public PriorityQueue<Location> findKNearestNeighborsHelper(Location loc, int k, PriorityQueue<Location> pq) {

    if (locations.size() == 0 || loc == null) {
      return pq;
    }

    Location maxClosest = pq.peek();
    double maxClosestDistance = maxClosest == null ? Double.MAX_VALUE : euclidianDistance(maxClosest, loc);

    // Check distance between location and points in this box
    for (Location location: locations) {
      /* 
      * Convert euclidianDistance to miles (one degree of latitude is approx.
      * 69miles)
      */
      DecimalFormat df = new DecimalFormat("#.000");
      double dist = euclidianDistance(location, loc) * 69;
      location.setDistance(Double.valueOf(df.format(dist)));
      if (euclidianDistance(location, loc) < maxClosestDistance) {
        if (pq.size() >= k) {
          // Remove current maxClosest
          pq.poll();
        } 
        // Insert next maxClosest into queue
        if (!pq.contains(location)) pq.add(location);
      } else if (pq.size() < k) {
        if (!pq.contains(location)) pq.add(location);
      }
      // Update maxClosestDistance to element last in priority queue
      maxClosestDistance = euclidianDistance(loc, pq.peek());
    }

    // Check distance between location and points within all children
    if (!(topLeftTree == null)) {
      for (QuadTree qt : children) {
        pq = qt.findKNearestNeighborsHelper(loc, k, pq);
      }
    }

    return pq;
  }
}