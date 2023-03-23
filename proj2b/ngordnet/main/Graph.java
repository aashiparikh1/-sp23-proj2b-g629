package ngordnet.main;

import edu.princeton.cs.algs4.In;
import ngordnet.ngrams.NGramMap;

import java.util.*;

public class Graph {
    private class Node {
        private static int synsetID;
        private List<Node> synsetChildren;
        public Node(int id, List<Node> children) {
            synsetID = id;
            synsetChildren = children;
        }
        public List<Node> getSynsetChildren() {
            return synsetChildren;
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
            String[] words = currentLine[1].split(" ");
            // adds synset id mapped to list of the words it represents.
            synsetToWords.put(Integer.parseInt(currentLine[0]), Arrays.asList(words)); // having a hard time doing this in node form
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
                        if (currWord.equals(word)) { //ok I know this isn't right, but I'm getting tired, gotta look tomorrow
                            for (String child : currWord.getSynsetChildren()) { // obviously this is wrong too, tomorrow I'll add a way to get the name from the node
                                hyponyms.add(child);
                            }
                        }
                    }
                }
            }
        }
        return hyponyms;
    }
}
