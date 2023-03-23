package ngordnet.main;

import edu.princeton.cs.algs4.In;
import ngordnet.ngrams.NGramMap;

import java.util.*;

public class Graph {
    private class Node {
        private static int synsetID;
        private ArrayList<Node> synsetChildren;
        public Node(int id, ArrayList<Node> children) {
            synsetID = id;
            synsetChildren = children;
        }
        public ArrayList<Node> getSynsetChildren() {
            return synsetChildren;
        }
    }

    // Instance variables
    public String synsetFilePath = "./data/wordnet/synsets14.txt";
    public Map<String, List<Integer>> wordIDMap;
    public Map<Integer, List<String>> synsetToWords;
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
            String[] words = currentLine[1].split(" ");
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
                    // for some reason I thought the words were stored as nodes, so it 'd be easy to find the children
                    // since it isn't I'm gonna have to think about this again (I'll be back)
                    List<String> synsetWords = synsetToWords.get(synset);
                }
            }
        }
    }
}
