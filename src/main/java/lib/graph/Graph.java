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
import lib.graph.io.GraphIO;
import lib.math.Calcul;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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

    private int getEdgeCountWithoutIdentity(int i1, int i2) {
        int res = data.get(i1 * nb + i2);
        if(i1==i2)res--;
        return res;
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

    private int[] getExitsWithoutIdentity(int i) {
        int[] res = new int[nb];

        for(int j = 0; j < nb; j++) {
            res[j] = getEdgeCount(i, j);
        }
        res[i]--;

        return res;
    }

    public int[] getEntries(int i) {
        int [] res = new int[nb];

        for(int j = 0; j < nb; j++) {
            res[j] = getEdgeCount(j, i);
        }

        return res;
    }

    private int[] getEntriesWithoutIdentity(int i) {
        int [] res = new int[nb];

        for(int j = 0; j < nb; j++) {
            res[j] = getEdgeCount(j, i);
        }

        res[i]--;

        return res;
    }

    public int splitEntries(int i, int... split) {
        int i2 = addNode();

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

            setEdgeCount(i2, j, getEdgeCount(i, j));
        }
        return i2;
    }

    public int splitExits(int i, int... split) {
        int i2 = addNode();

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

            setEdgeCount(j, i2, getEdgeCount(j, i));
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

    private int[] zeros() {
        return zeros(nbVertices());
    }

    private int[] zeros(int nb) {
        return new int[nb];
    }

    public void addExits(int i1, int i2) throws InvalidOperationException {
        int[] i1exit = getExits(i1);

        if(i1exit[i2] < 1) {
            throw new InvalidOperationException();
        }

        int[] tmp = zeros(nbVertices() + 1);

        tmp[i2] = 1;
        int p1 = splitExits(i1, tmp);
        tmp[i2] = 0;

        tmp[p1] = 1;
        int p2 = splitEntries(i2, tmp);

        flowEquivalence(p1, p2);

        mergeExits(i1, p1);
    }

    public void addEntries(int i1, int i2) throws InvalidOperationException {
        int[] i1entries = getEntries(i1);

        if(i1entries[i2] < 1) {
            throw new InvalidOperationException();
        }

        int[] tmp = zeros(nbVertices() + 1);

        tmp[i1] = 1;
        int p2 = splitExits(i2, tmp);
        tmp[i1] = 0;

        tmp[p2] = 1;
        int p1 = splitEntries(i1, tmp);

        flowEquivalence(p2, p1);

        mergeEntries(i1, p2);
    }

    public void subExits(int i1, int i2) throws InvalidOperationException {
        int[] i1exits = getExits(i1);
        int[] i2exits = getExits(i2);

        for (int j = 0; j < nbVertices(); j++) {
            if(i1exits[j] < i2exits[j]) {
                throw new InvalidOperationException();
            }
        }

        int p1 = splitExits(i1, getExits(i2));
        int p2 = flowEquivalence(p1);

        mergeEntries(i2, p2);
        mergeExits(i1, p1);
    }

    public void subEntries(int i1, int i2) throws InvalidOperationException {
        int[] i1entries = getEntries(i1);
        int[] i2entries = getEntries(i2);

        for (int j = 0; j < nbVertices(); j++) {
            if(i1entries[j] < i2entries[j]) {
                throw new InvalidOperationException();
            }
        }

        int p1 = splitEntries(i1, getEntries(i2));
        int p2 = flowEquivalence(p1);

        mergeEntries(i1, p2);
        mergeExits(i2, p1);
    }

    public void removeLooplessNodes() throws InvalidOperationException {
        for(int j = nbVertices() - 1; j >= 0; j--) {
            if(getEdgeCount(j, j) == 0) {
                int se = Calcul.sum(getEntries(j));
                int sx = Calcul.sum(getExits(j));

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

    private boolean testSub(int i1, int i2) {
        for (int j = 0; j < nbVertices(); j++) {
            if(getEdgeCount(j, i1) < getEdgeCount(j, i2)) {
                return false;
            }
        }
        return true;
    }

    public void reduceAll() throws InvalidOperationException{
        for(int i=nbVertices()-1; i>=0; i--) {
            reduceLine(i);
        }
    }

    public void reduceLine(int line) throws InvalidOperationException {
        boolean continu = true;
        while(continu) {
            int[] exits = getExitsWithoutIdentity(line);
            int min = -1;
            int max = -1;
            for (int i = 0; i < nbVertices(); i++) {
                if (exits[i] != 0) {
                    if (min == -1 || exits[i] < exits[min]) {
                        min = i;
                    }
                }
            }
            for (int i = 0; i < nbVertices(); i++) {
                if (exits[i] != 0 && i != min) {
                    if (max == -1 || exits[i] > exits[max]) {
                        max = i;
                    }
                }
            }
            if(max!=-1) {
                /*System.out.println(exits[min]+ " "+ exits[max]);
                if(exits[min]<1){
                    GraphIO.printGraph(this);
                }*/
                int old = exits[max];
                reduce(line, min, max);

                if(getEdgeCountWithoutIdentity(line, max)==old){
                    continu=false;
                }

                int oldsize = nbVertices();
                removeLooplessNodes();
                if(oldsize!=nbVertices()){
                    continu=false;
                }
            }
            else{
                continu = false;
            }
        }
        /*if(min!=-1) {
            for (int i = nbVertices() - 1; i >= 0; i--) {
                if (exits[i] != 0 && i != min && getEdgeCountWithoutIdentity(line, min)>0) {
                    System.out.println(line+" "+min+" "+i);
                    reduce(line, min, i);
                }
            }
        }*/
    }


    public int reduce(int line, int min, int dst) throws InvalidOperationException {
        int[] minEntries = getEntries(min);
        int[] dstEntries = getEntries(dst);

        int nb = dstEntries[line] / minEntries[line];

        for(int i = 0; i < nbVertices() && nb > 0; i++) {
            if(minEntries[i] * nb > dstEntries[i]) {
                double k = Math.ceil((double)(nb * minEntries[i] - dstEntries[i]) / (dstEntries[line] - minEntries[line] * nb));

                while (k >= Double.MAX_VALUE || (getEdgeCount(i, line) < k && getEdgeCount(line, line) == 0)) {
                    nb--;

                    if(nb == 0) {
                        break;
                    }

                    k = Math.ceil((double)(nb * minEntries[i] - dstEntries[i]) / (dstEntries[line] - minEntries[line] * nb));
                }

                if(getEdgeCount(i, line) < 1) {
                    nb = 0;
                }
            }
        }

        if(nb != 0) {
            for (int i = 0; i < nbVertices(); i++) {
                if(minEntries[i] * nb > dstEntries[i]) {
                    double k = Math.ceil((double)(nb * minEntries[i] - dstEntries[i]) / (dstEntries[line] - minEntries[line] * nb));

                    while (k > 0) {
                        addExits(i, line);
                        k--;
                    }
                }
            }

            for(int i = 0; i < nb && testSub(dst, min); i++) {
                subEntries(dst, min);
            }
        }

        return nb;
    }


    public void putZeroOnLines() throws InvalidOperationException {
        for (int j = nbVertices() - 1; j >= 0; j--) {
            int out[] = getExitsWithoutIdentity(j);
            int notzero = 0;
            int min = -1;
            for (int i = 0; i < nbVertices(); i++) {
                if (out[i] != 0) {
                    notzero++;
                    if (min == -1 || out[min] > out[i]) {
                        min = i;
                    }
                }
            }
            while (notzero > 1) {
                for (int i = nbVertices() - 1; i >= 0; i--) {
                    int a = getEdgeCountWithoutIdentity(j, i);
                    if (i != min && a != 0) {
                        int b = getEdgeCountWithoutIdentity(j, min);
                        while (a>b){
                            for (int l = nbVertices() - 1; l >= 0; l--) {
                                if (l != j) {

                                    int c = getEdgeCountWithoutIdentity(l, i);
                                    int d = getEdgeCountWithoutIdentity(l, min);

                                    while (c<d) {
                                        c += a;
                                        d += b;
                                        //GraphIO.printGraph(this);
                                        addExits(l, j);
                                    }
                                }
                            }
                            subEntries(i,min);
                            a = getEdgeCountWithoutIdentity(j, i);
                            b = getEdgeCountWithoutIdentity(j, min);
                        }

                        if(a==b) {
                            boolean swip = true;
                            int cbis=0;
                            int dbis=0;
                            int bis = -1;
                            for (int l = nbVertices() - 1; l >= 0; l--) {
                                if (l != j) {
                                    int c = getEdgeCountWithoutIdentity(l, i);
                                    int d = getEdgeCountWithoutIdentity(l, min);

                                    if (c>d) {
                                        swip=false;
                                        cbis= c;
                                        dbis= d;
                                        bis = l;
                                        break;
                                    }
                                }
                            }
                            if(swip){
                                min = i;
                            }else{
                                for (int l = nbVertices() - 1; l >= 0; l--) {
                                    if (l != j) {
                                        int c = getEdgeCountWithoutIdentity(l, i);
                                        int d = getEdgeCountWithoutIdentity(l, min);

                                        while (c<d) {
                                            addExits(l, bis);
                                            c+=cbis;
                                            d+=dbis;
                                        }
                                    }
                                }
                                subEntries(i,min);
                                a = getEdgeCountWithoutIdentity(j, i);
                                b = getEdgeCountWithoutIdentity(j, min);

                            }
                        }

                        if (a == 0) {
                            notzero--;
                        } else if(a<b){
                            min = i;
                        }
                    }
                }
            }
        }
    }

    public void putZeroOnColumns() throws InvalidOperationException {
        for (int j = nbVertices() - 1; j >= 0; j--) {
            int in[] = getEntriesWithoutIdentity(j);
            int notzero = 0;
            int min = -1;
            for (int i = 0; i < nbVertices(); i++) {
                if (in[i] != 0) {
                    notzero++;
                    if (min == -1 || in[min] > in[i]) {
                        min = i;
                    }
                }
            }
            while (notzero > 1) {
                for (int i = nbVertices() - 1; i >= 0; i--) {
                    int a = getEdgeCountWithoutIdentity(i, j);
                    if (i != min && a != 0) {
                        int b = getEdgeCountWithoutIdentity(min, j);
                        while (a>b){
                            for (int l = nbVertices() - 1; l >= 0; l--) {
                                if (l != j) {

                                    int c = getEdgeCountWithoutIdentity(i,l);
                                    int d = getEdgeCountWithoutIdentity(min, l);
                                    while (c-d<0) {
                                        c += a;
                                        d += b;
                                        addEntries(l, j);
                                    }
                                }
                            }
                            subExits(i,min);
                            a = getEdgeCountWithoutIdentity(i, j);
                            b = getEdgeCountWithoutIdentity(min, j);
                        }

                        if(a==b) {
                            boolean swip = true;
                            int cbis=0;
                            int dbis=0;
                            int bis = -1;
                            for (int l = nbVertices() - 1; l >= 0; l--) {
                                if (l != j) {
                                    int c = getEdgeCountWithoutIdentity(i, l);
                                    int d = getEdgeCountWithoutIdentity(min, l);

                                    if (c>d) {
                                        swip=false;
                                        cbis= c;
                                        dbis= d;
                                        bis = l;
                                        break;
                                    }
                                }
                            }
                            if(swip){
                                min = i;
                            }else{
                                for (int l = nbVertices() - 1; l >= 0; l--) {
                                    if (l != j) {
                                        int c = getEdgeCountWithoutIdentity(i, l);
                                        int d = getEdgeCountWithoutIdentity(min, l);

                                        while (c<d) {
                                            addEntries(l, bis);
                                            c+=cbis;
                                            d+=dbis;
                                        }
                                    }
                                }
                                subExits(i,min);
                                a = getEdgeCountWithoutIdentity(i, j);
                                b = getEdgeCountWithoutIdentity(min, j);

                            }
                        }

                        if (a == 0) {
                            notzero--;
                        } else if(a<b){
                            min = i;
                        }
                    }
                }
            }
        }
    }

    public void putZeros() throws InvalidOperationException {
        putZeroOnLines();
        System.out.println("0 sur les lignes : ");
        GraphIO.printGraph(this);
        putZeroOnColumns();
        System.out.println("0 sur les colonnes : ");
        GraphIO.printGraph(this);
    }

}
