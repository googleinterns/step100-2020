package com.google.sps.graph;

public class UserEdge implements Edge<UserVertex> {

  private double edgeWeight;
  private UserVertex sourceVertex;
  private UserVertex destVertex;

  public UserEdge(double edgeWeight, UserVertex sourceVertex, UserVertex destVertex) {
    this.edgeWeight = edgeWeight;
    this.sourceVertex = sourceVertex;
    this.destVertex = destVertex;
  }

  @Override
  public double getEdgeWeight() {
    return this.edgeWeight;
  }

  @Override
  public UserVertex getSourceVertex() {
    return this.sourceVertex;
  }

  @Override
  public UserVertex getDestVertex() {
    return this.destVertex;
  }
}
