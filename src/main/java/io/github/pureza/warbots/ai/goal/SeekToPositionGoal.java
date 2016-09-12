package io.github.pureza.warbots.ai.goal;

import io.github.pureza.warbots.geometry.Vector;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;


/**
 * Moves the bot to an arbitrary location using the Seek steering behavior
 *
 * This goal starts by rotating the bot towards the point and only then it
 * starts moving.
 *
 * This goal is constantly monitoring its progress: if it is taking to long
 * (perhaps because the bot is stuck?), it fails, allowing the AI to re-plan.
 */
public class SeekToPositionGoal extends Goal {

    /** Tolerance to add to the estimation of how long the seek will take */
    private static final double ESTIMATION_TOLERANCE = 0.1;

    /** The target point the bot is trying to reach */
    private final Point target;

    /** Time passed since the goal started */
    private long duration;

    /** Maximum amount of time this goal can take before it is cancelled */
    private long maxDuration;

    private final boolean shouldRotate;

    /** Has the bot rotated towards the target? */
    private boolean hasRotated = false;


    public SeekToPositionGoal(Bot bot, Point target) {
        this(bot, target, true);
    }


    public SeekToPositionGoal(Bot bot, Point target, boolean shouldRotate) {
        super(bot);
        this.target = target;
        this.shouldRotate = shouldRotate;
    }


    @Override
    public void start() {
        this.maxDuration = estimateDuration();
    }


    @Override
    public State update(long dt) {
        // Rotate the bot towards the target before it starts moving
        // Furthermore, the time passed only starts counting after rotation has
        // finished (otherwise, an initial big rotation always makes the AI
        // think the bot is stuck).
        if (!hasRotated) {
            Vector toTarget = target.minus(bot.getLocation());
            if (!toTarget.isNull() && (!shouldRotate || bot.rotateFacing(toTarget, dt))) {
                // The bot is heading towards the target. Start moving it!
                bot.seek(target);
                hasRotated = true;
            }
        } else {
            // Increase the amount of time spent in this goal
            duration += dt;

            // If the bot is stuck, abort
            if (isStuck()) {
                fail();
                return State.FAILED;
            }
        }

        // Complete successfully if the bot has reached the target
        if (bot.getLocation().equals(target)) {
            complete();
            return State.COMPLETED;
        }

        // We haven't reached the target yet, go on
        return State.ACTIVE;
    }


    @Override
    public void stop() {
        bot.stop();
    }


    private void complete() {
        bot.stop();
    }


    private void fail() {
        bot.stop();
    }


    /**
     * Estimates how long it will take until the bot reaches the target point,
     * assuming it doesn't get stuck
     *
     * Adds a bit of slack, to account for quick collisions with other bots
     */
    private long estimateDuration() {
        double length = target.minus(bot.getLocation()).norm();
        double maxSpeed = bot.getMaxSpeed();

        return (long) ((length / maxSpeed + ESTIMATION_TOLERANCE) * 1000);
    }


    /**
     * Checks if the bot is stuck
     *
     * The bot is stuck if it spent more time than expected in this goal
     */
    private boolean isStuck() {
        return duration >= maxDuration;
    }
}
