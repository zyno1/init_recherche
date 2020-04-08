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

import lib.exceptions.InvalidOperationException;
import lib.graph.Graph;
import lib.graph.GraphBW;
import lib.graph.io.GraphBWIO;
import lib.graph.io.GraphIO;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InvalidOperationException {
        Graph g = GraphIO.loadFromFile("tests/g2.txt");
        Graph f = g.clone();

        GraphIO.printGraph(g);
        GraphIO.writeToDotFile(g, "dot/r0.dot");

        int p0 = g.splitExits(0, 0, 1);

        //GraphIO.printGraph(g);
        GraphIO.writeToDotFile(g, "dot/r1.dot");

        int p1 = g.splitEntries(1, 0, 0, 0, 0, 1);

        //GraphIO.printGraph(g);
        GraphIO.writeToDotFile(g, "dot/r2.dot");

        g.flowEquivalence(p0, p1);

        //GraphIO.printGraph(g);
        GraphIO.writeToDotFile(g, "dot/r3.dot");

        g.mergeExits(0, p0);

        GraphIO.printGraph(g);
        GraphIO.writeToDotFile(g, "dot/r.dot");

        f.addExits(0, 1);
        GraphIO.printGraph(f);
    }
}
