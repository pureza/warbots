package io.github.pureza.warbots.navigation;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.search.Path;

/**
 * The Path Planner is the component of the Bot that handles path finding
 */
public class PathPlanner {

    /** The bot this planner is a part of */
    private Bot bot;

    /** The game map */
    private Map map;


    public PathPlanner(Bot bot) {
        this.bot = bot;
        this.map = bot.getGame().getMap();
    }


    /**
     * Finds a path from the bot's current location to some other target
     * location
     */
    public Path<Point> findPathTo(Point target) {
        Point source = bot.getLocation();

        // If the bot can move to the target location in a straight line, use that
        if (map.canMoveBetween(source, target, bot.getBoundingRadius())) {
            return new Path<>(source, target);
        }

        // Find the nodes closest to the source and target position
        Point closestSource = map.cellAt(source).getCenter();
        Point closestTarget = map.cellAt(target).getCenter();

        // Computes the path
        Path<Point> path = map.findPath(closestSource, closestTarget);

        // Prepend the source and append the target locations
        path.prepend(source);
        path.append(target);

        // Smooth the path
        return this.smoothPath(path);
    }


    /**
     * Smooths the path, removing unneeded intermediate points
     *
     * Doesn't change the argument.
     */
    Path<Point> smoothPath(Path<Point> path) {
        Path<Point> smoothed = new Path<>();

        // Prepend the source position
        smoothed.append(path.getSource());

        // For each point, check if we can go straight from the previous to the
        // next without passing there. In this context, 'previous' is the
        // previous point in the smoothed path, not the original one
        for (int i = 1; i < path.size() - 1; i++) {
            Point previous = smoothed.getTarget();
            Point current = path.getLocations().get(i);
            Point next = path.getLocations().get(i + 1);

            // Can't go from the previous to the next in a straight line, so we
            // must pass by the current point
            if (!map.canMoveBetween(previous, next, bot.getBoundingRadius())) {
                smoothed.append(current);
            }
        }

        // Append the target
        smoothed.append(path.getTarget());

        return smoothed;
    }
}
