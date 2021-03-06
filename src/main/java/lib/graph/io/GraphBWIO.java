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

import lib.graph.Color;
import lib.graph.Graph;
import lib.graph.GraphBW;
import lib.graph.IGraph;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class GraphBWIO {

    /**
     * charge un graphe noir/blanc à partir d'un fichier TXT qui contient une matrice d'adjacence.
     * @param path chemin du fichier à charger
     * @return une instance de GraphBW qui représente le graphe noir/blanc chargé.
     * @throws IOException
     */
    public static GraphBW loadFromFile(String path) throws IOException {
        Graph res = GraphIO.loadFromFile(path);
        return GraphBW.fromGraphUnsafe(res);
    }

    /**
     * Sauve le graphe noir/blanc g dans un fichier DOT.
     * @param g une instance de GraphBW
     * @param path destination
     * @throws IOException
     */
    public static void writeToDotFile(GraphBW g, String path) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(path));

        out.write("digraph name {\n");

        for(int i = 0; i < g.nbVertices(); i++) {
            if(g.getColor(i) == Color.Black) {
                out.write(i + " [style=filled fontcolor=\"white\" fillcolor=\"black\"];\n");
            }
        }

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
     * Sauve une liste de graphes noir/blanc gc dans des fichiers DOT dans le répertoire folder.
     * @param gc liste de graphe noir/blanc
     * @param folder répertoire cible
     * @throws IOException
     */
    public static void writeToDotFiles(List<GraphBW> gc, String folder) throws IOException {
        folder = folder.trim();
        if(folder.charAt(folder.length() - 1) != '/') {
            folder += '/';
        }

        for(int i = 0; i < gc.size(); i++) {
            GraphBW g = gc.get(i);

            String path = folder + "r_" + i + ".dot";
            writeToDotFile(g, path);
        }
    }

    /**
     * Sauve le graphe g dans un fichier TXT.
     * @param g une instance de IGraph
     * @param path destination
     * @throws IOException
     */
    public static void writeToFile(IGraph g, String path) throws IOException {
        GraphIO.writeToFile(g, path);
    }

    /**
     * affiche la matrice d'adjacence de g sur la sortie standard
     * @param g une instance de IGraph
     */
    public static void printGraph(IGraph g) {
        GraphIO.printGraph(g);
    }
}
