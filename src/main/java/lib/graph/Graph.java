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
}
