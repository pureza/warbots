package io.github.pureza.warbots.search;


import java.util.function.Predicate;

/**
 * Exception thrown by the graph searching module when it is unable to find
 * a path
 */
public class NoPathFoundException extends Exception {

    public NoPathFoundException(Object sourceKey, Predicate<?> condition) {
        super("No path found from " + sourceKey + " with termination condition " + condition);
    }
}
