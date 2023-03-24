package ngordnet.main;

import edu.princeton.cs.algs4.In;
import ngordnet.ngrams.NGramMap;

import java.util.*;

public class Graph {
    private class Node {
        private static int synsetID;
        private String word;
        private List<Node> synsetChildren;
        public Node(int id, String thisWord, List<Node> children) {
            synsetID = id;
            word = thisWord;
            synsetChildren = children;
        }
        public List<Node> getSynsetChildren() {
            return synsetChildren;
        }
        public String getWord() {
            return word;
        }
    }

    // Instance variables
    public String synsetFilePath = "./data/wordnet/synsets14.txt";
    public Map<String, List<Integer>> wordIDMap;
    public Map<Integer, List<Node>> synsetToWords;
    public String hyponymFilePath;
    public NGramMap ngm;

    public Graph () {
        wordIDMap = new HashMap<>();
        synsetToWords = new HashMap<>();
        synsetInput(synsetFilePath);
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
            String[] words = currentLine[1].split(" "); // need some code to turn each word into a node object
            // adds synset id mapped to list of the words it represents.
            synsetToWords.put(Integer.parseInt(currentLine[0]), Arrays.asList(words));
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

    public ArrayList<String> getHyponyms(String word) {
        ArrayList<String> hyponyms = new ArrayList<>();
        for (String wordKey : wordIDMap.keySet()) {
            if (wordKey.equals(word)) {
                List<Integer> synsets = wordIDMap.get(wordKey);
                for (int synset : synsets) {
                    List<Node> synsetWords = synsetToWords.get(synset);
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
        return hyponyms;
    }
}
