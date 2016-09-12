package io.github.pureza.warbots.ai.goal;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.search.Path;

/**
 * Hunts an enemy bot by looking for him at its last known location
 */
public class HuntGoal extends Goal {

    /** The enemy bot to look for */
    private final Bot target;

    /** Inner goal that actually moves the bot towards the location */
    private Goal followPathGoal;


    public HuntGoal(Bot bot, Bot target) {
        super(bot);

        this.target = target;
    }

    @Override
    public void start() {
        if (target != null) {
            // Retrieve the enemy's last known location from memory and look
            // for him there
            Point lastKnownLocation = bot.getMemory().getBotRecord(target).getLastKnownLocation();
            Path<Point> path = bot.findPathTo(lastKnownLocation);
            followPathGoal = new FollowPathGoal(bot, path);
            followPathGoal.start();
        }
    }


    @Override
    public State update(long dt) {
        // If there was no target...
        if (target == null) {
            return State.FAILED;
        }

        // Succeed if I have seen the bot!
        if (bot.isInFov(target)) {
            complete();
            return State.COMPLETED;
        }

        // Otherwise, just move towards the location
        return followPathGoal.update(dt);
    }


    @Override
    public void stop() {
        if (followPathGoal != null) {
            followPathGoal.stop();
        }
    }


    private void complete() {
        stop();
    }


    @Override
    public String toString() {
        return "hunt";
    }
}
