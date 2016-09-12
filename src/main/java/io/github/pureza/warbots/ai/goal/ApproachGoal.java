package io.github.pureza.warbots.ai.goal;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.search.Path;

/**
 * The goal of the ApproachGoal is to approach the enemy bot until he is close
 * enough and there is a clear shot
 */
public class ApproachGoal extends Goal {

    /**
     * Distance at which the target is considered to be close enough and this
     * goal terminates
     */
    public final static double CLOSE_DISTANCE = 3;

    /** The enemy bot */
    private final Bot target;

    /** Sub goal used to reach the enemy's position */
    private Goal subGoal;


    public ApproachGoal(Bot bot, Bot target) {
        super(bot);

        this.target = target;
    }


    @Override
    public void start() {
        if (target != null) {

            // We know the enemy's location, thus it must be in sight
            assert bot.isInFov(target);

            // If there is a clear shot between me and the enemy, just pursue him
            if (bot.isShootable(target)) {
                subGoal = new PursueEnemyGoal(bot, target);
                subGoal.start();
            } else {
                // I can see where he is, but I can't shoot him from here
                // because there must be a wall blocking the projectile
                // Find a clear path to approach the target
                Path<Point> path = bot.findPathTo(target.getLocation());
                subGoal = new FollowPathGoal(bot, path);
                subGoal.start();
            }
        }
    }


    @Override
    public State update(long dt) {
        // If there is no target bot to approach, abort
        if (target == null) {
            return State.FAILED;
        }

        if (bot.isShootable(target)) {
            double distance = bot.getLocation().distanceTo(target.getLocation());
            if (distance < CLOSE_DISTANCE) {
                // There is a clear shot to the enemy and I am close enough to
                // it. Success!
                complete();
                return State.COMPLETED;
            }
        } else if (!bot.isInFov(target)) {
            // I no longer know where the target bot is. Abort.
            fail();
            return State.FAILED;
        }

        // If the target is visible (shootable or not), just update the sub goal
        // This may fail if the bot gets stuck, but that's ok
        return subGoal.update(dt);
    }


    @Override
    public void stop() {
        subGoal.stop();
    }


    private void complete() {
        stop();
    }


    private void fail() {
        stop();
    }


    @Override
    public String toString() {
        return "approach:" + String.valueOf(subGoal);
    }
}
