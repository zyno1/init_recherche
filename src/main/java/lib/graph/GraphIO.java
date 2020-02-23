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

import java.io.*;

public class GraphIO {
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

    public static void writeToFile(Graph g, String path) throws IOException {
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

    public static void writeToDotFile(Graph g, String path) throws IOException {
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
}
