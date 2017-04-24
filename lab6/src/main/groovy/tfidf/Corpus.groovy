package tfidf

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap

class Corpus {

    private List<Document> documents
    private Multimap<String, Document> termToDocument = ArrayListMultimap.create()

    Corpus(List<Document> documents) {
        this.documents = documents
        populateTermToDocument(documents)
    }

    private populateTermToDocument(List<Document> documents) {
        documents.forEach {
            document ->
                def terms = document.getTerms()
                terms.forEach {
                    term ->
                        termToDocument.put(term, document)
                }
        }
    }

    double getInverseDocumentFrequency(String term) {
        if (termToDocument.containsKey(term.toUpperCase())) {
            double size = documents.size()
            double documentFrequency = termToDocument.get(term).size()
            Math.log10(size / documentFrequency)
        } else {
            0d
        }
    }

    Set<String> getTerms() {
        termToDocument.keySet()
    }

    List<Document> getDocuments() {
        documents
    }
}
