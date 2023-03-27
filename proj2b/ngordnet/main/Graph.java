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
        public List<String> getWords() {
            return words;
        }
    }

    // Instance variables
    // public String synsetFilePath = "./data/wordnet/synsets.txt";
    public Map<String, List<Integer>> wordIDMap;
    public Map<Integer, Node> synsetToNode;
    public Map<Integer, List<String>> synsetToWord;
    // public String hyponymFilePath = "./data/wordnet/hyponyms.txt";
    public NGramMap ngm;

    public Graph (String synsetFilePath, String hyponymFilePath, NGramMap ngm) {
        wordIDMap = new HashMap<>();
        synsetToNode = new HashMap<>();
        synsetToWord = new HashMap<>();
        this.ngm = ngm;

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
                for (int i : childrenInt) {
                    Node child;
                    if (!synsetToNode.containsKey(i)) {
                        child = new Node(i, synsetToWord.get(i));
                    } else {
                        child = synsetToNode.get(i);
                    }
                    synsetToNode.get(Integer.parseInt(currentLine[0])).getSynsetChildren().add(child);
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
            tempSet.addAll(n.getWords());
            for (Node s : n.getSynsetChildren()) {
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
        return List.copyOf(hyponyms);
    }

    public List<String> getHyponymsMultipleWords (List<String> words) {
        // this hyponyms set later gets converted to a list and is what is ultimately returned
        Set<String> hyponyms = new TreeSet<>();
        // hyponymFreq maps each hyponym (of each word in words) to an int frequency value so we know how many times
        // that hyponym shows up among all the words
        Map<String, Integer> hyponymFreq = new TreeMap<>();
        for (String word : words) {
            // list of all the hyponyms of the current word
            List<String> curr_hyponyms = getHyponyms(word);
            if (words.size() == 1) {
                return curr_hyponyms;
            }
            for (String hyponym : curr_hyponyms) {
                // if the current hyponym doesn't exist in the hyponymFreq map, add it and set the frequency value to 1
                if (!hyponymFreq.containsKey(hyponym)) {
                    hyponymFreq.put(hyponym, 1);
                // otherwise, increase the frequency value for current hyponym by 1
                } else {
                    hyponymFreq.replace(hyponym, hyponymFreq.get(hyponym), hyponymFreq.get(hyponym) + 1);
                }
            }
        }
        // to the final hyponyms set, add only the hyponyms with frequency = size of words list (meaning they're
        // found in all words' hyponym lists)
        for (String key : hyponymFreq.keySet()) {
            if (hyponymFreq.get(key) == words.size()) {
                hyponyms.add(key);
            }
        }
        return List.copyOf(hyponyms);
    }

    public List<String> getHyponymsK (List<String> words, int k, int startYear, int endYear) {
        Map<String, Double> freqMap = new TreeMap<>();
        List<String> hyponyms = getHyponymsMultipleWords(words);
        String leastCommonWord = "";

        for (String word : hyponyms) {
            List<Double> data = ngm.countHistory(word, startYear, endYear).data();
            double totalFreq = 0;
            for (double i : data) {
                totalFreq += i;
            }
            if (totalFreq != 0) {
                freqMap.put(word, totalFreq);
                if (freqMap.size() > k) {
                    List<String> keyList = List.copyOf(freqMap.keySet());
                    leastCommonWord = keyList.get(0);
                    for (String key : keyList) {
                        if (freqMap.get(key) < freqMap.get(leastCommonWord)) {
                            leastCommonWord = key;
                        }
                    }
                    freqMap.remove(leastCommonWord);
                }
            }
        }
        return List.copyOf(freqMap.keySet());
    }

    //changed arguments for graph class so commented out your code. I think we can test from hyponymHandler class now.
    public static void main (String[] args) {
        /*
        Graph testGraph = new Graph("./data/wordnet/synsets.txt", "./data/wordnet/hyponyms.txt);
        List<String> testList = new ArrayList<>();
        testList.add("tart");
        testList.add("pastry");
        System.out.println(testGraph.getHyponymsMultipleWords(testList));

         */
        //System.out.println(testGraph.getHyponyms("video"));
        //System.out.println(testGraph.getHyponyms("recording"));
    }
}
