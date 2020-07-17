package com.google.sps.Objects.response;

public class JoinGroupResponse {

  private String groupName;
  private boolean isMember;

  public JoinGroupResponse(String groupName, boolean isMember) {
    this.groupName = groupName;
    this.isMember = isMember;
  }
}
