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
import lib.graph.Color;
import lib.graph.Graph;
import lib.graph.GraphBW;
import lib.graph.io.GraphBWIO;
import lib.graph.io.GraphIO;

import java.io.IOException;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws IOException, InvalidOperationException {
        //Graph g = GraphIO.loadFromFile("tests/g1.txt");
        GraphBW g = GraphBWIO.loadFromFile("tests/g1.txt");

        GraphBWIO.writeToDotFile(g, "dot/r0.dot");

        //g.removeSameColorNodes();
        //g.addExits(15, 5);
        //int i1 = 2;
        //int i2 = 4;
        //int[] i2exit = g.getExits(i2);
        //i2exit[i1] -= 1;
        //g.split(i2, i2exit);

        g.addEntries(2, 4);

        GraphBWIO.writeToDotFile(g, "dot/r1.dot");

        //g.addExits(15, 5);

        GraphBWIO.writeToDotFile(g, "dot/r2.dot");

        GraphBWIO.writeToDotFile(g, "dot/r3.dot");

        /*g.merge(3, 6);

        GraphBWIO.writeToDotFile(g, "dot/r2.dot");

        g.merge(2, 7);

        GraphBWIO.writeToDotFile(g, "dot/r3.dot");*/
    }
}
