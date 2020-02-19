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
