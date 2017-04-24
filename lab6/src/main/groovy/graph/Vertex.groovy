package graph

class Vertex<E> {
    int id
    Set<Vertex> neighbours = new HashSet<>()
    E content

    Vertex(int id, E content){
        this.id = id
        this.content = content
    }

    void addNeighbour(Vertex neighbour) {
        neighbours.add(neighbour)
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
