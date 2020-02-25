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
import java.util.List;

public class Graph {
    private List<Integer> data;
    private int nb;

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
        int old = data.get(i1 * nb + i2);
        data.set(i1 * nb + i2, old + n);
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

    public void splitOnEntries(int i, int... split) {
        int i2 = addNode();

        for(int j = 0; j < nb; j++) {
            setEdgeCount(i2, j, getEdgeCount(i, j));
        }

        for(int j = 0; j < nb; j++) {
            int count = getEdgeCount(j, i);
            int s = 0;
            if(split.length > j) {
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
    }

    public void splitOnExits(int i, int... split) {
        int i2 = addNode();

        for(int j = 0; j < nb; j++) {
            setEdgeCount(j, i2, getEdgeCount(j, i));
        }

        for(int j = 0; j < nb; j++) {
            int count = getEdgeCount(i, j);
            int s = 0;
            if(split.length > j) {
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
    }
}
