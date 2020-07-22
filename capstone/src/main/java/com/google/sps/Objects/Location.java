package com.google.sps.Objects;

import com.google.appengine.api.datastore.Entity;

public final class Location {

  private final String locationName;
  private final String address;
  private final double latitude;
  private final double longitude;

  public Location(
    String locationName, 
    String address, 
    double latitude, 
    double longitude
  ) {
    this.locationName = locationName;
    this.address = address;
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public static Location fromEntity(Entity entity) {
    String locationName = (String) entity.getProperty("locationName");
    String address = (String) entity.getProperty("address");
    double latitude = (double) entity.getProperty("latitude");  
    double longitude = (double) entity.getProperty("longitude");
    return new Location(locationName, address, latitude, longitude);
  }

  public Entity toEntity() {
    Entity entity = new Entity("Location");
    entity.setProperty("locationName", this.locationName);
    entity.setProperty("address", this.address);
    entity.setProperty("latitude", this.latitude);
    entity.setProperty("longitude", this.longitude);
    return entity;
  }

  @Override 
  public boolean equals(Object other) {
    if (other == null) return false;
    if (other == this) return true;
    if (!(other instanceof Location)) return false;
    Location location = (Location) other;
    return latitude == location.latitude &&
      longitude == location.longitude &&
      locationName.equals(location.locationName) &&
      address.equals(location.address);
  }

  public String getLocationName() {
    return locationName;
  }

  public String getAddress() {
    return address;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }
  
}