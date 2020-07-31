package com.google.sps.graph;

import java.util.Comparator;

public class VertexComparator<E> implements Comparator<Vertex<E>> {

  @Override
  public int compare(Vertex<E> v1, Vertex<E> v2) {
    if (v1.getDistance() < v2.getDistance()) {
      return -1;
    } else if (v1.getDistance() > v2.getDistance()) {
      return 1;
    }
    return 0;
  }
}
