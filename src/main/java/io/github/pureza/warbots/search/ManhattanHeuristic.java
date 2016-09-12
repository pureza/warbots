package io.github.pureza.warbots.search;

import io.github.pureza.warbots.collection.Graph;
import io.github.pureza.warbots.geometry.Point;

import java.util.function.Predicate;


/**
 * An heuristic that estimates costs using the manhattan distance between points.
 */
public class ManhattanHeuristic implements Heuristic<Point> {

    @Override
    public double estimate(Graph<Point, Double> graph, Point current,
                           Predicate<Point> terminationCondition, double costSoFar) {
        Point target = ((FindTargetCondition<Point>) terminationCondition).target();
        return costSoFar + manhattanDistance(current, target);
    }


    /**
     * Calculates the manhattan distance between two points
     */
    private double manhattanDistance(Point pointA, Point pointB) {
        return Math.abs(pointA.x() - pointB.x()) + Math.abs(pointA.y() - pointB.y());
    }
}
