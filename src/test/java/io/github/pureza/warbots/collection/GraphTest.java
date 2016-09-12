package io.github.pureza.warbots.collection;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

public class GraphTest {

    /*
     * void add(V vertex)
     */

    @Test
    public void addAddsNewVertex() {
        Graph<Integer, Void> graph = new Graph<>();

        assertThat(graph.vertices(), is(empty()));
        graph.add(1);
        assertThat(graph.vertices(), contains(1));
    }


    @Test(expected=IllegalArgumentException.class)
    public void addFailsForExistingVertex() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.add(1);
    }


    /*
     * boolean remove(V vertex)
     */


    @Test
    public void removeRemovesExistingVertex() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        assertThat(graph.remove(1), is(true));
        assertThat(graph.vertices(), is(empty()));
    }


    @Test
    public void removeReturnsFalseForNonExistingVertex() {
        Graph<Integer, Void> graph = new Graph<>();
        assertThat(graph.remove(1), is(false));
    }


    @Test
    public void removeRemovesVertexEdges() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.add(2);
        graph.addEdge(1, 2, null);

        assertThat(graph.containsEdge(1, 2), is(true));
        assertThat(graph.containsEdge(2, 1), is(true));
        assertThat(graph.remove(1), is(true));
        assertThat(graph.edgesAt(2), is(anEmptyMap()));
    }


    /*
     * boolean contains(V vertex)
     */

    @Test
    public void containsReturnsTrueIfVertexExists() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        assertThat(graph.contains(1), is(true));
    }


    @Test
    public void containsReturnsTrueIfVertexDoesNotExist() {
        Graph<Integer, Void> graph = new Graph<>();
        assertThat(graph.contains(1), is(false));
    }


    /*
     * Set<V> vertices()
     */

    @Test
    public void verticesReturnsTheEmptySetWhenThereAreNoVertices() {
        Graph<Integer, Void> graph = new Graph<>();
        assertThat(graph.vertices(), is(empty()));
    }


    @Test
    public void verticesReturnsSetOfVertices() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.add(2);
        assertThat(graph.vertices(), containsInAnyOrder(1, 2));
    }


    /*
     * void addEdge(V v, V u, E label)
     */

    @Test(expected=NoSuchElementException.class)
    public void addEdgeFailsIfFirstVertexDoesNotExist() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(2);
        graph.addEdge(1, 2, null);
    }


    @Test(expected=NoSuchElementException.class)
    public void addEdgeFailsIfSecondVertexDoesNotExist() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.addEdge(1, 2, null);
    }


    @Test(expected=IllegalStateException.class)
    public void addEdgeFailsIfEdgeAlreadyExists() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.add(2);
        graph.addEdge(1, 2, null);
        graph.addEdge(1, 2, null);
    }


    @Test
    public void addEdgeAddsNewEdge() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.add(2);
        graph.addEdge(1, 2, null);

        assertThat(graph.containsEdge(1, 2), is(true));
        assertThat(graph.containsEdge(2, 1), is(true));
    }


    @Test
    public void addEdgeAddsLoopEdge() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.addEdge(1, 1, null);

        assertThat(graph.containsEdge(1, 1), is(true));
    }


    /*
     * boolean removeEdge(V v, V u)
     */

    @Test(expected=NoSuchElementException.class)
    public void removeEdgeFailsIfFirstVertexDoesNotExist() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(2);
        graph.removeEdge(1, 2);
    }


    @Test(expected=NoSuchElementException.class)
    public void removeEdgeFailsIfSecondVertexDoesNotExist() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.removeEdge(1, 2);
    }


    @Test
    public void removeEdgeReturnsFalseIfEdgeDoesNotExist() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.add(2);
        assertThat(graph.removeEdge(1, 2), is(false));
    }


    @Test
    public void removeEdgeRemovesExistingEdge() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.add(2);
        graph.addEdge(1, 2, null);

        assertThat(graph.containsEdge(1, 2), is(true));
        assertThat(graph.containsEdge(2, 1), is(true));

        assertThat(graph.removeEdge(1, 2), is(true));

        assertThat(graph.containsEdge(1, 2), is(false));
        assertThat(graph.containsEdge(2, 1), is(false));
    }


    /*
     * boolean containsEdge(V v, V u)
     */

    @Test(expected=NoSuchElementException.class)
    public void containsEdgeFailsIfFirstVertexDoesNotExist() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(2);
        graph.containsEdge(1, 2);
    }


    @Test(expected=NoSuchElementException.class)
    public void containsEdgeFailsIfSecondVertexDoesNotExist() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.containsEdge(1, 2);
    }


    @Test
    public void containsEdgeReturnsFalseIfEdgeDoesNotExist() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.add(2);
        assertThat(graph.containsEdge(1, 2), is(false));
    }


    @Test
    public void containsEdgeReturnsTrueIfEdgeDoesNotExist() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.add(2);
        graph.addEdge(1, 2, null);
        assertThat(graph.containsEdge(1, 2), is(true));
    }


    /*
     * int size()
     */

    @Test
    public void sizeReturnsZeroWhenGraphIsEmpty() {
        Graph<Integer, Void> graph = new Graph<>();
        assertThat(graph.size(), is(0));
    }


    @Test
    public void sizeReturnsTheNumberOfVerticesInTheGraph() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.add(2);
        graph.add(3);
        assertThat(graph.size(), is(3));
    }


    /*
     * boolean isEmpty()
     */

    @Test
    public void isEmptyReturnsTrueWhenHasNoVertices() {
        Graph<Integer, Void> graph = new Graph<>();
        assertThat(graph.isEmpty(), is(true));
    }


    @Test
    public void isEmptyReturnsFalseWhenGraphHasVertices() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.add(2);
        graph.add(3);
        assertThat(graph.isEmpty(), is(false));
    }


    /*
     * Map<V, E> edgesAt(V vertex)
     */

    @Test(expected=NoSuchElementException.class)
    public void edgesAtFailsWhenVertexDoesNotExist() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.edgesAt(1);
    }


    @Test
    public void edgesAtReturnsEmptyMapWhenVertexHasNoEdges() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        assertThat(graph.edgesAt(1), is(anEmptyMap()));
    }


    @Test
    public void edgesAtReturnsEdgesAtVertex() {
        Graph<Integer, String> graph = new Graph<>();
        graph.add(1);
        graph.add(2);
        graph.add(3);
        graph.addEdge(1, 1, "1->1");
        graph.addEdge(1, 2, "1->2");
        graph.addEdge(1, 3, "1->3");
        graph.addEdge(2, 3, "2->3");

        Map<Integer, String> expected = new HashMap<>();
        expected.put(1, "1->1");
        expected.put(2, "1->2");
        expected.put(3, "1->3");

        assertThat(graph.edgesAt(1), is(expected));
    }
}
