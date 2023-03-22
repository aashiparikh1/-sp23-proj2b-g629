package ngordnet.main;

import java.util.ArrayList;

public class Graph {
    private class Node {
        private static int synsetID;
        private ArrayList<Node> synsetChildren;
        public Node(int id, ArrayList<Node> children) {
            synsetID = id;
            synsetChildren = children;
        }
    }
}
