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

import java.io.*;

public class GraphBWIO {
    public static GraphBW loadFromFile(String path) throws IOException {
        Graph res = GraphIO.loadFromFile(path);
        return GraphBW.fromGraphUnsafe(res);
    }

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

    public static void writeToFile(IGraph g, String path) throws IOException {
        GraphIO.writeToFile(g, path);
    }

    public static void printGraph(IGraph g) {
        GraphIO.printGraph(g);
    }
}
