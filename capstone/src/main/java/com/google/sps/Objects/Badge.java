package com.google.sps.Objects;

import java.awt.Image;

public final class Badge {

  String challengeName;
  Image icon;
  long timestamp;

  public Badge(String challengeName, Image icon, long timestamp) {
    this.challengeName = challengeName;
    this.icon = icon;
    this.timestamp = timestamp;
  }

  public String getChallengeName() {
    return this.challengeName;
  }

  public Image getIcon() {
    return this.icon;
  }

  public long getTimestamp() {
    return this.timestamp;
  }
}