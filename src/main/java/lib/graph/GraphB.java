package lib.graph;

import lib.exceptions.InvalidOperationException;
import lib.math.Calcul;

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

    public int[] getExits(int i) {
        int[] res = new int[nb];

        for(int j = 0; j < nb; j++) {
            res[j] = getEdgeCount(i, j);
        }

        return res;
    }

    public int[] getEntries(int i) {
        int [] res = new int[nb];

        for(int j = 0; j < nb; j++) {
            res[j] = getEdgeCount(j, i);
        }

        return res;
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

    public int split_r2_exits(int i1, int... split) throws InvalidOperationException {
        for (int i = 0; i < nbVertices() && i < split.length; i++) {
            if(getEdgeCount(i1, i) < split[i]) {
                throw new InvalidOperationException();
            }
        }

        int i2 = addNode();
        addEdges(i1, i2, 1);

        for(int j = 0; j < nbVertices() && j < split.length; j++) {
            removeEdges(i1, j, split[j]);
            addEdges(i2, j, split[j]);
        }

        return i2;
    }

    public int split_r2_entries(int i1, int... split) throws InvalidOperationException {
        for (int i = 0; i < nbVertices() && i < split.length; i++) {
            if(getEdgeCount(i, i1) < split[i]) {
                throw new InvalidOperationException();
            }
        }

        int i2 = addNode();
        addEdges(i2, i1, 1);

        for(int j = 0; j < nbVertices() && j < split.length; j++) {
            removeEdges(j, i1, split[j]);
            addEdges(j, i2, split[j]);
        }

        return i2;
    }

    private void merge_r2(int i1, int i2) {
        int node = Math.min(i1, i2);

        for(int j = 0; j < nbVertices(); j++) {
            int in = getEdgeCount(j, i1) + getEdgeCount(j, i2);
            int out = getEdgeCount(i1, j) + getEdgeCount(i2, j);

            setEdgeCount(j, node, in);
            setEdgeCount(node, j, out);
        }

        removeNode(Math.max(i1, i2));
    }

    private void merge_on_entry(int i1, int i2) {
        int node = Math.min(i1, i2);

        for(int j = 0; j < nbVertices(); j++) {
            int in = getEdgeCount(j, i1);
            int out = getEdgeCount(i1, j) + getEdgeCount(i2, j);

            setEdgeCount(j, node, in);
            setEdgeCount(node, j, out);
        }

        removeNode(Math.max(i1, i2));
    }

    private void merge_on_exit(int i1, int i2) {
        int node = Math.min(i1, i2);

        for(int j = 0; j < nbVertices(); j++) {
            int in = getEdgeCount(j, i1) + getEdgeCount(j, i2);
            int out = getEdgeCount(i1, j);

            setEdgeCount(j, node, in);
            setEdgeCount(node, j, out);
        }

        removeNode(Math.max(i1, i2));
    }

    public void merge(int i1, int i2) throws InvalidOperationException {
        if(getEdgeCount(i1, i2) == 1) {
            if(Calcul.sum(getExits(i1)) == 1) {
                removeEdges(i1, i2, 1);
                merge_r2(i1, i2);
            }
            else if(Calcul.sum(getEntries(i2)) == 1) {
                removeEdges(i1, i2, 1);
                merge_r2(i1, i2);
            }
            else {
                throw new InvalidOperationException();
            }
        }
        else if(getEdgeCount(i2, i1) == 1) {
            if(Calcul.sum(getExits(i2)) == 1) {
                removeEdges(i2, i1, 1);
                merge_r2(i1, i2);
            }
            else if(Calcul.sum(getEntries(i1)) == 1) {
                removeEdges(i2, i1, 1);
                merge_r2(i1, i2);
            }
            else {
                throw new InvalidOperationException();
            }
        }
        else {
            boolean enEq = true;
            boolean exEq = true;
            for(int i = 0; i < nbVertices() && (enEq || exEq); i++) {
                enEq = enEq && getEdgeCount(i, i1) == getEdgeCount(i, i2);
                exEq = exEq && getEdgeCount(i1, i) == getEdgeCount(i2, i);
            }

            if(enEq) {
                merge_on_entry(i1, i2);
            }
            else if(exEq) {
                merge_on_exit(i1, i2);
            }
            else {
                throw new InvalidOperationException();
            }
        }
    }

    public int split_entry(int i1) throws InvalidOperationException {
        if(Calcul.sum(getEntries(i1)) != 2) {
            throw new InvalidOperationException();
        }

        int i2 = addNode();

        boolean done = false;

        for(int i = 0; i < nbVertices(); i++) {
            int out = getEdgeCount(i1, i);

            setEdgeCount(i2, i, out);

            if(!done && getEdgeCount(i, i1) > 0) {
                done = true;

                removeEdges(i, i1, 1);
                addEdges(i, i2, 1);
            }
        }

        return i2;
    }

    public int split_exit(int i1) throws InvalidOperationException {
        if(Calcul.sum(getExits(i1)) != 2) {
            throw new InvalidOperationException();
        }

        int i2 = addNode();

        boolean done = false;

        for(int i = 0; i < nbVertices(); i++) {
            int in = getEdgeCount(i, i1);

            setEdgeCount(i, i2, in);

            if(!done && getEdgeCount(i1, i) > 0) {
                done = true;

                removeEdges(i1, i, 1);
                addEdges(i2, i, 1);
            }
        }

        return i2;
    }

    private int[] zeros(int nb) {
        return new int[nb];
    }

    public void addExits(int i1, int i2) throws InvalidOperationException {
        if(getEdgeCount(i1, i2) < 1) {
            throw new InvalidOperationException();
        }

        int[] i1exit = getExits(i1);
        i1exit[i2] -= 1;
        int p1 = split_r2_exits(i1, i1exit);
        int pp1 = split_exit(i1);

        int[] i2entry = getEntries(i2);
        i2entry[pp1] -= 1;
        int p2 = split_r2_entries(i2, i2entry);
        int pp2 = split_entry(i2);

        merge(pp1, pp2);
        merge(i2, p2);
        merge(i1, pp1);
        merge(i1, p1);
    }

    public void addEntries(int i1, int i2) throws InvalidOperationException {
        if(getEdgeCount(i2, i1) < 1) {
            throw new InvalidOperationException();
        }

        int[] i2exit = getExits(i2);
        i2exit[i1] -= 1;
        int p2 = split_r2_exits(i2, i2exit);
        int pp2 = split_exit(i2);

        int[] i1entry = getEntries(i1);
        i1entry[pp2] -= 1;
        int p1 = split_r2_entries(i1, i1entry);
        int pp1 = split_entry(i1);

        merge(pp1, pp2);
        merge(p1, i1);
        merge(i1, pp2);
        merge(i2, p2);
    }
}
