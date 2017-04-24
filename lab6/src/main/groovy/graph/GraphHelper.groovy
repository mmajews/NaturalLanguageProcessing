package graph

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class GraphHelper {
    static final Logger logger = LoggerFactory.getLogger(GraphHelper.class)

    static test(List<List<String>> elements, Set<String> stopList, String toFindSimilar, int k) {
        Set<String> terms = new HashSet<>()
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

}
