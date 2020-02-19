import lib.graph.Graph;
import lib.graph.GraphIO;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("hello");
        Graph g = GraphIO.loadFromFile("tests/g0.txt");
        GraphIO.writeToFile(g, "g.txt");
        GraphIO.writeToDotFile(g, "dot.dot");
    }
}
