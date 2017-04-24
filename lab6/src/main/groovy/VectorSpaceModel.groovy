import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.atomic.AtomicInteger

class VectorSpaceModel {
    private static final Logger logger = LoggerFactory.getLogger(VectorSpaceModel.class)
    private Corpus corpus
    private Map<Document, Map<String, Double>> tfIDfWeights

    VectorSpaceModel(Corpus corpus) {
        this.corpus = corpus
        tfIDfWeights = createTfIdfWieghts()
    }

    Map<Document, Map<String, Double>> createTfIdfWieghts() {
        AtomicInteger i = new AtomicInteger(0)
        def map = new HashMap<Document, Map<String, Double>>()
        def terms = corpus.getTerms()

        corpus.getDocuments().parallelStream().forEach {
            document ->
                def weights = new HashMap<String, Double>()
                terms.forEach {
                    term ->
                        double tf = document.getTermFrequency(term)
                        double idf = corpus.getInverseDocumentFrequency(term)
                        double weight = tf * idf
                        weights.put(term, weight)
                }
                def curr = i.getAndAdd(1)
                logger.info("Number of processed $curr")
                map.put(document, weights)
        }
        map
    }
}
