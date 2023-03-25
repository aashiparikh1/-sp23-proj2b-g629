package ngordnet.main;

import edu.princeton.cs.algs4.In;
import ngordnet.ngrams.NGramMap;

import java.util.*;

public class Graph {
    private class Node {
        private static int synsetID;
        private List<String> words;
        private List<Node> synsetChildren;
        public Node(int id, List<String> words) {
            synsetID = id;
            this.words = words;
            synsetChildren = new ArrayList<>();
        }
        public List<Node> getSynsetChildren() {
            return synsetChildren;
        }
        public List<String> getWord() {
            return words;
        }
    }

    // Instance variables
    public String synsetFilePath = "./data/wordnet/synsets16.txt";
    public Map<String, List<Integer>> wordIDMap;
    public Map<Integer, Node> synsetToNode;
    public Map<Integer, List<String>> synsetToWord;
    public String hyponymFilePath = "./data/wordnet/hyponyms16.txt";
    public NGramMap ngm;

    public Graph () {
        wordIDMap = new HashMap<>();
        synsetToNode = new HashMap<>();
        synsetToWord = new HashMap<>();
        synsetInput(synsetFilePath);
        hyponymInput(hyponymFilePath);
    }

    // this function also constructs the tree structure
    public void hyponymInput (String path) {
        In hyponymsIn = new In(path);
        while (hyponymsIn.hasNextLine()) {
            String[] currentLine = hyponymsIn.readLine().split(",");
            //creates a list of strings of the contents of the line starting from index 1 (skipping the first item)
            List<String> childrenStrings = Arrays.asList(Arrays.copyOfRange(currentLine, 1, currentLine.length));
            //code to convert it to a list of ints
            List<Integer> childrenInt = new ArrayList<>();
            for (String s : childrenStrings) {
                childrenInt.add(Integer.parseInt(s));
            }

            if (synsetToNode.containsKey(Integer.parseInt(currentLine[0]))) {
                for (String s : childrenStrings) {
                    int childID = Integer.parseInt(s);
                    Node child;
                    if (!synsetToNode.containsKey(childID)) {
                        child = new Node(childID, synsetToWord.get(childID));
                    } else {
                        child = synsetToNode.get(childID);
                    }
                    synsetToNode.get(Integer.parseInt(currentLine[0])).synsetChildren.add(child);
                }
            }
        }

    }

    // Takes in the file path specified in instance variables
    public void synsetInput (String path) {
        In synsetsIn = new In(path);
        while (synsetsIn.hasNextLine()) {
            // splits current line into string array by commas
            String[] currentLine = synsetsIn.readLine().split(",");
            /*
            1st index item in string away is the words of the synset, so it splits that by spaces, and puts it into a new
            string array
             */
            String[] words = currentLine[1].split(" ");

            synsetToNode.put(Integer.parseInt(currentLine[0]), new Node(Integer.parseInt(currentLine[0]), Arrays.asList(words)));

            // adds synset id mapped to list of the words it represents. (necessary for creating hierarchy in hyponym method)
            synsetToWord.put(Integer.parseInt(currentLine[0]), Arrays.asList(words));
            /* iterates over the string array.
            if the word is in the map already, it gets the value associated with that word (a list), and adds the 0th
            index of the currentLine (the synsetid number) to the list
            if the word is not in the map, it creates a list, adds the synset id, and puts the list and word in the map
             */

            for (String word : words) {
                if (wordIDMap.containsKey(word)) {
                    wordIDMap.get(word).add(Integer.parseInt(currentLine[0]));
                } else {
                    List<Integer> tempList = new ArrayList<>();
                    tempList.add(Integer.parseInt(currentLine[0]));
                    wordIDMap.put(word, tempList);
                }
            }
        }
    }


    public Set<String> hyponymHelper (Node n) {
        Set<String> tempSet = new TreeSet<>();
        if (n != null) {
            tempSet.addAll(n.words);
            for (Node s : n.synsetChildren) {
                tempSet.addAll(hyponymHelper(s));
            }
        }
        return tempSet;
    }

    public List<String> getHyponyms (String word) {
        Set<String> hyponyms = new TreeSet();
        if (wordIDMap.containsKey(word)) {
            List<Integer> synsets = wordIDMap.get(word);
            for (int synset : synsets) {
                hyponyms.addAll(hyponymHelper(synsetToNode.get(synset)));
            }
        }
        /*
        for (String wordKey : wordIDMap.keySet()) {
            if (wordKey.equals(word)) {
                List<Integer> synsets = wordIDMap.get(wordKey);
                for (int synset : synsets) {
                    List<Node> synsetWords = synsetToNode.get(synset);
                    for (Node currWord : synsetWords) {
                        if (currWord.getWord().equals(word)) {
                            for (Node child : currWord.getSynsetChildren()) {
                                hyponyms.add(child.getWord());
                            }
                        }
                    }
                }
            }
        }

         */
        return List.copyOf(hyponyms);
    }

    public static void main (String[] args) {
        Graph testGraph = new Graph();
        System.out.println(testGraph.getHyponyms("event"));
    }
}
