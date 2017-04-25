package graph

class Graph {
    Map<String, Vertex> vertexMap = new HashMap<>()
    int k
    Set<String> stopList

    Graph(Set<String> vertexes, int k, Set<String> stopList) {
        def id = 0
        vertexes.forEach {
            vertex ->
                vertexMap.put(vertex, new Vertex<String>(id, vertex))
                id++
        }
        this.k = k
        this.stopList = stopList
    }

    def createConnections(List<List<String>> lists) {
        lists.forEach {
            document ->
                def filtered = filterThroughStopList(document)
                for (def i = 0; i + k < filtered.size(); i++) {
                    def sublist = filtered.subList(i, i + 1 + k)
                    def from = sublist.get(0)
                    sublist.forEach {
                        connectTo -> vertexMap.get(from).addNeighbour(vertexMap.get(connectTo))
                    }
                }
        }
    }

    List<String> filterThroughStopList(List<String> strings) {
        def filtered = new ArrayList<String>()
        strings.forEach {
            el ->
                if (!stopList.contains(el)) {
                    filtered.add(el)
                }
        }
        return filtered
    }

    Vertex getVertexBasedOnString(String text){
        return vertexMap.get(text)
    }
}
