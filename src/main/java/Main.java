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
import lib.graph.GraphB;
import lib.graph.io.GraphBIO;
import lib.math.Calcul;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InvalidOperationException {
        GraphB g = GraphBIO.loadFromFile("tests/g1.txt");

        GraphBIO.writeToDotFile(g, "dot/r0.dot");

        //g.split_r2_exits(0, 1);
        //g.merge(0, 1);
        //g.addExits(0, 2);
        //g.addEntries(2, 0);

        g.removeLooplessNodes();

        GraphBIO.printGraph(g);

        GraphBIO.writeToDotFile(g, "dot/r1.dot");

        //g.merge(2, 1);
        //g.merge(0, 1);
        g.reduceAll();

        GraphBIO.printGraph(g);
        //g.subExits(0, 2);
        //g.subEntries(2, 0);
        //g.addEntries(2, 0);
        //g.subEntries(2, 0);

        GraphBIO.writeToDotFile(g, "dot/r2.dot");

        //gbw.addEntries(3, 6);
        //gbw.removeNode(7);
        //gbw.removeNode(6);
        //gbw.addEntries(0, 4);
        //gbw.removeNode(5);
        //gbw.removeNode(4);

        //gbw.removeLooplessNodes();

        //GraphBWIO.writeToDotFile(gbw, "dot/r1.dot");
        //GraphBWIO.printGraph(gbw);
    }
}
