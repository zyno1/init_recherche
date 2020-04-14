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
import lib.math.Calcul;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Queue;

public class GraphBW implements IGraph {
    ArrayList<Integer> data;
    ArrayList<Color> colors;
    int nb;

    public GraphBW(int n) {
        nb = n;
        data = new ArrayList<Integer>(n * n);
        colors = new ArrayList<>(n);

        for(int i = 0; i < n * n; i++) {
            data.add(0);
        }

        for(int i = 0; i < n; i++) {
            colors.add(Color.White);
        }
    }

    public static GraphBW fromGraph(Graph g) {
        return fromGraphUnsafe(g.clone());
    }

    public static GraphBW fromGraphUnsafe(Graph g) {
        int n = g.nbVertices();

        ArrayList<Color> c = new ArrayList<>(2 * n);

        for(int i = 0; i < n; i++) {
            //if(sum(g.getEntries(i)) > 1 && sum(g.getExits(i)) > 1) {
                g.flowEquivalence(i);
                c.add(Color.White);
            //}
        }

        for (int i = n; i < 2 * n; i++) {
            c.add(Color.Black);
        }

        GraphBW res = new GraphBW(0);
        res.data = g.data;
        res.colors = c;
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
        //int[] i1entry = getEntries(i1);
        int[] i1exit = getExits(i1);
        int[] i2entry = getEntries(i2);
        //int[] i2exit = getExits(i2);

        i1exit[i2] = n;
        i2entry[i1] = n;

        if(getColor(i1) == Color.White && Calcul.sum(i1exit) > 1) {
            throw new InvalidOperationException();
        }
        if(getColor(i2) == Color.Black && Calcul.sum(i2entry) > 1) {
            throw new InvalidOperationException();
        }

        data.set(i1 * nb + i2, n);
    }

    public Color getColor(int i) {
        return colors.get(i);
    }

