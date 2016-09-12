package io.github.pureza.warbots.search;

import io.github.pureza.warbots.collection.Graph;
import org.junit.Test;
import io.github.pureza.warbots.geometry.Point;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static io.github.pureza.warbots.geometry.Point.pt;

public class ManhattanHeuristicTest {

    @Test
    public void estimateReturnsZeroForCurrentLocation() {
        Graph<Point, Double> graph = new Graph<>();
        graph.add(pt(0, 0));

        FindTargetCondition<Point> goal = new FindTargetCondition<>(graph, pt(0, 0));
        assertThat(new ManhattanHeuristic().estimate(graph, pt(0, 0), goal, 0), is(0.0));
    }


    @Test
    public void estimateReturnsManhattanDistance() {
        Graph<Point, Double> graph = new Graph<>();
        graph.add(pt(0, 0));

        FindTargetCondition<Point> goal = new FindTargetCondition<>(graph, pt(0, 0));
        assertThat(new ManhattanHeuristic().estimate(graph, pt(10, -5), goal, 0), is(15.0));
    }


    @Test
    public void estimateAddsCostSoFar() {
        Graph<Point, Double> graph = new Graph<>();
        graph.add(pt(0, 0));

        FindTargetCondition<Point> goal = new FindTargetCondition<>(graph, pt(0, 0));
        assertThat(new ManhattanHeuristic().estimate(graph, pt(-5, 10), goal, 100), is(115.0));
    }
}
