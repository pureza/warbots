package io.github.pureza.warbots.search;

import io.github.pureza.warbots.collection.Graph;

import java.util.function.Predicate;


/**
 * Graph search using the Dijkstra algorithm
 * Implemented using A* with h() = 0
 *
 * @param <V> The type of the vertices in the graph.
 */
public class DijkstraSearch<V> {

    /** The A* algorithm is internally used by the Dijkstra */
    private AStarSearch<V> aStar;


    public DijkstraSearch(Graph<V, Double> graph, V source, Predicate<V> terminationCondition) {

        // The null heuristic: h() = 0
        Heuristic<V> nullHeuristic = (graph1, current, condition, costSoFar) -> costSoFar;
        this.aStar = new AStarSearch<V>(graph, source, terminationCondition, nullHeuristic);
    }


    /**
     * Performs the search
     *
     * Returns the path found.
     *
     * @throws NoPathFoundException when it is unable to find a path
     */
    public Path<V> search() throws NoPathFoundException {
        return aStar.search();
    }
}
