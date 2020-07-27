package com.google.sps.Objects;

public class BoundingBox {
  private double xMin;
  private double xMax;
	private double yMin;
  private double yMax;

	public BoundingBox(double xMin, double yMin, double xMax, double yMax) {
	  this.xMin = xMin;
		this.yMin = yMin;
		this.xMax = xMax;
		this.yMax = yMax;
  }

	/* 
  * Checks if location is within boundary
  */
	public boolean containsPoint(Location loc) {
		return (loc.getLatitude() >= this.getXMin() 
      && loc.getLatitude() <= this.getXMax() 
      && loc.getLongitude() >= this.getYMin() 
      && loc.getLongitude() <= this.getYMax());
	}

  /* 
  * Checks if bounding box intersects with circle area of center location and 
  * distance to closest point 
  */
  public boolean intersectsCircle(Location center, double distance) {
    double boundaryX = (this.xMin + this.xMax)/2;
		double boundaryY = (this.yMin + this.yMax)/2;
		double boundaryWidth = this.xMax - this.yMin;
		double boundaryHeight = this.yMax - this.yMin;
		double circleDistanceX = Math.abs(center.getLatitude() - boundaryX);
		double circleDistanceY = Math.abs(center.getLongitude() - boundaryY);
		
		if(circleDistanceX > distance + boundaryWidth/2) {
			return false;
		}
		if(circleDistanceY > distance + boundaryHeight/2) {
			return false;
		}
		if(circleDistanceX <= boundaryWidth/2) {
			return true;
		}
		if(circleDistanceY <=boundaryHeight/2) {
			return true;
    }

    double cornerDistance = (circleDistanceX - boundaryWidth/2)*(circleDistanceX - boundaryWidth/2) + 
				(circleDistanceY - boundaryHeight/2)*(circleDistanceY - boundaryHeight/2);
		return (cornerDistance <= distance*distance);
  }

  double getXMin() {
		return xMin;
	}

  double getYMin() {
		return yMin;
	}

  double getXMax() {
		return xMax;
	}

  double getYMax() {
		return yMax;
	}
}