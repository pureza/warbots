package io.github.pureza.warbots.search;

import io.github.pureza.warbots.collection.Graph;

import java.util.function.Predicate;

/**
 * An heuristic used by the graph search algorithm to estimate path costs
 *
 * @param <V> The type of vertices in the graph
 */
public interface Heuristic<V> {

    /**
     * Estimates the total cost for the search to terminate, assuming the
     * resulting path visits some given vertex
     */
    double estimate(Graph<V, Double> graph, V current,
                    Predicate<V> terminationCondition, double costSoFar);
}
