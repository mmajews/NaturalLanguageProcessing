package graph

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.stream.Collectors

class GraphHelper {
    static final Logger logger = LoggerFactory.getLogger(GraphHelper.class)

    static test(List<List<String>> elements, Set<String> stopList, String toFindSimilar, int k) {
        Set<String> terms = new HashSet<>()
        terms.addAll(getTermsForString(toFindSimilar))

        elements.forEach{
            list -> list.forEach{
                element ->
                    if(!stopList.contains(element)){
                        terms.add(element)
                    }
            }
        }

        Graph graph = new Graph(terms, k, stopList);
        logger.info("Vertexes created")
        graph.createConnections(elements)
        logger.info("Finished creating connections!")
    }

    static getTermsForString(String el) {
        el.split("\\s+").toList().stream().map {
            var -> var.toUpperCase().trim().replaceAll("\\p{P}", "")
        }.collect(Collectors.toList())
    }

}
