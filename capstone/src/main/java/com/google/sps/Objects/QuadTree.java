import java.util.Stack;

import com.google.sps.Objects.Location;
import com.google.sps.Objects.Coordinate;

public class QuadTree {
  Coordinate bottomLeft; 
  Coordinate topRight; 
  
  Location node; 

  QuadTree topLeftTree; 
  QuadTree topRightTree; 
  QuadTree botLeftTree; 
  QuadTree botRightTree; 

	public QuadTree() {
    bottomLeft = new Coordinate(0, 0); 
    topRight = new Coordinate(0, 0); 
    node = null; 
    topLeftTree  = null; 
    topRightTree = null; 
    botLeftTree  = null; 
    botRightTree = null; 
  }

  public static double euclidianDistance(Location loc1, Location loc2) {
    return Math.sqrt(Math.pow(loc1.getLatitude() - loc2.getLatitude(), 2) + Math.pow(loc1.getLongitude() - loc2.getLongitude(), 2));
  }




}

