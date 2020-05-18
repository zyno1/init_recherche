package lib.graph;

import lib.exceptions.InvalidOperationException;

import java.util.ArrayList;

public class GraphB implements IGraph {
    ArrayList<Integer> data;
    int nb;

    public GraphB(int n) {
        nb = n;
        data = new ArrayList<>(n * n);

        for(int i = 0; i < n * n; i++) {
            data.add(0);
        }
    }

    @Override
    public int nbVertices() {
        return nb;
    }

    @Override
    public int getEdgeCount(int i1, int i2) {
        return data.get(i1 * nb + i2);
    }

    public void setEdgeCount(int i1, int i2, int n) {
        data.set(i1 * nb + i2, n);
    }

    public void addEdges(int i1, int i2, int nb) {
        int old = getEdgeCount(i1, i2);
        setEdgeCount(i1, i2, old + nb);
    }

    public void removeEdges(int i1, int i2, int n) throws InvalidOperationException {
        if(getEdgeCount(i1, i2) < n) {
            throw new InvalidOperationException();
        }
        addEdges(i1, i2, -1 * n);
    }

    public int addNode() {
        data.ensureCapacity((nb + 1) * (nb + 1));
        for(int i = nb; i <= data.size(); i += nb + 1) {
            data.add(i, 0);
        }
        nb++;
        for(int i = 0; i < nb; i++) {
            data.add(0);
        }
        return nb - 1;
    }

    public void removeNode(int i) {
        for(int j = 0; j < nb; j++) {
            data.remove(i * nb);
        }
        for(int j = i; j < data.size(); j += nb - 1) {
            data.remove(j);
        }
        nb--;
    }
}
