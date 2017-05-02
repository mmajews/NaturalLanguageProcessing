package graph

import com.google.common.base.Preconditions
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.stream.Collectors

class GraphHelper {
    static final Logger logger = LoggerFactory.getLogger(GraphHelper.class)

    static test(List<List<String>> elements, Set<String> stopList, String toFindSimilar, int k) {
//        Set<String> terms = new HashSet<>()
//        terms.addAll(getTermsForString(toFindSimilar))
//
//        elements.forEach {
//            list ->
//                list.forEach {
//                    element ->
//                        if (!stopList.contains(element)) {
//                            terms.add(element)
//                        }
//                }
//        }
//
//        Graph graph = new Graph(terms, k, stopList);

        Map<Integer, Graph> intToGraph = new HashMap<>()
        Map<Integer, String> intToText = new HashMap<>()
        def count = 1
        for (List<String> element : elements) {
            Graph newGraph = new Graph(element.toSet(), 5, stopList)
            newGraph.createConnections(element)
            intToGraph.put(count, newGraph)
            intToText.put(count, element.stream().collect(Collectors.joining(" ")))
            count++
        }

//        logger.info("Vertexes created")
//        graph.createConnections(elements)
//        logger.info("Finished creating connections!")


        def toCompare = 12
        def svmVectorOfText = countSVMVector(intToText.get(toCompare), intToGraph.get(toCompare), stopList)
        logger.info("SVM vector of text to find found")
        def bestCosineSimilarity = 0d

        def count1 = 1
        def similarities = new TreeMap<Double, Integer>()
        for (List<String> note : elements) {
            def svmVectorOfNote = countSVMVector(note.stream().collect(Collectors.joining(" ")), intToGraph.get(count1), stopList)
            def cosineSimilarity = cosineSimilarity(svmVectorOfText, svmVectorOfNote)
//            logger.info("$cosineSimilarity")
            similarities.put(cosineSimilarity, count1)
            count1++
        }

        def it = similarities.descendingMap().entrySet().iterator()
        for (int i = 0; i < 10; i++) {
            logger.info(it.next().getValue() as String)
        }
        logger.info("Finished")
    }

    static getTermsForString(String el) {
        el.split("\\s+").toList().stream().map {
            var -> var.toUpperCase().trim().replaceAll("\\p{P}", "")
        }.collect(Collectors.toList())
    }

    static List<Integer> countSVMVector(String text, Graph graph, Set<String> stopList) {
        List<String> terms = getTermsForString(text)
        terms = terms.stream().filter {
            el -> !stopList.contains(el)
        }.collect(Collectors.toList())

        ArrayList<Integer> vector = getSVMVectorFromTerms(terms, graph)
        return vector
    }


    private static ArrayList<Integer> getSVMVectorFromTerms(List terms, Graph graph) {
        List<Integer> vector = new ArrayList<Integer>()

        for (def i = 0; i < terms.size(); i++) {
            String currTerm = terms.get(i)
            def vertex = graph.getVertexBasedOnString(currTerm)

            Preconditions.checkNotNull(currTerm, "Term should be present in graph $currTerm")
            for (def j = 0; j < terms.size(); j++) {
                def connectToVertex = graph.getVertexBasedOnString(terms.get(j))
                vector.add(vertex.getNumberOfConnectionsToVertex(connectToVertex))
            }
        }
        vector
    }

    private static double cosineSimilarity(List<Integer> v1, List<Integer> v2) {
        double dotProduct = 0d
        double normA = 0d
        double normB = 0d

        if (v1.size() > v2.size()) {
            while (v2.size() != v1.size()) {
                v2.add(0)
            }
        } else {
            while (v2.size() != v1.size()) {
                v1.add(0)
            }
        }

        for (int i = 0; i < v1.size(); i++) {
            dotProduct += v1[i] * v2[i]
            normA += Math.pow(v1[i], 2)
            normB += Math.pow(v2[i], 2)
        }

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB))
    }

}
