package io.github.pureza.warbots.search;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;

/**
 * A path is an ordered list of locations
 *
 * @param <T> The type of the locations
 */
public class Path<T> {

    /** The empty path. Immutable. */
    private static final Path EMPTY_PATH = new Path() {

        @Override
        public void prepend(Object wayPoint) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void append(Object wayPoint) {
            throw new UnsupportedOperationException();
        }
    };


    /**
     * Returns an instance representing an empty path.
     *
     * This instance is immutable.
     */
    public static <T> Path<T> emptyPath() {
        return EMPTY_PATH;
    }


    /** The locations that make up the path */
    private LinkedList<T> locations;


    public Path() {
        this(new LinkedList<>());
    }


    @SafeVarargs
    public Path(T... locations) {
        this.locations = new LinkedList<>(asList(locations));
    }


    public Path(List<T> locations) {
        this.locations = new LinkedList<>(locations);
    }


    /**
     * Adds a new location to the beginning of the path
     */
    public void prepend(T location) {
        this.locations.addFirst(location);
    }


    /**
     * Adds a new location to the end of the path.
     */
    public void append(T location) {
        this.locations.addLast(location);
    }


    /**
     * Returns the locations that make up the path
     */
    public List<T> getLocations() {
        return unmodifiableList(this.locations);
    }


    /**
     * Returns the source of the path (i.e., the first location)
     */
    public T getSource() {
        return locations.getFirst();
    }


    /**
     * Returns the target of the path (i.e., the last location)
     */
    public T getTarget() {
        return locations.getLast();
    }


    /**
     * Returns the ith location
     */
    public T get(int i) {
        return locations.get(i);
    }


    /**
     * Returns the number of locations in this path
     */
    public int size() {
        return locations.size();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Path)) return false;
        Path<?> path = (Path<?>) o;
        return Objects.equals(locations, path.locations);
    }


    @Override
    public int hashCode() {
        return Objects.hash(locations);
    }


    @Override
    public String toString() {
        return "[" + locations + ']';
    }
}
