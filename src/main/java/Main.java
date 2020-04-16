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

        g.addExits(4, 2);

        GraphBWIO.writeToDotFile(g, "dot/r1.dot");

        //g.addExits(15, 5);
        /*int i1 = 3;
        int i2 = 6;
        int i2b = g.getBrother(i2);
        int[] tmp = new int[g.nbVertices()];
        tmp[i1] = 1;
        tmp[i2b] = 1;
        int[] w = new int[1];
        int[] b = new int[2];
        w[0] = g.split(2, tmp);
        b[0] = g.addNodeOnEdge(i1, w[0], Color.Black);
        b[1] = i2b;
        g.r3(b, w);
        g.removeSameColorNodes();*/
        g.subExits(3, 6);

        GraphBWIO.writeToDotFile(g, "dot/r2.dot");

        GraphBWIO.writeToDotFile(g, "dot/r3.dot");

        /*g.merge(3, 6);

        GraphBWIO.writeToDotFile(g, "dot/r2.dot");

        g.merge(2, 7);

        GraphBWIO.writeToDotFile(g, "dot/r3.dot");*/
    }
}
