package io.github.pureza.warbots.ai.goal;

import io.github.pureza.warbots.entities.Bot;

/**
 * Pursues an enemy bot using the pursuit steering behavior
 *
 * This goal succeeds when the bot is close to the enemy and fails if the
 * distance to the enemy doesn't decrease for a certain period of time.
 */
public class PursueEnemyGoal extends Goal {

    /** Aborts the pursuit if the bot gets stuck for longer than this */
    private static final double MAX_TIME_WITHOUT_PROGRESS = 1000;

    /** The pursuit succeeds when the distance to the enemy is lower than this */
    private static final double PURSUIT_SUCCESSFUL_DISTANCE = 0.5;

    /** The bot we are pursuing */
    private final Bot target;

    /** Distance to the target bot during the previous iteration */
    private double previousDistance;

    /** Time passed since the last time we closed in on the enemy */
    private double timeWithoutProgress = 0;


    public PursueEnemyGoal(Bot bot, Bot target) {
        super(bot);
        this.target = target;
    }


    @Override
    public State update(long dt) {
        double distance = bot.getLocation().distanceTo(target.getLocation());

        // Abort if the bot got stuck somewhere
        if (isStuck(distance, dt)) {
            fail();
            return State.FAILED;
        }

        // Have we reached the target?
        if (distance < PURSUIT_SUCCESSFUL_DISTANCE) {
            complete();
            return State.COMPLETED;
        }

        // Keep pursuing the enemy
        bot.pursue(target);

        previousDistance = distance;

        // We haven't reached the target yet, go on
        return State.ACTIVE;
    }


    @Override
    public void start() {
        this.previousDistance = bot.getLocation().distanceTo(target.getLocation());
    }


    @Override
    public void stop() {
        bot.stop();
    }


    /**
     * Checks if the bot is stuck
     *
     * The bot is considered to be stuck if the distance to the enemy has been
     * decreasing very slowly or not at all
     */
    private boolean isStuck(double distance, long dt) {
        // Are we getting closer to the enemy? If not, we might be stuck
        if (distance >= previousDistance - bot.getMaxSpeed() / 5) {
            timeWithoutProgress += dt;
        } else {
            timeWithoutProgress = 0;
        }

        // Is the bot stuck somewhere?
        return timeWithoutProgress > MAX_TIME_WITHOUT_PROGRESS;
    }


    private void complete() {
        stop();
    }


    private void fail() {
        stop();
    }


    @Override
    public String toString() {
        return "pursue";
    }
}
