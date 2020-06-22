package com.google.sps.Objects;

import java.awt.Image;

public final class Badge {

  String challengeName;
  Image icon;

  public Badge(String challengeName, Image icon) {
    this.challengeName = challengeName;
    this.icon = icon;
  }

  public String getChallengeName() {
    return this.challengeName;
  }

  public Image getIcon() {
    return this.icon;
  }
}