package lib.graph;

import lib.exceptions.InvalidOperationException;

import java.util.ArrayList;

public class GraphBW {
    ArrayList<Integer> data;
    int nb;

    public GraphBW(int n) {
        nb = n;
        data = new ArrayList<Integer>(n * n);

        for(int i = 0; i < n * n; i++) {
            data.add(0);
        }
    }

    public static GraphBW fromGraph(Graph g) {
        return fromGraphUnsafe(g.clone());
    }

    public static GraphBW fromGraphUnsafe(Graph g) {
        int n = g.nbVertices();

        for(int i = 0; i < n; i++) {
            if(sum(g.getEntries(i)) > 1 && sum(g.getExits(i)) > 1) {
                g.flowEquivalence(i);
            }
        }

        GraphBW res = new GraphBW(0);
        res.data = g.data;
        res.nb = g.nb;

        return res;
    }

    public int nbVertices() {
        return nb;
    }

    public int getEdgeCount(int i1, int i2) {
        return data.get(i1 * nb + i2);
    }

    public void setEdgeCount(int i1, int i2, int n) throws InvalidOperationException {
        int[] i1entry = getEntries(i1);
        int[] i1exit = getExits(i1);
        int[] i2entry = getEntries(i2);
        int[] i2exit = getExits(i2);

        i1exit[i2] = n;
        i2entry[i1] = n;

        if((sum(i1entry) > 1 && sum(i1exit) > 1) || (sum(i2entry) > 1 && sum(i2exit) > 1)) {
            throw new InvalidOperationException();
        }

        data.set(i1 * nb + i2, n);
    }

    public static int sum(int... n) {
        int res = 0;
        for(int i : n) {
            res += i;
        }
        return res;
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
}
