package com.google.sps.Objects;
import java.util.List;
import java.util.ArrayList;

import com.google.sps.Objects.Location;
import com.google.sps.Objects.Coordinate;
import com.google.sps.Objects.BoundingBox;

public class QuadTree {
  private final int NODE_CAPACITY;
  private List<QuadTree> children;
  private List<Location> locations;
  private QuadTree topLeftTree, topRightTree, botLeftTree, botRightTree; 
  private BoundingBox bounds;

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

  public double euclidianDistance(Location loc1, Location loc2) {
    return Math.sqrt(Math.pow(loc1.getLatitude() - loc2.getLatitude(), 2) + Math.pow(loc1.getLongitude() - loc2.getLongitude(), 2));
  }

  public boolean insert(Location node) { 
    if (node == null) return false; 

    if (!bounds.containsPoint(node)) {
      return false;
    }

    if (locations.size() < NODE_CAPACITY) {
      locations.add(node);
      numPoints++;
			return true;
		}

		// Exceeded the capacity so split it in FOUR
		if (topLeftTree == null) {
			split();
		}

		// Check coordinates belongs to which partition
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
		double xOffset = xMin + (xMax - xMin) / 2;
		double yOffset = yMin + (yMax - yMin) / 2;

		topLeftTree = new QuadTree(NODE_CAPACITY, level + 1, 
        new BoundingBox(xMin, yOffset, xOffset, yMax));
    topRightTree = new QuadTree(NODE_CAPACITY, level + 1, 
        new BoundingBox(xOffset, yOffset, xMax, yMax));
    botLeftTree = new QuadTree(NODE_CAPACITY, level + 1, 
        new BoundingBox(xMin, yMin, xOffset, yOffset));
    botRightTree = new QuadTree(NODE_CAPACITY, level + 1, 
        new BoundingBox(xOffset, yMin, xMax, yOffset));

    children.add(topRightTree);
    children.add(topLeftTree);
    children.add(botLeftTree);
    children.add(botRightTree);
	}

  public Location nearestNeighbor(Location loc) {
    Location firstInList = locations.get(0);
    return nearestNeighbor(loc, firstInList);
  }

  public Location nearestNeighbor(Location loc, Location closest) {

    double closestDistance = euclidianDistance(loc, closest);

    if(!bounds.intersectsCircle(loc, closestDistance)) {
      return closest;
    }

    // Check distance between location and points in this box
    for(Location location: locations) {
      if (euclidianDistance(loc, location) < closestDistance) {
        closest = location;
        closestDistance = euclidianDistance(loc, location);
      }
    }

    // Check distance between location and points from all children
    if(!(topLeftTree == null)) {
      for(QuadTree qt : children) {
        closest = qt.nearestNeighbor(loc, closest);
      }
    }
    return closest;
  }
	
}