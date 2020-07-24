package com.google.sps.Objects;

import com.google.appengine.api.datastore.Entity;
import com.google.sps.Objects.Coordinate;

public final class Location {

  private final String locationName;
  private final String address;
  private final Coordinate coordinate;

  public Location (
    String locationName, 
    String address, 
    Coordinate coordinate
  ) {
    this.locationName = locationName;
    this.address = address;
    this.coordinate = coordinate;
  }

  public static Location fromEntity(Entity entity) {
    String locationName = (String) entity.getProperty("locationName");
    String address = (String) entity.getProperty("address");
    double latitude = (double) entity.getProperty("latitude");  
    double longitude = (double) entity.getProperty("longitude");
    Coordinate coordinate = new Coordinate(latitude, longitude);
    return new Location(locationName, address, coordinate);
  }

  public Entity toEntity() {
    Entity entity = new Entity("Location");
    entity.setProperty("locationName", this.locationName);
    entity.setProperty("address", this.address);
    entity.setProperty("latitude", this.coordinate.getLat());
    entity.setProperty("longitude", this.coordinate.getLng());
    return entity;
  }

  @Override 
  public boolean equals(Object other) {
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof Location)) return false;
    Location location = (Location) other;
    return coordinate.getLat() == location.coordinate.getLat() &&
      coordinate.getLng() == location.coordinate.getLng() &&
      locationName.equals(location.locationName) &&
      address.equals(location.address);
  }

  public String getLocationName() {
    return this.locationName;
  }

  public String getAddress() {
    return this.address;
  }

  public double getLatitude() {
    return this.coordinate.getLat();
  }

  public double getLongitude() {
    return this.coordinate.getLng();
  }
}