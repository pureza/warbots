package io.github.pureza.warbots.util;

import java.util.Objects;

/**
 * A pair
 *
 * @param <A> The type of the first element of the pair
 * @param <B> The type of the second element of the pair
 */
public class Pair<A, B> {

    /** The first element of the pair */
    private final A first;

    /** The second element of the pair */
    private final B second;


    public static <A, B> Pair<A, B> of(A first, B second) {
        return new Pair<>(first, second);
    }


    private Pair(A first, B second) {
        this.first = first;
        this.second = second;
    }


    public A first() {
        return first;
    }


    public B second() {
        return second;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }


    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }


    @Override
    public String toString() {
        return '(' +
                String.valueOf(first) +
                ", " +
                String.valueOf(second) +
                ')';
    }
}
