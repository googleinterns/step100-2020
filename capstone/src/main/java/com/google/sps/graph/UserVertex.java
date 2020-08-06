package com.google.sps.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.sps.database.DatabaseRetriever;

public class UserVertex implements Vertex<UserEdge> {

  private String id;
  private double dist;
  private UserEdge prev;

  public UserVertex(String id) {
    this.id = id;
    this.dist = Double.POSITIVE_INFINITY;
    this.prev = null;
  }

  @Override
  public void setDistance(double d) {
    this.dist = d;
  }

  @Override
  public double getDistance() {
    return this.dist;
  }

  @Override
  public void setPrev(UserEdge e) {
    this.prev = e;
  }

  @Override
  public UserEdge getPrev() {
    return this.prev;
  }

  @Override
  public List<UserEdge> getOutgoingEdges() {
    List<UserEdge> edgesList = new ArrayList<UserEdge>();
    // Maps people you are in groups with the number of shared groups
    Map<UserVertex, Integer> friendsMap = DatabaseRetriever.getImmediateFriends(this.id);
    for (UserVertex v : friendsMap.keySet()) {
      // Set edge weight to reciprocal of number of shared groups
      UserEdge edge = new UserEdge(1 / friendsMap.get(v), this, v);
      edgesList.add(edge);
    }
    return edgesList;
  }

  @Override
  public boolean equals(Object v) {
    return this.getId().equals(((UserVertex) v).getId());
  }

  public String getId() {
    return this.id;
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }
}
