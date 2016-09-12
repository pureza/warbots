package io.github.pureza.warbots.search;

import io.github.pureza.warbots.collection.Graph;

import java.util.NoSuchElementException;
import java.util.function.Predicate;

/**
 * Terminate the search when the target is found
 */
public class FindTargetCondition<V> implements Predicate<V> {

    /** The target */
    private final V target;


    public FindTargetCondition(Graph<V, ?> graph, V target) {
        this.target = target;

        if (!graph.contains(target)) {
            throw new NoSuchElementException(String.valueOf(target));
        }
    }


    @Override
    public boolean test(V current) {
        return current.equals(target);
    }


    public V target() {
        return target;
    }
}