    public void setColor(int i, Color c) throws InvalidOperationException {
        if(c == Color.Black && Calcul.sum(getEntries(i)) != 1) {
            throw new InvalidOperationException();
        }
        else if(c == Color.White && Calcul.sum(getExits(i)) != 1) {
            throw new InvalidOperationException();
        }
        colors.set(i, c);
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

    public int addNode(Color c) {
        data.ensureCapacity((nb + 1) * (nb + 1));
        for(int i = nb; i <= data.size(); i += nb + 1) {
            data.add(i, 0);
        }
        nb++;
        for(int i = 0; i < nb; i++) {
            data.add(0);
        }
        colors.add(c);
        return nb - 1;
    }

    public void removeNode(int i) {
        for(int j = 0; j < nb; j++) {
            data.remove(i * nb);
        }
        for(int j = i; j < data.size(); j += nb - 1) {
            data.remove(j);
        }
        colors.remove(i);
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
        int i2 = addNode(getColor(i1));

        if(getColor(i1) == Color.Black) {
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

    public void merge(int i1, int i2) throws InvalidOperationException {
        if(i1 > i2) {
            int tmp = i1;
            i1 = i2;
            i2 = tmp;
        }

        Color c1 = getColor(i1);
        Color c2 = getColor(i2);

        if(c1 != c2) {
            throw new InvalidOperationException();
        }

        if (getEdgeCount(i1, i2) == 0 && getEdgeCount(i2, i1) == 0) {
            throw new InvalidOperationException();
        }

        if(getEdgeCount(i1, i2) == 1) {
            setEdgeCount(i1, i2, 0);
        }
        else if(getEdgeCount(i2, i1) == 1) {
            setEdgeCount(i2, i1, 0);
        }

        if(c1 == Color.Black) {
            for(int i = 0; i < nbVertices(); i++) {
                int n = getEdgeCount(i2, i);
                setEdgeCount(i2, i, 0);
                addEdges(i1, i, n);

                n = getEdgeCount(i, i2);
                setEdgeCount(i, i2, 0);
                addEdges(i, i1, n);
            }
        }
        else {
            for(int i = 0; i < nbVertices(); i++) {
                int n = getEdgeCount(i, i2);
                setEdgeCount(i, i2, 0);
                addEdges(i, i1, n);

                n = getEdgeCount(i2, i);
                setEdgeCount(i2, i, 0);
                addEdges(i1, i, n);
            }
        }

        removeNode(i2);
    }

    public int addNodeOnEdge(int i1, int i2, Color c) throws InvalidOperationException {
        int nb = getEdgeCount(i1, i2);
        if(nb < 1) {
            throw new InvalidOperationException();
        }

        int i3 = addNode(c);

        setEdgeCount(i1, i2, nb - 1);

        addEdges(i1, i3, 1);
        addEdges(i3, i2, 1);

        return i3;
    }

    public void removeNodeOnEdge(int i) throws InvalidOperationException {
        int[] entries = getEntries(i);
        int[] exits = getExits(i);

        if(Calcul.sum(entries) != 1 || Calcul.sum(exits) != 1) {
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

        removeEdges(i1, i, 1);
        removeEdges(i, i2, 1);

        addEdges(i1, i2, 1);
        removeNode(i);
    }

    public int[][] r3(final int i1, final int i2) throws InvalidOperationException {
        int[] i1exit = getExits(i1);
        int[] i2entries = getEntries(i2);

        if(getColor(i1) != Color.White || getColor(i2) != Color.Black || getEdgeCount(i1, i2) != 1) {
            throw new InvalidOperationException();
        }

        int first = nbVertices();
        int entry = 0;
        int exit = 0;

        int[][] res = new int[2][];

        for(int j = 0; j < nbVertices(); j++) {
            int n = getEdgeCount(j, i1);
            while (n > 0) {
                int t = addNode(Color.Black);
                addEdges(j, t, 1);
                entry++;
                n--;
            }
            setEdgeCount(j, i1, 0);
        }

        for(int j = 0; j < nbVertices(); j++) {
            int n = getEdgeCount(i2, j);
            while (n > 0) {
                int t = addNode(Color.White);
                addEdges(t, j, 1);
                exit++;
                n--;
            }
            setEdgeCount(i2, j, 0);
        }

        res[0] = new int[entry];
        res[1] = new int[exit];

        for(int i = 0; i < entry; i++) {
            for(int j = 0; j < exit; j++) {
                addEdges(i + first, j + first + entry, 1);
            }
        }

        if(i1 > i2) {
            removeNode(i1);
            removeNode(i2);
        }
        else {
            removeNode(i2);
            removeNode(i1);
        }

        for (int i = 0; i < entry; i++) {
            res[0][i] = i + first - 2;
        }
        for(int i = 0; i < exit; i++) {
            res[1][i] = i + first + entry - 2;
        }

        return res;
    }

    private boolean contains(int i, int... l) {
        for(int j = 0; j < l.length; j++) {
            if(l[j] == i) {
                return true;
            }
        }
        return false;
    }

    public void r3(int[] entry, int[] exit) throws InvalidOperationException {
        for(int ei = 0; ei < nbVertices(); ei++) {
            for(int xi = 0; xi < nbVertices(); xi++) {
                int n = getEdgeCount(ei, xi);

                boolean cei = contains(ei, entry);
                boolean cxi = contains(xi, exit);

                if( (cxi && cei) && n != 1) {
                    throw new InvalidOperationException();
                }
                if( getEdgeCount(ei, xi) != 0 && ((cxi && !cei) || (!cxi && cei))) {
                    throw new InvalidOperationException();
                }
            }
        }

        for(int ei : entry) {
            if(getColor(ei) != Color.Black) {
                throw new InvalidOperationException();
            }
        }
        for(int xi : exit) {
            if(getColor(xi) != Color.White) {
                throw new InvalidOperationException();
            }
        }

        int i1 = addNode(Color.White);
        int i2 = addNode(Color.Black);
        setEdgeCount(i1, i2, 1);

        for(int ei : entry) {
            for(int j = 0; j < nbVertices(); j++) {
                int n = getEdgeCount(j, ei);
                setEdgeCount(j, ei, 0);
                addEdges(j, i1, n);
            }
        }

        for(int xi : exit) {
            for(int j = 0; j < nbVertices(); j++) {
                int n = getEdgeCount(xi, j);
                setEdgeCount(xi, j, 0);
                addEdges(i2, j, n);
            }
        }

        Arrays.sort(entry);
        Arrays.sort(exit);

        int i = entry.length - 1;
        int j = exit.length - 1;

        while (i >= 0 || j >= 0) {
            if(i >= 0) {
                if(j >= 0) {
                    if(entry[i] > exit[j]) {
                        removeNode(entry[i--]);
                    }
                    else {
                        removeNode(exit[j--]);
                    }
                }
                else {
                    removeNode(entry[i--]);
                }
            }
            else {
                removeNode(exit[j--]);
            }
        }
    }

    public void addEntries(int i1, int i2) throws InvalidOperationException {
        if(getColor(i1) == Color.Black) {
            throw new InvalidOperationException();
        }
        removeEdges(i2, i1, 1);

        int[] e = getIndirectEntries(i2);

        for(int j = 0; j < nbVertices(); j++) {
            addEdges(j, i1, e[j]);
        }
    }

    public int getBrother(int i) {
        if(getColor(i) == Color.Black) {
            for (int j = 0; j < nbVertices(); j++) {
                if(getEdgeCount(j, i) == 1) {
                    return j;
                }
            }
        }
        else {
            for (int j = 0; j < nbVertices(); j++) {
                if(getEdgeCount(i, j) == 1) {
                    return j;
                }
            }
        }
        return -1;
    }

    private void correction(int removed, int[] list) {
        for(int i = 0; i < list.length; i++) {
            if(removed < list[i]) {
                list[i]--;
            }
        }
    }

    public void addExits(int i1, int i2) throws InvalidOperationException {
        if(getColor(i2) == Color.Black || getColor(i1) == Color.White || getEdgeCount(i1, i2) < 1) {
            throw new InvalidOperationException();
        }

        int[] i2entries = getEntries(i2);
        i2entries[i1] -= 1;
        split(i2, i2entries);

        int i2b = getBrother(i2);
        int[][] tmp = r3(i2, i2b);

        if(i2b < i1) {
            i1--;
        }
        if(i2 < i1) {
            i1--;
        }

        for (int i = tmp[1].length - 1; i >= 0; i--) {
            int b = getBrother(tmp[1][i]);
            if(getColor(b) != Color.Black) {
                System.out.println("merge " + b + ", " + tmp[1][i]);
                merge(b, tmp[1][i]);
            }
        }

        System.out.println("---");

        for(int i = tmp[0].length - 1; i >= 0; i--) {
            int b = getBrother(tmp[0][i]);
            if(getColor(b) != Color.White && b == i1) {
                System.out.println("merge " + b + ", " + tmp[0][i]);
                merge(b, tmp[0][i]);
            }
        }
    }

    private int[] zeros() {
        return new int[nbVertices()];
    }

    private int[] getIndirectEntries(int i) {
        int[] entries = zeros();

        Queue<Integer> q = new ArrayDeque(nbVertices());

        for(int j = 0; j < nbVertices(); j++) {
            int n = getEdgeCount(j, i);

            if(n != 0 && getColor(j) == Color.White) {
                q.add(j); //note: if j is black then n = 1
            }
            else {
                entries[j] += n;
            }
        }

        while (!q.isEmpty()) {
            int j = q.remove();

            for(int k = 0; k < nbVertices(); k++) {
                int n = getEdgeCount(k, j);

                if(n != 0 && getColor(k) == Color.White) {
                    q.add(k);
                }
                else {
                    entries[k] += n;
                }
            }
        }

        return entries;
    }

    private int[] getInderectExits(int i) {
        int[] exits = zeros();

        Queue<Integer> q = new ArrayDeque(nbVertices());

        for(int j = 0; j < nbVertices(); j++) {
            int n = getEdgeCount(i, j);

            if(n != 0 && getColor(j) == Color.Black) {
                q.add(j); //note: if j is black then n = 1
            }
            else {
                exits[j] += n;
            }
        }

        while (!q.isEmpty()) {
            int j = q.remove();

            for(int k = 0; k < nbVertices(); k++) {
                int n = getEdgeCount(j, k);

                if(n != 0 && getColor(k) == Color.Black) {
                    q.add(k);
                }
                else {
                    exits[k] += n;
                }
            }
        }

        return exits;
    }

    public void subExits(int i1, int i2) throws InvalidOperationException {
        if(getColor(i1) == Color.White) {
            throw new InvalidOperationException();
        }

        int[] toSub = getInderectExits(i2);
        toSub[i2] -= 1;
        for(int j = 0; j < nbVertices(); j++) {
            if(getEdgeCount(i1, j) < toSub[j]) {
                throw new InvalidOperationException();
            }
        }

        for(int j = 0; j < nbVertices(); j++) {
            removeEdges(i1, j, toSub[j]);
        }
    }

    public void subEntries(int i1, int i2) throws InvalidOperationException {
        if(getColor(i1) == Color.Black) {
            throw new InvalidOperationException();
        }

        int[] toSub = getIndirectEntries(i2);
        toSub[i2] -= 1;
        for(int j = 0; j < nbVertices(); j++) {
            if(getEdgeCount(j, i1) < toSub[j]) {
                System.out.println(j + " -> " + i1);
                throw new InvalidOperationException();
            }
        }

        for(int j = 0; j < nbVertices(); j++) {
            removeEdges(j, i1, toSub[j]);
        }
    }

    public void removeSameColorNodes() throws InvalidOperationException {
        for(int i = nbVertices() - 1; i >= 0; i--) {
            for (int j = 0; j < nbVertices(); j++) {
                if(getColor(i) == getColor(j) && i != j && (getEdgeCount(i, j) != 0 || getEdgeCount(j, i) != 0)) {
                    System.out.println(i + " -> " + j);
                    merge(i, j);
                    break;
                }
            }
        }
    }
}
