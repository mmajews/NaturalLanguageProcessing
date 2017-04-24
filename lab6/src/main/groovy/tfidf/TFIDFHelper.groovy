package tfidf

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import static tfidf.TermsHelper.getTermsForString

class TFIDFHelper {
    private static final Logger logger = LoggerFactory.getLogger(TFIDFHelper.class)

    static tfidfTest(List<List<String>> sliced, Set<String> stopList, String toFindSimilar) {
        Document documentToCompare = createDocumentToFindSimilarFor(sliced.size() + 1, stopList, toFindSimilar)
        ArrayList<Document> documents = createDocumentsList(sliced, stopList)
        documents.add(documentToCompare)

        def corpus = new Corpus(documents)
        def vectorSpace = new VectorSpaceModel(corpus)

        def max = 0
        Document bestDoc
        for (Document document : documents) {
            if (document.getId() != documentToCompare.getId()) {
                def similarity = vectorSpace.cosineSimilarity(documentToCompare, document)
                logger.info("Similarity for $document.id : $similarity")
                if (similarity > max) {
                    max = similarity
                    bestDoc = document
                }
            }
        }
        logger.info("Best doc is " + bestDoc.getId())

    }

    private static createDocumentsList(List<List<String>> sliced, Set<String> stopList) {
        def id = 1
        def documents = new ArrayList<Document>()
        sliced.forEach {
            el ->
                documents.add(new Document(el, id, stopList))
                id++
        }
        documents
    }

    private static createDocumentToFindSimilarFor(int id, Set<String> stopList, String toFindSimilar) {
        def termForSimilar = getTermsForString(toFindSimilar.toUpperCase())
        def documentToCompare = new Document(termForSimilar, id, stopList)
        documentToCompare
    }
}