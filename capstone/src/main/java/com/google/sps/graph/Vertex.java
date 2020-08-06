package com.google.sps.graph;

import java.util.List;

public interface Vertex<E> {

  void setDistance(double d);

  double getDistance();

  void setPrev(E e);

  E getPrev();

  List<E> getOutgoingEdges();

  @Override
  boolean equals(Object o);

  @Override
  int hashCode();

  String getId();
}
