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
import lib.graph.GraphBW;
import lib.graph.io.GraphBWIO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InvalidOperationException {
        GraphBW g = GraphBWIO.loadFromFile("tests/g2.txt");

        List<GraphBW> gc = new ArrayList<>();
        g.removeLooplessNodes(gc);
        gc.add(g.clone());

        GraphBWIO.writeToDotFiles(gc, "dot/");

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
