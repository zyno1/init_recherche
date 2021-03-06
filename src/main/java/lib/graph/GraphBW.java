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

import java.util.*;

public class GraphBW implements IGraph {
    ArrayList<Integer> data;
    ArrayList<Color> colors;
    int nb;

    /**
     *
     * @param n nombre de sommets
     */
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

    /**
     *
     * @return une copie du graphe BW
     */
    public GraphBW clone() {
        GraphBW g = new GraphBW(nbVertices());

        for(int i = 0; i < nbVertices(); i++) {
            g.colors.set(i, colors.get(i));
        }

        for(int i = 0; i < data.size(); i++) {
            g.data.set(i, data.get(i));
        }

        return g;
    }

    /**
     *
     * @param g instance de Graph
     * @return une instance de GraphBW créée à partir de g
     */
    public static GraphBW fromGraph(Graph g) {
        return fromGraphUnsafe(g.clone());
    }

    /**
     * Attention cette méthode modifie g
     * pour éviter ce problème utiliser <i>fromGraph(Graph g)</i>
     * @param g instance de Graph
     * @return une instance de GraphBW créée à partir de g
     */
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

    /**
     *
     * @return le nombre de sommets
     */
    public int nbVertices() {
        return nb;
    }

    /**
     *
     * @param i1 l'indice correspondant à un sommet du graphe
     * @param i2 l'indice correspondant à un sommet du graphe
     * @return le nombre d'arrêtes de i1 vers i2
     */
    public int getEdgeCount(int i1, int i2) {
        return data.get(i1 * nb + i2);
    }

    /**
     * Met à jour le nombre de sommets de i1 vers i2
     * @param i1 l'indice correspondant à un sommet du graphe
     * @param i2 l'indice correspondant à un sommet du graphe
     * @param n nouvelle valeur
     */
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

    /**
     *
     * @param i l'indice correspondant à un sommet du graphe
     * @return la couleur du sommet i
     */
    public Color getColor(int i) {
        return colors.get(i);
    }

    /**
     * met à jour la couleur du sommet i
     * @param i l'indice correspondant à un sommet du graphe
     * @param c nouvelle couleur
     * @throws InvalidOperationException si la c est incohérent par rapport au entrées et sorties de i
     */
    public void setColor(int i, Color c) throws InvalidOperationException {
        if(c == Color.Black && Calcul.sum(getEntries(i)) != 1) {
            throw new InvalidOperationException();
        }
        else if(c == Color.White && Calcul.sum(getExits(i)) != 1) {
            throw new InvalidOperationException();
        }
        colors.set(i, c);
    }

    /**
     * Ajoute n arrêtes de i1 vers i2
     * @param i1 l'indice correspondant à un sommet du graphe
     * @param i2 l'indice correspondant à un sommet du graphe
     * @param n le nombre d'arrêtes à ajouter
     */
    public void addEdges(int i1, int i2, int n) throws InvalidOperationException {
        int k = getEdgeCount(i1, i2) + n;
        setEdgeCount(i1, i2, k);
    }

    /**
     * Supprime n arrêtes de i1 vers i2
     * @param i1 l'indice correspondant à un sommet du graphe
     * @param i2 l'indice correspondant à un sommet du graphe
     * @param n le nombre d'arrêtes à supprimer
     * @throws InvalidOperationException si n est plus grand que le nombre d'arrêtes initiale de i1 vers i2
     */
    public void removeEdges(int i1, int i2, int n) throws InvalidOperationException {
        int k = getEdgeCount(i1, i2) - n;
        if(k < 0) {
            throw new InvalidOperationException();
        }
        setEdgeCount(i1, i2, k);
    }

    /**
     * Ajoute un sommet dans le graphe de couleur c
     * @param c une couleur
     * @return l'indice du sommet ajouté
     */
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

    /**
     * Ajoute une copie du graph BW dans gc et supprime le sommet i
     * @param gc liste de GraphBW
     * @param i l'indice du sommet à supprimer
     */
    public void removeNode(List<GraphBW> gc, int i) {
        gc.add(this.clone());

        for(int j = 0; j < nb; j++) {
            data.remove(i * nb);
        }
        for(int j = i; j < data.size(); j += nb - 1) {
            data.remove(j);
        }
        colors.remove(i);
        nb--;
    }

    /**
     *
     * @param i l'indice correspondant à un sommet du graphe
     * @return un tableau res tel que res[j] est égal au nombre d'arrêtes de i vers j.
     */
    public int[] getExits(int i) {
        int[] res = new int[nb];

        for(int j = 0; j < nb; j++) {
            res[j] = getEdgeCount(i, j);
        }

        return res;
    }

