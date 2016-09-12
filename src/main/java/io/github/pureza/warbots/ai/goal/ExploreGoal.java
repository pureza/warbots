package io.github.pureza.warbots.ai.goal;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.search.Path;

/**
 * Explores the world
 *
 * In practice, this just selects a random point from the map and instructs the
 * bot to go there.
 */
public class ExploreGoal extends Goal {

    /**
     * Internal goal instructing the bot to move to a random point
     * This goal is reset every time the bot reaches the target or the goal
     * fails for some reason.
     */
    private FollowPathGoal followPathGoal;


    public ExploreGoal(Bot bot) {
        super(bot);
    }


    @Override
    public State update(long dt) {
        State state = followPathGoal.update(dt);
        switch (state) {
            case COMPLETED:
            case FAILED:
                // If the Follow Path goal completes successfully or fails (due
                // to a timeout, for example), choose a new point and go there!
                followPathToRandomPoint();
                break;
        }

        // This goal is always active, unless it's interrupted
        return State.ACTIVE;
    }


    @Override
    public void start() {
        // Choose a new point and go there
        followPathToRandomPoint();
    }


    @Override
    public void stop() {
        if (followPathGoal != null) {
            // Stop the bot
            followPathGoal.stop();
        }
    }


    /**
     * Chooses a random, empty point from the map and instructs the bot to go
     * there, using A* to find the path
     */
    private void followPathToRandomPoint() {
        Point target = map.chooseRandomLocation();
        Path<Point> path = bot.findPathTo(target);
        followPathGoal = new FollowPathGoal(bot, path);
        followPathGoal.start();
    }


    @Override
    public String toString() {
        return "explore";
    }
}
