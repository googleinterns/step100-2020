package com.google.sps.graph;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class Dijkstra<V extends Vertex<E>, E extends Edge<V>> {

  public Vertex<E> runDijkstra(V src, V dest) {
    // Used to keep track of vertices closest to the source vertex
    PriorityQueue<V> minHeap = new PriorityQueue<V>(new VertexComparator<E>());
    src.setDistance(0);
    minHeap.add(src);
    Set<V> visitedSet = new HashSet<V>();
    while (!minHeap.isEmpty()) {
      System.out.println("MINHEAP: " + minHeap);
      // Pop off vertex with smallest distance from src
      V current = minHeap.peek();
      visitedSet.add(minHeap.poll());
      System.out.println("CURRENT " + current + " |DEST " + dest);
      if (current.equals(dest)) {
        System.out.println("EQUALS, RETURNING ----------------");
        return current;
      }
      List<E> outgoingEdges = current.getOutgoingEdges();
      if (outgoingEdges == null) {
        return null;
      }
      for (E e : outgoingEdges) {
        // Get the vertex on the other side of the edge
        V adjacent = e.getDestVertex();
        // Update the shortest path to adjacent vertex
        double newDistanceToAdjacent = current.getDistance() + e.getEdgeWeight();
        System.out.println("ADJACENT: " + adjacent.getId() + " " + adjacent.getDistance());
        if (newDistanceToAdjacent < adjacent.getDistance()
            || (!minHeap.contains(adjacent) && !visitedSet.contains(adjacent))) {
          adjacent.setDistance(newDistanceToAdjacent);
          // remember path to this vertex
          adjacent.setPrev(e);
          // Trigger reheapify since vertex has been mutated
          if (minHeap.contains(adjacent)) {
            minHeap.add(minHeap.remove());
          } else {
            System.out.println(
                "adding " + adjacent + " " + adjacent.getId() + " " + adjacent.getDistance());
            minHeap.add(adjacent);
          }
        }
      }
    }
    return dest;
  }
}
