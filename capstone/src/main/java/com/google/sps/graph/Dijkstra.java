package com.google.sps.graph;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra<V extends Vertex<E>, E extends Edge<V>> {

  public Vertex<E> runDijkstra(V src, V dest) {
    System.out.println("running dijkstra");
    // Used to keep track of vertices closest to the source vertex
    PriorityQueue<V> minHeap = new PriorityQueue<V>(new VertexComparator<E>());
    src.setDistance(0);
    minHeap.add(src);
    Set<V> visitedSet = new HashSet<V>();
    while (!minHeap.isEmpty()) {
      // Pop off vertex with smallest distance from src
      V current = minHeap.peek();
      visitedSet.add(minHeap.poll());
      System.out.println("visited set " + visitedSet);
      System.out.println("current " + current.getId() + " " + current + " " + "dest " + dest.getId());
      if (current.equals(dest)) {
        System.out.println("current equals dest----------------------");
        return current;
      }
      List<E> outgoingEdges = current.getOutgoingEdges();
      System.out.println("outgoing edges dijkstra " + outgoingEdges);
      if (outgoingEdges == null) {
        return null;
      }
      for (E e : outgoingEdges) {
        // Get the vertex on the other side of the edge
        V adjacent = e.getDestVertex();
        System.out.println("adjacent " + adjacent.getId() + " " + adjacent);
        // Update the shortest path to adjacent vertex
        double newDistanceToAdjacent = current.getDistance() + e.getEdgeWeight();
        System.out.println("new calculated distance " + newDistanceToAdjacent + "old distance" + adjacent.getDistance());
        if (newDistanceToAdjacent < adjacent.getDistance()
            || (!minHeap.contains(adjacent) && !visitedSet.contains(adjacent))) {
          System.out.println("resetting distance");
          adjacent.setDistance(newDistanceToAdjacent);
          // remember path to this vertex
          adjacent.setPrev(e);
          // Trigger reheapify since vertex has been mutated
          if (minHeap.contains(adjacent)) {
            System.out.println("reheapify");
            minHeap.add(minHeap.remove());
          } else {
            System.out.println("adding adjacentn to minheap");
            minHeap.add(adjacent);
          }
        }
      }
    }
    System.out.println("returning dest ---------------------" + dest.getDistance());
    return dest;
  }
}
