package lib.graph;

import lib.exceptions.InvalidOperationException;

public class Graph {
    private int[] data;
    private int nb;

    public Graph(int n) {
        nb = n;
        data = new int[n * n];
    }

    public int nbVertices() {
        return nb;
    }

    public int getEdgeCount(int i1, int i2) {
        return data[i1 * nb + i2];
    }

    public boolean hasEdge(int i1, int i2) {
        return getEdgeCount(i1, i2) != 0;
    }

    public void addEdges(int i1, int i2, int n) {
        data[i1 * nb + i2] += n;
    }

    public void addEdge(int i1, int i2) {
        addEdges(i1, i2, 1);
    }

    public void removeEdges(int i1, int i2, int n) throws InvalidOperationException {
        if(getEdgeCount(i1, i2) < n) {
            throw new InvalidOperationException();
        }
        data[i1 * nb + i2] -= n;
    }

    public void removeEdge(int i1, int i2) throws InvalidOperationException {
        removeEdges(i1, i2, 1);
    }
}
