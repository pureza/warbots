package io.github.pureza.warbots.search;

import io.github.pureza.warbots.collection.Graph;
import org.junit.Test;

import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class FindTargetConditionTest {

    /*
     * FindTargetCondition(Graph<V, ?> graph, V target)
     */

    @Test(expected=NoSuchElementException.class)
    public void constructorFailsIfTargetDoesNotExist() {
        Graph<Integer, Void> graph = new Graph<>();
        new FindTargetCondition<>(graph, 1);
    }


    /*
     * boolean test(AStarSearch.State state)
     */

    @Test
    public void testReturnsTrueIfTargetReached() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.add(2);

        assertThat(new FindTargetCondition<>(graph, 1).test(1), is(true));
    }


    @Test
    public void testReturnsFalseIfTargetNotReached() {
        Graph<Integer, Void> graph = new Graph<>();
        graph.add(1);
        graph.add(2);

        assertThat(new FindTargetCondition<>(graph, 1).test(2), is(false));
    }
}
