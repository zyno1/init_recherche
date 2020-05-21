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

        int loops = getEdgeCount(i1, i1) + getEdgeCount(i2, i2) + getEdgeCount(i1, i2) + getEdgeCount(i2, i1);

        for(int j = 0; j < nbVertices(); j++) {
            int in = getEdgeCount(j, i1) + getEdgeCount(j, i2);
            int out = getEdgeCount(i1, j) + getEdgeCount(i2, j);

            setEdgeCount(j, node, in);
            setEdgeCount(node, j, out);

        }

        setEdgeCount(node, node, loops);

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
                return;
            }
            else if(Calcul.sum(getEntries(i2)) == 1) {
                removeEdges(i1, i2, 1);
                merge_r2(i1, i2);
                return;
            }
        }
        if(getEdgeCount(i2, i1) == 1) {
            if(Calcul.sum(getExits(i2)) == 1) {
                removeEdges(i2, i1, 1);
                merge_r2(i1, i2);
                return;
            }
            else if(Calcul.sum(getEntries(i1)) == 1) {
                removeEdges(i2, i1, 1);
                merge_r2(i1, i2);
                return;
            }
        }
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

        int[] i2entry = getEntries(i2);
        i2entry[i1] -= 1;
        int p2 = split_r2_entries(i2, i2entry);
        int pp2 = split_entry(i2);

        merge(i1, pp2);
        merge(i2, p2);
    }

    public void addEntries(int i1, int i2) throws InvalidOperationException {
        if(getEdgeCount(i2, i1) < 1) {
            throw new InvalidOperationException();
        }

        int[] i2exit = getExits(i2);
        i2exit[i1] -= 1;
        int p2 = split_r2_exits(i2, i2exit);
        int pp2 = split_exit(i2);

        merge(i1, pp2);
        merge(i2, p2);
    }

    public void subEntries(int i1, int i2) throws InvalidOperationException {
        for(int i = 0; i < nbVertices(); i++) {
            if(getEdgeCount(i, i1) < getEdgeCount(i, i2)) {
                throw new InvalidOperationException();
            }
        }

        int p2 = split_r2_exits(i2, getExits(i2));
        int p1 = split_r2_entries(i1, getEntries(i2));

        merge(i2, p1);
        merge(i2, p2);
    }

    public void subExits(int i1, int i2) throws InvalidOperationException {
        for(int i = 0; i < nbVertices(); i++) {
            if(getEdgeCount(i1, i) < getEdgeCount(i2, i)) {
                throw new InvalidOperationException();
            }
        }

        int p2 = split_r2_entries(i2, getEntries(i2));
        int p1 = split_r2_exits(i1, getExits(i2));

        merge(i2, p1);
        merge(i2, p2);
    }

    public void removeUselessNodes() {
        for(int i = nbVertices() - 1; i >= 0; i--) {
            if(Calcul.sum(getEntries(i)) == 1) {
                for(int j = 0; j < nbVertices(); j++) {
                    if(getEdgeCount(j, i) == 1) {
                        try {
                            merge(j, i);
                        } catch (InvalidOperationException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
            else if(Calcul.sum(getExits(i)) == 1) {
                for(int j = 0; j < nbVertices(); j++) {
                    if(getEdgeCount(i, j) == 1) {
                        try {
                            merge(j, i);
                        } catch (InvalidOperationException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        }
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

    /**
     *
     * @param i l'indice correspondant à un sommet du graphe
     * @return un tableau res tel que res[j] est égal au nombre d'arrêtes de j vers i
     * dans le graphe sans la matrice identité.
     */
    private int[] getEntriesWithoutIdentity(int i) {
        int [] res = new int[nb];

        for(int j = 0; j < nb; j++) {
            res[j] = getEdgeCount(j, i);
        }

        res[i]--;

        return res;
    }

    /**
     *
     * @param i l'indice correspondant à un sommet du graphe
     * @return un tableau res tel que res[j] est égal au nombre d'arrêtes de i vers j
     * dans le graphe sans la matrice identité.
     */
    private int[] getExitsWithoutIdentity(int i) {
        int[] res = new int[nb];

        for(int j = 0; j < nb; j++) {
            res[j] = getEdgeCount(i, j);
        }
        res[i]--;

        return res;
    }

    /**
     *
     * @param i1 l'indice correspondant à un sommet du graphe
     * @param i2 l'indice correspondant à un sommet du graphe
     * @return le nombre d'arrêtes de i1 vers i2 dans la graphe sans la matrice identité.
     */
    private int getEdgeCountWithoutIdentity(int i1, int i2) {
        int res = data.get(i1 * nb + i2);
        if(i1==i2)res--;
        return res;
    }

    /**
     * Vérifie que l'on peut soustraire les entrées de i2 sur i1.
     * @param i1 l'indice correspondant à un sommet du graphe
     * @param i2 l'indice correspondant à un sommet du graphe
     * @return True la soustraction est possible False sinon
     */
    private boolean testSub(int i1, int i2) {
        for (int j = 0; j < nbVertices(); j++) {
            if(getEdgeCount(j, i1) < getEdgeCount(j, i2)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Réduit le graphe en forme normale
     * @throws InvalidOperationException
     */
    public void reduceAll() throws InvalidOperationException{
        for(int i=nbVertices()-1; i>=0; i--) {
            reduceLine(i);
        }
    }

    /**
     * réduit une ligne de la matrice du graphe.
     * @param line ligne à réduire
     * @throws InvalidOperationException
     */
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


    /**
     * Réduit la dst-ième valeur de la line-ième ligne de la matrice du graphe
     * en appliquant la soustraction des entrée de la min-ième valeur de la ligne
     * @param line ligne de la matrice
     * @param min indice de la valeur non nulle minimum sur la ligne
     * @param dst indice de la valeur à réduire
     * @return nombre soustraction d'entrées de min sur dst effectués.
     * @throws InvalidOperationException
     */
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
}
