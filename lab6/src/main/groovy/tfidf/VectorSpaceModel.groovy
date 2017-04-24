package tfidf

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
                logger.debug("Number of processed $curr")
                map.put(document, weights)
        }
        map
    }


    private double getMagnitude(Document document) {
        double magnitude = 0;
        Map<String, Double> weights = tfIDfWeights.get(document);

        for (double weight : weights.values()) {
            magnitude += weight * weight;
        }

        return Math.sqrt(magnitude);
    }

    private double getDotProduct(Document d1, Document d2) {
        double product = 0;
        Map<String, Double> weights1 = tfIDfWeights.get(d1);
        Map<String, Double> weights2 = tfIDfWeights.get(d2);

        for (String term : weights1.keySet()) {
            product += weights1.get(term) * weights2.get(term);
        }
        return product;
    }

    double cosineSimilarity(Document d1, Document d2) {
        return getDotProduct(d1, d2) / (getMagnitude(d1) * getMagnitude(d2));
    }
}
