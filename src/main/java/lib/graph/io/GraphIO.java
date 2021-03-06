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

package lib.graph.io;

import lib.graph.Graph;
import lib.graph.IGraph;

import java.io.*;

public class GraphIO {

    /**
     * Charge un graphe à partir d'un fichier TXT qui contient une matrice d'adjacence.
     * @param path chemin du fichier à charger
     * @return une instance de Graph qui représente le graphe chargé.
     * @throws IOException
     */
    public static Graph loadFromFile(String path) throws IOException {
        Graph res = null;

        BufferedReader in = new BufferedReader(new FileReader(path));

        String line;
        int i1 = 0;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if(line.length() != 0 && line.charAt(0) != '#') {

                String[] data = line.split(";");

                if(res == null) {
                    res = new Graph(data.length);
                }

                for(int i2 = 0; i2 < data.length; i2++) {
                    res.addEdges(i1, i2, Integer.parseInt(data[i2].trim()));
                }

                i1++;
            }
        }

        in.close();

        return res;
    }

    /**
     * Sauve le graphe g dans un fichier TXT.
     * @param g une instance de IGraph
     * @param path destination
     * @throws IOException
     */
    public static void writeToFile(IGraph g, String path) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(path));

        for(int j = 0; j < g.nbVertices(); j++) {
            for(int i = 0; i < g.nbVertices(); i++) {
                String suffix = ";";

                if(i == g.nbVertices() - 1) {
                    suffix = "\n";
                }

                out.write(g.getEdgeCount(j, i) + suffix);
            }
        }

        out.flush();
        out.close();
    }

    /**
     * Sauve le graphe g dans un fichier DOT.
     * @param g une instance de IGraph
     * @param path destination
     * @throws IOException
     */
    public static void writeToDotFile(IGraph g, String path) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(path));

        out.write("digraph name {\n");

        for(int j = 0; j < g.nbVertices(); j++) {
            for(int i = 0; i < g.nbVertices(); i++) {
                for(int k = 0; k < g.getEdgeCount(j, i); k++) {
                    out.write(j + " -> " + i + ";\n");
                }
            }
        }

        out.write("}");

        out.flush();
        out.close();
    }

    /**
     *
     * @param c un caractère
     * @param len un entier
     * @return Une String composée du caractère c répété len fois.
     */
    private static String repeat(char c, int len) {
        StringBuilder str = new StringBuilder(len);

        for(int i = 0; i < len; i++) {
            str.append(c);
        }

        return str.toString();
    }

    /**
     *
     * @param len un entier
     * @return Une String composée de len espaces.
     */
    private static String spaces(int len) {
        return repeat(' ', len);
    }

    /**
     *
     * @param num un entier
     * @param len un entier
     * @return Une String de taille len qui contient des espaces et le nombre num placé au millieu
     */
    private static String wrap(int num, int len) {
        StringBuilder str = new StringBuilder(len);

        String n = String.valueOf(num);
        str.append(spaces((len - n.length()) / 2));
        str.append(n);
        str.append(spaces(len - str.length()));

        return str.toString();
    }

    /**
     * affiche la matrice d'adjacence de g sur la sortie standard
     * @param g une instance de IGraph
     */
    public static void printGraph(IGraph g) {
        int maxValue = g.nbVertices() - 1;

        for(int j = 0; j < g.nbVertices(); j++) {
            for(int i = 0; i < g.nbVertices(); i++) {
                maxValue = Math.max(maxValue, g.getEdgeCount(i, j));
            }
        }

        int len = String.valueOf(maxValue).length() + 2;
        System.out.print(repeat('-', (g.nbVertices() + 1) * (len + 1) + 1) + "\n");
        System.out.print('|');
        System.out.print(spaces(len));
        for(int i = 0; i < g.nbVertices(); i++) {
            System.out.print('|');
            System.out.print(wrap(i, len));
        }
        System.out.print("|\n" + repeat('-', (g.nbVertices() + 1) * (len + 1) + 1) + "\n");

        for(int j = 0; j < g.nbVertices(); j++) {
            System.out.print('|');
            System.out.print(wrap(j, len));
            for(int i = 0; i < g.nbVertices(); i++) {
                System.out.print('|');
                System.out.print(wrap(g.getEdgeCount(j, i), len));
            }
            System.out.print("|\n");
        }
        System.out.print(repeat('-', (g.nbVertices() + 1) * (len + 1) + 1) + "\n");
    }
}
