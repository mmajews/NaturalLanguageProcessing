package tfidf

import java.util.stream.Collectors

class Document {
    private List<String> terms
    private Set<String> stopList
    private int id
    private def frequency = new HashMap<>()

    Document(List<String> terms, int id, Set<String> stopList) {
        this.terms = terms
        this.id = id
        this.stopList = stopList

        this.terms = this.terms.stream().filter {
            el -> !stopList.contains(el)
        }.collect(Collectors.toList())

        processTerms(this.terms)
    }

    private processTerms(List<String> terms) {
        terms.forEach {
            el ->
                if (frequency.containsKey(el)) {
                    frequency.put(el, frequency.get(el) + 1)
                } else {
                    frequency.put(el, 1)
                }
        }
    }


    Set<String> getTerms() {
        this.frequency.keySet()
    }

    double getTermFrequency(String term) {
        if (frequency.containsKey(term)) {
            return frequency.get(term) as double
        } else {
            return 0d
        }
    }

    int getId() {
        id
    }
}
