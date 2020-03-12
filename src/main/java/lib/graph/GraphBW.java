/*
Copyright 2020 Antoine PETITJEAN, Olivier ZEYEN

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package lib.graph;

import lib.exceptions.InvalidOperationException;

import java.util.ArrayList;

public class GraphBW implements IGraph {
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

    public void addEdges(int i1, int i2, int n) throws InvalidOperationException {
        int k = getEdgeCount(i1, i2) + n;
        setEdgeCount(i1, i2, k);
    }

    public void removeEdges(int i1, int i2, int n) throws InvalidOperationException {
        int k = getEdgeCount(i1, i2) - n;
        if(k < 0) {
            throw new InvalidOperationException();
        }
        setEdgeCount(i1, i2, k);
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

    public int split(int i1, int... split) throws InvalidOperationException {
        int[] entries = getEntries(i1);
        //int[] exits = getExits(i);
        int i2 = addNode();

        if(sum(entries) <= 1) {
            addEdges(i1, i2, 1);
            for(int j = 0; j < nbVertices(); j++) {
                int tmp = getEdgeCount(i1, j);

                int s = 0;
                if(split.length > j) {
                    s = split[j];
                }

                if(s < 0) {
                    throw new InvalidOperationException();
                }

                int k1 = Math.max(0, getEdgeCount(i1, j) - s);
                int k2 = Math.min(tmp, s);

                setEdgeCount(i1, j, k1);
                setEdgeCount(i2, j, k2);
            }
        }
        else {
            addEdges(i2, i1, 1);
            for(int j = 0; j < nbVertices(); j++) {
                int tmp = getEdgeCount(j, i1);

                int s = 0;
                if(split.length > j) {
                    s = split[j];
                }

                if(s < 0) {
                    throw new InvalidOperationException();
                }

                int k1 = Math.max(0, getEdgeCount(j, i1) - s);
                int k2 = Math.min(tmp, s);

                setEdgeCount(j, i1, k1);
                setEdgeCount(j, i2, k2);
            }
        }
        return i2;
    }

    public int addNodeOnEdge(int i1, int i2) throws InvalidOperationException {
        int nb = getEdgeCount(i1, i2);
        if(nb < 1) {
            throw new InvalidOperationException();
        }

        int i3 = addNode();

        setEdgeCount(i1, i2, nb - 1);

        addEdges(i1, i3, 1);
        addEdges(i3, i2, 1);

        return i3;
    }

    public void removeNodeOnEdge(int i) throws InvalidOperationException {
        int[] entries = getEntries(i);
        int[] exits = getExits(i);

        if(sum(entries) != 1 || sum(exits) != 1) {
            throw new InvalidOperationException();
        }

        int i1 = -1;
        int i2 = -1;

        for(int j = 0; j < nbVertices(); j++) {
            if(entries[j] != 0) {
                i1 = j;
            }
            if(exits[j] != 0) {
                i2 = j;
            }
        }

        addEdges(i1, i2, 1);
        removeNode(i);
    }
}
