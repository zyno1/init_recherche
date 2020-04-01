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
        Graph g = GraphIO.loadFromFile("tests/g1.txt");
        //GraphBW gbw = GraphBWIO.loadFromFile("tests/g2.txt");

        GraphIO.writeToDotFile(g, "dot/init.dot");
        //GraphBWIO.writeToDotFile(gbw, "dot/init.dot");

        //g.addEntries(0, 1);
        g.removeLooplessNodes();
        //gbw.addEntries(3, 4);
        //gbw.addExits(7, 1);

        GraphIO.writeToDotFile(g, "dot/r1.dot");
        //GraphBWIO.writeToDotFile(gbw, "dot/r1.dot");

        //g.subEntries(0, 1);
        GraphIO.printGraph(g);
        g.putZeros();
        //GraphIO.printGraph(g);
        //gbw.subExits(7, 1);
        //gbw.subEntries(3, 4);

        //GraphBWIO.writeToDotFile(gbw, "dot/r2.dot");
        GraphIO.writeToDotFile(g, "dot/r2.dot");
    }
}
