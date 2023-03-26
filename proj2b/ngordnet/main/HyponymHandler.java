package ngordnet.main;

import ngordnet.browser.NgordnetQuery;
import ngordnet.browser.NgordnetQueryHandler;
import ngordnet.ngrams.NGramMap;

import java.util.List;

public class HyponymHandler extends NgordnetQueryHandler {
    NGramMap ngm;
    public HyponymHandler (NGramMap ngm) {
        this.ngm = ngm;
    }
    @Override
    public String handle(NgordnetQuery q) {
        List<String> wordList = q.words();
        int endYear = q.endYear();
        int startYear = q.startYear();
        int k = q.k();

        Graph testGraph = new Graph("./data/wordnet/synsets.txt", "./data/wordnet/hyponyms.txt");
        System.out.println(testGraph.getHyponymsMultipleWords(wordList));
        return testGraph.getHyponymsMultipleWords(wordList).toString();
    }
}