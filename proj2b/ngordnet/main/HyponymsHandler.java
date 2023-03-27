package ngordnet.main;

import ngordnet.browser.NgordnetQuery;
import ngordnet.browser.NgordnetQueryHandler;
import ngordnet.ngrams.NGramMap;

import java.util.List;

public class HyponymsHandler extends NgordnetQueryHandler {
    NGramMap ngm;
    String synsetFile;
    String hyponymFile;
    Graph testGraph;
    public HyponymsHandler(NGramMap ngm, String synsetFile, String hyponymFile) {
        this.ngm = ngm;
        this.synsetFile = synsetFile;
        this.hyponymFile = hyponymFile;
        testGraph = new Graph(synsetFile, hyponymFile, ngm);
    }

    @Override
    public String handle(NgordnetQuery q) {
        List<String> wordList = q.words();
        int endYear = q.endYear();
        int startYear = q.startYear();
        int k = q.k();

        if (k == 0) {
            return testGraph.getHyponymsMultipleWords(wordList).toString();
        } else {
            return testGraph.getHyponymsK(wordList, k, startYear, endYear).toString();
        }
    }
}
