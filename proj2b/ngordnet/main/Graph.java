package ngordnet.main;

import java.util.ArrayList;

public class Graph {
    private class Node {
        private static int synsetID;
        private ArrayList<String> synset;
        public Node(int id, ArrayList<String> synsetWords) {
            synsetID = id;
            synset = synsetWords;
        }
    }
}