    /**
     *
     * @param i l'indice correspondant à un sommet du graphe
     * @return un tableau res tel que res[j] est égal au nombre d'arrêtes de j vers i.
     */
    public int[] getEntries(int i) {
        int [] res = new int[nb];

        for(int j = 0; j < nb; j++) {
            res[j] = getEdgeCount(j, i);
        }

        return res;
    }

    /**
     * Ajoute une copie du graphe dans gc puis divise le sommet i1 en suivant la règle R2
     * si le sommet est de couleur <i>Black</i> on divise ses sorties si il est <i>White</i> on divise ses entrée
     * @param gc liste de GraphBW
     * @param i1 indice correspondant à un sommet du graphe
     * @param split masque correspondant aux entrées ou sorties que l'on souhaites transférer de i1 au nouveau sommet
     * @return l'indice du sommet créé par l'opération
     * @throws InvalidOperationException si split contient une valeur négative
     */
    public int split(List<GraphBW> gc, int i1, int... split) throws InvalidOperationException {
    //public int split(int i1, int... split) throws InvalidOperationException {
        gc.add(this.clone());

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

    /**
     * Ajoute une copie du graphe dans gc puis fusionne les sommets i1 et i2 en suivant la règle R2
     * @param gc liste de GraphBW
     * @param i1 indice correspondant à un sommet du graphe
     * @param i2 indice correspondant à un sommet du graphe
     * @throws InvalidOperationException si les sommets sont de couleurs différentes ou si ils ne sont pas reliés par une arrète
     */
    public void merge(List<GraphBW> gc, int i1, int i2) throws InvalidOperationException {
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

        gc.add(this.clone());

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

        removeNode(gc, i2);
    }

    /**
     * Ajoute une copie du graphe à gc puis ajoute un sommet entre i1 et i2 du couleur c
     * @param gc Liste de GraphBW
     * @param i1 indice d'un sommet du graphe
     * @param i2 indice d'un sommet du graphe
     * @param c une couleur
     * @return l'indice du sommet ajouté
     * @throws InvalidOperationException si il n'y a pas d'arrête entre i1 et i2
     */
    public int addNodeOnEdge(List<GraphBW> gc, int i1, int i2, Color c) throws InvalidOperationException {
        int nb = getEdgeCount(i1, i2);
        if(nb < 1) {
            throw new InvalidOperationException();
        }

        gc.add(this.clone());

        int i3 = addNode(c);

        setEdgeCount(i1, i2, nb - 1);

        addEdges(i1, i3, 1);
        addEdges(i3, i2, 1);

        return i3;
    }

    /**
     * Ajoute une copie du graphe à gc puis supprime le sommet i
     * @param gc Liste de GraphBW
     * @param i indice du sommet à supprimer
     * @throws InvalidOperationException si le sommet ne peut pas etre supprimé :
     * un sommet peut être supprimer si il posséde exactement une arrête entrante et une arrête sortante
     */
    public void removeNodeOnEdge(List<GraphBW> gc, int i) throws InvalidOperationException {
        int[] entries = getEntries(i);
        int[] exits = getExits(i);

        if(Calcul.sum(entries) != 1 || Calcul.sum(exits) != 1) {
            throw new InvalidOperationException();
        }

        gc.add(this.clone());

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
        removeNode(gc, i);
    }

    /**
     * Ajoute une copie du Graph à gc et applique la règle R3 généralisée sur les sommets i1 et i2
     * @param gc Liste de GraphBW
     * @param i1 indice correspondant à un sommet du graphe
     * @param i2 indice correspondant à un sommet du graphe
     * @return un tableau à 2 dimension res tel que res [0][i] est l'indice du i-ème sommet noir créé
     * par l'opération et res[1][j] est l'indice du i-ème sommet blanc créé par l'opération.
     * @throws InvalidOperationException si on ne peut pas appliquer la règle R3. Pour appliquer R3,
     * il faut que i1 soit un sommet blanc et i2 un sommet noir et que i1 et i2 soient reliés par une
     * et une seule arrête.
     */
    public int[][] r3(List<GraphBW> gc, final int i1, final int i2) throws InvalidOperationException {
        int[] i1exit = getExits(i1);
        int[] i2entries = getEntries(i2);

        if(getColor(i1) != Color.White || getColor(i2) != Color.Black || getEdgeCount(i1, i2) != 1) {
            throw new InvalidOperationException();
        }

        gc.add(this.clone());

        int first = nbVertices();
        int entry = 0;
        int exit = 0;

        int[][] res = new int[2][];

        for(int j = 0; j < nbVertices(); j++) {
            int n = getEdgeCount(j, i1);
            setEdgeCount(j, i1, 0);
            while (n > 0) {
                int t = addNode(Color.Black);
                addEdges(j, t, 1);
                entry++;
                n--;
            }
        }

        for(int j = 0; j < nbVertices(); j++) {
            int n = getEdgeCount(i2, j);
            setEdgeCount(i2, j, 0);
            while (n > 0) {
                int t = addNode(Color.White);
                addEdges(t, j, 1);
                exit++;
                n--;
            }
        }

        res[0] = new int[entry];
        res[1] = new int[exit];

        for(int i = 0; i < entry; i++) {
            for(int j = 0; j < exit; j++) {
                addEdges(i + first, j + first + entry, 1);
            }
        }

        if(i1 > i2) {
            removeNode(gc, i1);
            removeNode(gc, i2);
        }
        else {
            removeNode(gc, i2);
            removeNode(gc, i1);
        }

        for (int i = 0; i < entry; i++) {
            res[0][i] = i + first - 2;
        }
        for(int i = 0; i < exit; i++) {
            res[1][i] = i + first + entry - 2;
        }

        return res;
    }

    /**
     *
     * @param i un entier
     * @param l une collection d'entiers
     * @return True si i est contenu dans l, False sinon.
     */
    private boolean contains(int i, int... l) {
        for(int j = 0; j < l.length; j++) {
            if(l[j] == i) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ajoute une copie du graphe dans gc puis applique la règle R3 inverse sur les sommets dans entry et exit.
     * @param gc Une liste de GraphBW
     * @param entry indices des sommets noirs
     * @param exit indices des sommets blancs
     * @throws InvalidOperationException si une de ces conditions n'est pas respectée :
     * 1) entry ne contient que des sommets noirs
     * 2) exit ne contient que des sommets blancs
     * 3) il y a exactement une arrête de chaque sommet noir vers chaque sommet blanc
     * 4) il n'y a aucune arrête qui part d'un sommet blanc vers un sommet noir
     */
    public void r3(List<GraphBW> gc, int[] entry, int[] exit) throws InvalidOperationException {
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

        gc.add(this.clone());

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
                        removeNode(gc, entry[i--]);
                    }
                    else {
                        removeNode(gc, exit[j--]);
                    }
                }
                else {
                    removeNode(gc, entry[i--]);
                }
            }
            else {
                removeNode(gc, exit[j--]);
            }
        }
    }

    /**
     * Ajoute une copie du graphe dans gc puis ajoute les entrées du sommet-frère de i2 sur i1
     * @param gc Liste de GraphBW
     * @param i1 l'indice d'un sommet blanc
     * @param i2 l'indice d'un sommet noir
     * @throws InvalidOperationException si i1 est noir ou i2 est blanc ou si il n'y a pas d'arrète de i2 vers i1
     */
    public void addEntries(List<GraphBW> gc, int i1, int i2) throws InvalidOperationException {
        if(getColor(i1) == Color.Black || getColor(i2) == Color.White || getEdgeCount(i2, i1) < 1) {
            throw new InvalidOperationException();
        }

        int[] i2exit = getExits(i2);
        i2exit[i1] -= 1;
        split(gc, i2, i2exit);

        int i2b = getBrother(i2);
        r3(gc, i2b, i2);

        removeSameColorNodes(gc);
    }

    /**
     *
     * @param i indice d'un sommet du graphe
     * @return l'indice du sommet-frère de i
     */
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

    /**
     * Ajoute une copie du graphe dans gc puis ajoute les sorties du sommet-frère de i2 sur i1
     * @param gc Liste de GraphBW
     * @param i1 l'indice d'un sommet noir
     * @param i2 l'indice d'un sommet blanc
     * @throws InvalidOperationException si i1 est blanc ou i2 est noir ou si il n'y a pas d'arrète de i1 vers i2
     */
    public void addExits(List<GraphBW> gc, int i1, int i2) throws InvalidOperationException {
        if(getColor(i2) == Color.Black || getColor(i1) == Color.White || getEdgeCount(i1, i2) < 1) {
            throw new InvalidOperationException();
        }

        int[] i2entries = getEntries(i2);
        i2entries[i1] -= 1;
        split(gc, i2, i2entries);

        int i2b = getBrother(i2);
        r3(gc, i2, i2b);

        removeSameColorNodes(gc);
    }

    /**
     *
     * @return un tableau de 0 dont la taille est égale au nombre de sommets dans le graphe
     */
    private int[] zeros() {
        return new int[nbVertices()];
    }

    /**
     * Ajoute une copie du graphe dans gc puis soustrait les sorties du sommet-frère de i2 sur i1
     * @param gc Liste de GraphBW
     * @param i1 indice d'un sommet noir
     * @param i2 indice d'un sommet blanc
     * @throws InvalidOperationException si i1 est blanc ou i2 est noir ou encore si l'ensemble des sorties à
     * enlever ne sont pas toutes présentes sur i1.
     */
    public void subExits(List<GraphBW> gc, int i1, int i2) throws InvalidOperationException {
        if(getColor(i1) == Color.White) {
            throw new InvalidOperationException();
        }

        int i2b = getBrother(i2);

        for(int j = 0; j < nbVertices(); j++) {
            if(getEdgeCount(i1, j) < getEdgeCount(i2b, j)) {
                throw new InvalidOperationException();
            }
        }
        int[] mask = zeros();
        mask[i1] = 1;
        mask[i2b] = 1;

        int first = nbVertices();

        for(int i = 0; i < first; i++) {
            while (getEdgeCount(i2b, i) > 0) {
                split(gc, i, mask);
            }
        }

        mask = zeros();
        int[] b = new int[2];
        b[1] = i2b;
        int[] w = new int[nbVertices() - first];
        for (int i = 0; i < w.length; i++) {
            w[i] = i + first;
            mask[i + first] = 1;
        }
        b[0] = split(gc, i1, mask);

        r3(gc, b, w);
        removeSameColorNodes(gc);
    }

    /**
     * Ajoute une copie du graphe dans gc puis soustrait les entrées du sommet-frère de i2 sur i1
     * @param gc Liste de GraphBW
     * @param i1 indice d'un sommet blanc
     * @param i2 indice d'un sommet noir
     * @throws InvalidOperationException si i1 est noir ou i2 est blanc ou encore si l'ensemble des entrées à
     * enlever ne sont pas toutes présentes sur i1.
     */
    public void subEntries(List<GraphBW> gc, int i1, int i2) throws InvalidOperationException {
        if(getColor(i1) == Color.Black) {
            throw new InvalidOperationException();
        }

        int i2b = getBrother(i2);

        for(int j = 0; j < nbVertices(); j++) {
            if(getEdgeCount(j, i1) < getEdgeCount(j, i2b)) {
                throw new InvalidOperationException();
            }
        }

        int[] mask = zeros();
        mask[i1] = 1;
        mask[i2b] = 1;

        int first = nbVertices();
        for (int i = 0; i < first; i++) {
            while (getEdgeCount(i, i2b) > 0) {
                split(gc, i, mask);
            }
        }

        int[] b = new int[nbVertices() - first];
        int[] w = new int[2];
        mask = zeros();
        w[0] = i2b;
        for (int i = 0; i < b.length; i++) {
            b[i] = i + first;
            mask[i + first] = 1;
        }
        w[1] = split(gc, i1, mask);

        r3(gc, b, w);
        removeSameColorNodes(gc);
    }

    /**
     * Simplifie le graphe en fusionnant des sommets de même couleur
     * Ajoute chaque étape dans gc.
     * @param gc Liste de GraphBW
     * @throws InvalidOperationException
     */
    public void removeSameColorNodes(List<GraphBW> gc) throws InvalidOperationException {
        for(int i = nbVertices() - 1; i >= 0; i--) {
            for (int j = 0; j < nbVertices(); j++) {
                if(getColor(i) == getColor(j) && i != j && (getEdgeCount(i, j) != 0 || getEdgeCount(j, i) != 0)) {
                    merge(gc, i, j);
                    break;
                }
            }
        }
    }

    /**
     * Applique l'algorithme de suppression des paire de sommet-frère sans boucle.
     * Ajoute chaque étape de l'algorithme dans gc.
     * @param gc Liste de GraphBW
     * @throws InvalidOperationException
     */
    public void removeLooplessNodes(List<GraphBW> gc) throws InvalidOperationException {
        for(int j = nbVertices() - 1; j >= 0; j--) {
            int jb = j;
            int jw = getBrother(j);

            if(jw == -1) {
                continue;
            }

            if(getColor(jw) != Color.White) {
                int tmp = jw;
                jw = jb;
                jb = tmp;
            }

            if(getEdgeCount(jb, jw) == 0) {
                int se = Calcul.sum(getEntries(jw));
                int sx = Calcul.sum(getExits(jb));

                if(se == 0 || sx == 0) {
                    removeNode(gc, Math.max(jb, jw));
                    removeNode(gc, Math.min(jb, jw));
                }
                else {
                    for (int i = 0; i < nbVertices(); i++) {
                        if (se > sx) {
                            if (getEdgeCount(jb, i) > 0) {
                                addEntries(gc, i, jb);
                                break;
                            }
                        } else {
                            if (getEdgeCount(i, jw) > 0) {
                                addExits(gc, i, jw);
                                break;
                            }
                        }
                    }
                }

                j = nbVertices() - 1;
            }
        }
    }
}
