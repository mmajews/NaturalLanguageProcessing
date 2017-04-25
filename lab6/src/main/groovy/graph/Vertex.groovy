package graph

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap

class Vertex<E> {
    int id
    Map<Vertex, Integer> neighbours = new HashMap<>()
    E content

    Vertex(int id, E content) {
        this.id = id
        this.content = content
    }

    void addNeighbour(Vertex neighbour) {
        def value = neighbours.putIfAbsent(neighbour, 1)
        if (value != null) {
            value++
            neighbours.put(neighbour, value)
        }
    }

    int getNumberOfConnectionsToVertex(Vertex vertex) {
        if (!neighbours.containsKey(vertex)) {
            return 0
        } else {
            return neighbours.get(vertex)
        }
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Vertex vertex = (Vertex) o

        if (id != vertex.id) return false

        return true
    }

    int hashCode() {
        return id
    }
}
