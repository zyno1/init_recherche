package lib.graph.io;

import lib.graph.GraphBW;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class GraphBWIO {
    public static void writeToDotFile(GraphBW g, String path) throws IOException {
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
