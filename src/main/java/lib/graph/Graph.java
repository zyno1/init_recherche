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

public class Graph implements IGraph {
    ArrayList<Integer> data;
    int nb;

    public Graph(int n) {
        nb = n;
        data = new ArrayList<Integer>(n * n);

        for(int i = 0; i < n * n; i++) {
            data.add(0);
        }
    }

    public int nbVertices() {
        return nb;
    }

    public int getEdgeCount(int i1, int i2) {
        return data.get(i1 * nb + i2);
    }

    public void setEdgeCount(int i1, int i2, int n) {
        data.set(i1 * nb + i2, n);
    }

    public boolean hasEdge(int i1, int i2) {
        return getEdgeCount(i1, i2) != 0;
    }

    public void addEdges(int i1, int i2, int n) {
        int old = getEdgeCount(i1, i2);
        setEdgeCount(i1, i2, old + n);
    }

    public void addEdge(int i1, int i2) {
        addEdges(i1, i2, 1);
    }

    public void removeEdges(int i1, int i2, int n) throws InvalidOperationException {
        if(getEdgeCount(i1, i2) < n) {
            throw new InvalidOperationException();
        }
        addEdges(i1, i2, -1 * n);
    }

    public void removeEdge(int i1, int i2) throws InvalidOperationException {
        removeEdges(i1, i2, 1);
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

    public int flowEquivalence(int i) {
        int i2 = addNode();
        for(int j = 0; j < nb; j++) {
            int count = getEdgeCount(i, j);
            addEdges(i2, j, count);

            try {
                removeEdges(i, j, count);
            } catch (InvalidOperationException e) {
                e.printStackTrace();
            }
        }
        addEdge(i, i2);
        return i2;
    }

    public void flowEquivalence(int i1, int i2) throws InvalidOperationException {
        //verify i1 exits
        for(int j = 0; j < nb; j++) {
            int count = getEdgeCount(i1, j);

            if( ! ((j == i2 && count == 1) || count == 0)) {
                throw new InvalidOperationException();
            }
        }

        //verify i2 entries
        for(int j = 0; j < nb; j++) {
            int count = getEdgeCount(j, i2);

            if( ! ((j == i1 && count == 1) || count == 0)) {
                throw new InvalidOperationException();
            }
        }

        for(int j = 0; j < nb; j++) {
            setEdgeCount(i1, j, getEdgeCount(i2, j));
        }
        removeNode(i2);
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

    public int splitEntries(int i, int... split) {
        int i2 = addNode();

        for(int j = 0; j < nb; j++) {
            setEdgeCount(i2, j, getEdgeCount(i, j));
        }

        for(int j = 0; j < nb; j++) {
            int count = getEdgeCount(j, i);
            int s = 0;
            if(split.length > j && j != i) {
                s = split[j];
            }

            if(s > count) {
                s = count;
            }

            int c1 = count - s;
            int c2 = s;

            setEdgeCount(j, i, c1);
            setEdgeCount(j, i2, c2);
        }
        return i2;
    }

    public int splitExits(int i, int... split) {
        int i2 = addNode();

        for(int j = 0; j < nb; j++) {
            setEdgeCount(j, i2, getEdgeCount(j, i));
        }

        for(int j = 0; j < nb; j++) {
            int count = getEdgeCount(i, j);
            int s = 0;
            if(split.length > j && j != i) {
                s = split[j];
            }

            if(s > count) {
                s = count;
            }

            int c1 = count - s;
            int c2 = s;

            setEdgeCount(i, j, c1);
            setEdgeCount(i2, j, c2);
        }
        return i2;
    }

    public void mergeEntries(int i1, int i2) throws InvalidOperationException {
        if(i1 > i2) {
            int tmp = i1;
            i1 = i2;
            i2 = tmp;
        }

        for(int j = 0; j < nb; j++) {
            if(getEdgeCount(i1, j) != getEdgeCount(i2, j)) {
                throw new InvalidOperationException();
            }
        }

        for(int j = 0; j < nb; j++) {
            int c1 = getEdgeCount(j, i1);
            int c2 = getEdgeCount(j, i2);

            setEdgeCount(j, i1, c1 + c2);
        }
        removeNode(i2);
    }

    public void mergeExits(int i1, int i2) throws InvalidOperationException {
        if(i1 > i2) {
            int tmp = i1;
            i1 = i2;
            i2 = tmp;
        }

        for(int j = 0; j < nb; j++) {
            if(getEdgeCount(j, i1) != getEdgeCount(j, i2)) {
                throw new InvalidOperationException();
            }
        }

        for(int j = 0; j < nb; j++) {
            int c1 = getEdgeCount(i1, j);
            int c2 = getEdgeCount(i2, j);

            setEdgeCount(i1, j, c1 + c2);
        }
        removeNode(i2);
    }

    public Graph clone() {
        Graph g = new Graph(nbVertices());

        for(int j = 0; j < nbVertices(); j++) {
            for(int i = 0; i < nbVertices(); i++) {
                g.setEdgeCount(j, i, getEdgeCount(j, i));
            }
        }

        return g;
    }

    public void addExits(int i1, int i2) throws InvalidOperationException {
        int[] i1exit = getExits(i1);

        if(i1exit[i2] < 1) {
            throw new InvalidOperationException();
        }

        for(int j = 0; j < nbVertices(); j++) {
            int k = getEdgeCount(i1, j) + getEdgeCount(i2, j);
            if(j == i2) {
                k--;
            }
            setEdgeCount(i1, j, k);
        }
    }

    public void addEntries(int i1, int i2) throws InvalidOperationException {
        int[] i1entries = getEntries(i1);

        if(i1entries[i2] < 1) {
            throw new InvalidOperationException();
        }

        for(int j = 0; j < nbVertices(); j++) {
            int k = getEdgeCount(j, i1) + getEdgeCount(j, i2);
            if(j == i2) {
                k--;
            }
            setEdgeCount(j, i1, k);
        }
    }

    public void subExits(int i1, int i2) throws InvalidOperationException {
        int[] i1exits = getExits(i1);
        int[] i2exits = getExits(i2);

        i1exits[i2] += 1;

        for (int j = 0; j < nbVertices(); j++) {
            if(i1exits[j] < i2exits[j]) {
                throw new InvalidOperationException();
            }
        }

        for(int j = 0; j < nbVertices(); j++) {
            i1exits[j] -= i2exits[j];

            setEdgeCount(i1, j, i1exits[j]);
        }
    }

    public void subEntries(int i1, int i2) throws InvalidOperationException {
        int[] i1entries = getEntries(i1);
        int[] i2entries = getEntries(i2);

        i1entries[i2] += 1;

        for (int j = 0; j < nbVertices(); j++) {
            if(i1entries[j] < i2entries[j]) {
                throw new InvalidOperationException();
            }
        }

        for(int j = 0; j < nbVertices(); j++) {
            i1entries[j] -= i2entries[j];

            setEdgeCount(j, i1, i1entries[j]);
        }
    }

    public void removeLooplessNodes() throws InvalidOperationException {
        for(int j = nbVertices() - 1; j >= 0; j--) {
            if(getEdgeCount(j, j) == 0) {
                int se = GraphBW.sum(getEntries(j));
                int sx = GraphBW.sum(getExits(j));

                for(int i = 0; i < nbVertices(); i++) {
                    if(se > sx) {
                        while (getEdgeCount(j, i) > 0) {
                            addEntries(i, j);
                        }
                    }
                    else {
                        while (getEdgeCount(i, j) > 0) {
                            addExits(i, j);
                        }
                    }
                }

                removeNode(j);
            }
        }
    }

    public GraphBW toGraphBW() throws InvalidOperationException {
        GraphBW g = new GraphBW(nb*2);
        for(int i=0; i<nb; i++){
            g.addEdges(i*2, i*2+1,1);
            for (int j=0; j<nb; j++){
                g.addEdges(i*2+1, j*2, data.get(i*nb+j));
            }
        }
        return g;
    }
}
