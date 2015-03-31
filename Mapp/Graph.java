package Graphs;

import java.util.*;

interface Graph<N> {
    void add(N ny);
    void connect (N from, N to, String n, int v);
    Edge<N> getEdgeBetween(N s1, N s2);
    boolean pathExists(N from, N to);
    List<Edge<N>> getPath(N from, N to); 
    String toString();
    
}
