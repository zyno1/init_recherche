package lib.graph.io;

import lib.graph.Graph;
import lib.graph.GraphBW;

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
            if(GraphBW.sum(g.getExits(i)) > 1) {
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

    public static void writeToFile(GraphBW g, String path) throws IOException {
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

    private static String repeat(char c, int len) {
        StringBuilder str = new StringBuilder(len);

        for(int i = 0; i < len; i++) {
            str.append(c);
        }

        return str.toString();
    }

    private static String spaces(int len) {
        return repeat(' ', len);
    }

    private static String wrap(int num, int len) {
        StringBuilder str = new StringBuilder(len);

        String n = String.valueOf(num);
        str.append(spaces((len - n.length()) / 2));
        str.append(n);
        str.append(spaces(len - str.length()));

        return str.toString();
    }

    public static void printGraph(GraphBW g) {
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
