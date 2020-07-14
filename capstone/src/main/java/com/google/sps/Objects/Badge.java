package com.google.sps.Objects;

import com.google.appengine.api.datastore.Entity;
import java.awt.Image;

public final class Badge {

  long badgeId;
  String challengeName;
  String iconUrl;
  long timestamp;

  public Badge(long badgeId, String challengeName, String iconUrl, long timestamp) {
    this.badgeId = badgeId;
    this.challengeName = challengeName;
    this.iconUrl = iconUrl;
    this.timestamp = timestamp;
  }

  public String getChallengeName() {
    return this.challengeName;
  }

  public String getIcon() {
    return this.iconUrl;
  }

  public long getTimestamp() {
    return this.timestamp;
  }

  /** Given a Badge entity, returns a Badge object. */
  public static Badge fromEntity(Entity entity) {
    long badgeId = (long) entity.getProperty("badgeId");
    String challengeName = (String) entity.getProperty("challengeName");
    String icon = (String) entity.getProperty("icon");
    long timestamp = (long) entity.getProperty("timestamp");
    
    return new Badge(badgeId, challengeName, icon, timestamp);
  }
}