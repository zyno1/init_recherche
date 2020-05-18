package lib.graph.io;

import lib.graph.*;

import java.io.*;
import java.util.List;

public class GraphBIO {
    public static GraphB loadFromFile(String path) throws IOException {
        GraphB res = null;

        BufferedReader in = new BufferedReader(new FileReader(path));

        String line;
        int i1 = 0;
        while ((line = in.readLine()) != null) {
            line = line.trim();
            if(line.length() != 0 && line.charAt(0) != '#') {

                String[] data = line.split(";");

                if(res == null) {
                    res = new GraphB(data.length);
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

    public static void writeToDotFile(GraphB g, String path) throws IOException {
        GraphIO.writeToDotFile(g, path);
    }

    public static void writeToDotFiles(List<GraphB> gc, String folder) throws IOException {
        folder = folder.trim();
        if(folder.charAt(folder.length() - 1) != '/') {
            folder += '/';
        }

        for(int i = 0; i < gc.size(); i++) {
            GraphB g = gc.get(i);

            String path = folder + "r_" + i + ".dot";
            writeToDotFile(g, path);
        }
    }

    public static void writeToFile(IGraph g, String path) throws IOException {
        GraphIO.writeToFile(g, path);
    }

    public static void printGraph(IGraph g) {
        GraphIO.printGraph(g);
    }
}
