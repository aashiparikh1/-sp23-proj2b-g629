package ngordnet.main;

import ngordnet.browser.NgordnetQuery;
import ngordnet.browser.NgordnetQueryHandler;
import ngordnet.ngrams.NGramMap;

import java.util.List;

public class HyponymHandler extends NgordnetQueryHandler {
    NGramMap ngm;
    String synsetFile;
    String hyponymFile;
    public HyponymHandler (NGramMap ngm, String synsetFile, String hyponymFile) {
        this.ngm = ngm;
        this.synsetFile = synsetFile;
        this.hyponymFile = hyponymFile;
    }
    @Override
    public String handle(NgordnetQuery q) {
        List<String> wordList = q.words();
        int endYear = q.endYear();
        int startYear = q.startYear();
        int k = q.k();

        Graph testGraph = new Graph(synsetFile, hyponymFile);
        System.out.println(testGraph.getHyponymsMultipleWords(wordList));
        return testGraph.getHyponymsMultipleWords(wordList).toString();
    }
}