package io.github.pureza.warbots.search;

import io.github.pureza.warbots.collection.Graph;

import java.util.*;
import java.util.function.Predicate;


/**
 * Implementation of the A* graph search algorithm
 *
 * @param <V> The type of the vertices in the graph.
 */
public class AStarSearch<V> {

    /** The graph to search */
    private final Graph<V, Double> graph;

    /** The vertex where the search starts */
    private final V source;

    /** The termination condition */
    private final Predicate<V> terminationCondition;

    /** The heuristic to estimate the cost left */
    private final Heuristic<V> heuristic;


    public AStarSearch(Graph<V, Double> graph, V source, Predicate<V> terminationCondition,
                       Heuristic<V> heuristic) {
        this.graph = graph;
        this.source = source;
        this.terminationCondition = terminationCondition;
        this.heuristic = heuristic;

        if (!graph.contains(source)) {
            throw new NoSuchElementException(String.valueOf(source));
        }
    }


    /**
     * Performs the search
     *
     * Returns the path found.
     *
     * @throws NoPathFoundException when it is unable to find a path
     */
    public Path<V> search() throws NoPathFoundException {
        // Set of already expanded nodes
        Set<V> expanded = new HashSet<>();

        IndexedPriorityQueue<V> queue = new IndexedPriorityQueue<>();

        // Add the initial state to the queue
        queue.offer(new State<>(source, null, 0.0, 0.0));

        // Perform the search
        State<V> last = searchHelper(queue, expanded, this.heuristic);
        if (last == null) {
            throw new NoPathFoundException(this.source, this.terminationCondition);
        }

        // Construct a path from the last state of the search
        Path<V> path = new Path<>();
        State<V> current = last;
        while (current != null) {
            // Add the edge to the list
            path.prepend(current.vertex());
            current = current.previousState();
        }

        return path;
    }


    /**
     * Helper method that actually performs the search
     *
     * Returns the final state or null if the path can't be found.
     */
    private State<V> searchHelper(IndexedPriorityQueue<V> queue, Set<V> expanded, Heuristic<V> heuristic) {
        while (!queue.isEmpty()) {
            // The state with the lowest estimate is at the front of the queue
            State<V> best = queue.poll();

            // Found target. Return this last state
            if (this.terminationCondition.test(best.vertex())) {
                return best;
            }

            // Add this vertex to the list of already expanded vertex
            expanded.add(best.vertex());

            // For each non-expanded vertex connected to the current vertex,
            // create new States and add them to the queue. If there are other
            // States already present in the queue for the same node, replace
            // them if the estimated cost is lower.
            for (State<V> state : expandNode(best, expanded, heuristic)) {
                int index = queue.indexOf(state.vertex());

                if (index == -1) {
                    queue.offer(state);
                } else {
                    State<V> previous = queue.get(index);

                    // If the new cost is lower, replace it
                    if (state.costSoFar() < previous.costSoFar()) {
                        queue.update(index, state);
                    }
                }
            }
        }

        return null;
    }


    /**
     * Expand a vertex, creating State objects for each vertex connected to it
     *
     * Ignore neighbors that have already been expanded.
     * Returns a list with the new states.
     */
    private List<State<V>> expandNode(State<V> state, Set<V> expanded, Heuristic<V> heuristic) {
        List<State<V>> states = new LinkedList<>();

        // For each edge at the current node...
        graph.edgesAt(state.vertex()).forEach((neighbour, edgeCost) -> {
            if (!expanded.contains(neighbour)) {
                double costSoFar = state.costSoFar() + edgeCost;
                double estimatedCost = heuristic.estimate(graph, neighbour, terminationCondition, costSoFar);
                states.add(new State<>(neighbour, state, costSoFar, estimatedCost));
            }
        });

        return states;
    }


    /**
     * Represents a state of the search procedure
     */
    public static class State<V> implements Comparable<State<V>> {

        /** The current vertex */
        private final V vertex;

        /** The previous state */
        private final State<V> previousState;

        /** The cost so far */
        private final double costSoFar;

        /** The estimated cost to the target at this point */
        private final double estimatedCost;


        public State(V vertex, State<V> previousState, double costSoFar, double estimatedCost) {
            this.vertex = vertex;
            this.previousState = previousState;
            this.costSoFar = costSoFar;
            this.estimatedCost = estimatedCost;
        }


        /**
         * Compares states by the estimated cost field
         *
         * This way, when inserted into a sorted data structure, States will be
         * sorted by the estimated cost in ascending order.
         */
        public int compareTo(State other) {
            return Double.compare(estimatedCost, other.estimatedCost);
        }


        public V vertex() {
            return vertex;
        }


        public State<V> previousState() {
            return previousState;
        }


        public double costSoFar() {
            return costSoFar;
        }


        public double estimatedCost() {
            return estimatedCost;
        }
    }
}
