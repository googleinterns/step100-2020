package com.google.sps.graph;

public interface Edge<V> {

  V getSourceVertex();

  V getDestVertex();

  double getEdgeWeight();
}
