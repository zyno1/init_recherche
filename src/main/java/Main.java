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
import lib.graph.io.GraphIO;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InvalidOperationException {
        Graph g = GraphIO.loadFromFile("tests/g3.txt");
        GraphIO.writeToDotFile(g, "r0.dot");

        int f0 = g.flowEquivalence(0);
        GraphIO.writeToDotFile(g, "r1.dot");

        int sf0 = g.splitExits(f0, 0, 0, 0, 0, 1, 0, 0);
        GraphIO.writeToDotFile(g, "r2.dot");

        int fsf0 = g.flowEquivalence(sf0);
        GraphIO.writeToDotFile(g, "r3.dot");

        g.mergeExits(f0, sf0);
        GraphIO.writeToDotFile(g, "r4.dot");

        g.flowEquivalence(0, f0);
        GraphIO.writeToDotFile(g, "r5.dot");
    }
}
