package io.github.pureza.warbots.collection;

import java.util.*;

/**
 * A graph
 *
 * This graph is undirected.
 *
 * @param <V> stands for the data type held by a vertex
 * @param <E> stands for the data type held by an edge
 */
public class Graph<V, E> {

    /**
     * Maps vertices connected by an edge
     *
     * Since this is an undirected graph, we represent an edge between vertices
     * (a, b) as an edge from a to b and another from b to a.
     */
    private final Map<V, Map<V, E>> vertexEdges = new HashMap<>();


    /**
     * Adds a new vertex to the graph
     *
     * Fails if the vertex already exists.
     */
    public void add(V vertex) {
        if (vertexEdges.containsKey(vertex)) {
            throw new IllegalArgumentException("Vertex " + vertex + " already exists");
        }

        vertexEdges.put(vertex, new HashMap<>());
    }


    /**
     * Removes a vertex from the graph
     *
     * Returns true if the vertex was removed and false if it no such vertex
     * exists.
     */
    public boolean remove(V vertex) {
        if (!contains(vertex)) {
            return false;
        }

        // Remove the outward edges
        Map<V, E> outwardEdges = vertexEdges.remove(vertex);

        // Remove the inward edges
        outwardEdges.forEach((in, edges) -> vertexEdges.get(in).remove(vertex));

        return true;
    }


    /**
     * Checks if the given vertex exists
     */
    public boolean contains(V vertex) {
        return vertexEdges.containsKey(vertex);
    }


    /**
     * Returns the set of vertices in this graph
     */
    public Set<V> vertices() {
        return Collections.unmodifiableSet(vertexEdges.keySet());
    }


    /**
     * Creates an edge between two vertices
     *
     * Fails if either of the vertices doesn't exist or if the vertices are
     * already connected.
     */
    public void addEdge(V v, V u, E label) {
        if (!contains(v)) {
            throw new NoSuchElementException(String.valueOf(v));
        }

        if (!contains(u)) {
            throw new NoSuchElementException(String.valueOf(u));
        }

        if (containsEdge(v, u)) {
            throw new IllegalStateException("Edge between '" + v + "' and '" + u + "' already exists");
        }

        vertexEdges.get(v).put(u, label);
        vertexEdges.get(u).put(v, label);
    }


    /**
     * Removes an edge between two vertices
     *
     * Doesn't do anything if the edge does not exist.
     *
     * Returns true if the edge was removed and false otherwise.
     */
    public boolean removeEdge(V v, V u) {
        if (!contains(v)) {
            throw new NoSuchElementException(String.valueOf(v));
        }

        if (!contains(u)) {
            throw new NoSuchElementException(String.valueOf(u));
        }

        if (!containsEdge(v, u)) {
            return false;
        }

        vertexEdges.get(v).remove(u);
        vertexEdges.get(u).remove(v);

        return true;
    }


    /**
     * Checks if there is an edge between two vertices
     */
    public boolean containsEdge(V v, V u) {
        if (!contains(v)) {
            throw new NoSuchElementException(String.valueOf(v));
        }

        if (!contains(u)) {
            throw new NoSuchElementException(String.valueOf(u));
        }

        return vertexEdges.get(v).containsKey(u);
    }


    /**
     * Returns the number of vertices in this graph
     */
    public int size() {
        return vertexEdges.size();
    }


    /**
     * Checks if the graph is empty
     */
    public boolean isEmpty() {
        return size() == 0;
    }


    /**
     * Returns the edges at the given vertex
     */
    public Map<V, E> edgesAt(V vertex) {
        if (!contains(vertex)) {
            throw new NoSuchElementException(String.valueOf(vertex));
        }

        return vertexEdges.get(vertex);
    }
}
