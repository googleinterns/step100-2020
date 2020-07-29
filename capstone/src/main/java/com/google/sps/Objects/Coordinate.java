package com.google.sps.Objects;

public final class Coordinate {
  private final double latitude;
  private final double longitude;

  public Coordinate(double lat, double lon) {
    latitude = lat;
    longitude = lon;
  }

  public double getLat() {
    return this.latitude;
  }

  public double getLng() {
    return this.longitude;
  }
}