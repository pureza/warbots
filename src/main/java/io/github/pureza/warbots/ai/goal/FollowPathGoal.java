package io.github.pureza.warbots.ai.goal;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.search.Path;


/**
 * Instructs the bot to follow a path, step by step
 *
 * Internally, it uses the seek steering behavior to go from one step to the
 * following.
 */
public class FollowPathGoal extends Goal {

    /** The path to follow */
    private final Path<Point> path;

    /** Index of the current point of the path the bot is trying to reach */
    private int currentTargetIdx = 0;

    /** The goal to reach the current point */
    private Goal currentSegmentGoal;


    public FollowPathGoal(Bot bot, Path<Point> path) {
        super(bot);
        this.path = path;
    }


    @Override
    public State update(long dt) {
        State segmentState = currentSegmentGoal.update(dt);
        switch (segmentState) {
            case COMPLETED:
                // We have reached the current target
                // If this was the last point of the path, terminate...
                if (isLastSegment()) {
                    return State.COMPLETED;
                }

                // ... otherwise, pursue the next point!
                currentSegmentGoal = getNextSegmentGoal();
                currentSegmentGoal.start();
                return State.ACTIVE;
            default:
                // For ACTIVE or FAILED, just return the state itself
                return segmentState;
        }
    }


    @Override
    public void start() {
        // Create the goal to reach the first segment
        this.currentSegmentGoal = getNextSegmentGoal();
        this.currentSegmentGoal.start();
    }


    @Override
    public void stop() {
        if (currentSegmentGoal != null) {
            // Stop the bot
            currentSegmentGoal.stop();
        }
    }


    private Goal getNextSegmentGoal() {
        Point next = path.get(currentTargetIdx);
        currentTargetIdx++;

        // Use the Seek steering behavior to reach all the other points
        return new SeekToPositionGoal(bot, next);
    }


    /**
     * Checks if the bot is trying to reach the end of the path
     */
    private boolean isLastSegment() {
        return currentTargetIdx == path.size();
    }


    @Override
    public String toString() {
        return "path";
    }
}
