package io.github.pureza.warbots.ai.goal;

import io.github.pureza.warbots.entities.Bot;

/**
 * Attacks an enemy bot
 *
 * Internally, this goal works as following:
 * - If the enemy is shootable and close enough, it fires at him and strafes
 *   to the sides
 * - If the enemy is visible but far away, it approaches him
 * - Otherwise, if the current location of the enemy is unknown, it looks for
 *   him
 */
public class AttackGoal extends Goal {

    /** The target bot to attack */
    private final Bot target;

    /** Inner goal that is currently active */
    private Goal subGoal;


    public AttackGoal(Bot bot, Bot target) {
        super(bot);
        this.target = target;
    }


    @Override
    public void start() {
        if (target != null) {

            // If I know where the bot is...
            if (bot.isInFov(target)) {
                double distance = bot.getLocation().distanceTo(target.getLocation());

                // If I can hit the enemy and he is reasonably close, strafe
                // while attacking
                if (bot.isShootable(target) && distance < StrafeGoal.FAR_DISTANCE) {
                    subGoal = new StrafeGoal(bot, target);
                    subGoal.start();
                } else {
                    // Otherwise, approach the enemy
                    subGoal = new ApproachGoal(bot, target);
                    subGoal.start();
                }
            } else {
                // I don't know where the enemy is. Go look for him, starting
                // by the location where I last saw him
                subGoal = new HuntGoal(bot, target);
                subGoal.start();
            }
        }
    }


    @Override
    public State update(long dt) {
        if (target == null) {
            // There was no enemy to target
            return State.FAILED;
        }

        // Mission accomplished!
        if (target.isDead()) {
            complete();
            return State.COMPLETED;
        }

        if (subGoal != null) {
            // Finish if the subGoal has finished
            Goal.State subState = subGoal.update(dt);
            switch (subState) {
                case FAILED:
                case COMPLETED:
                    return subState;
            }
        }

        // If I can shoot at the enemy, do it
        if (bot.isShootable(target)) {
            bot.fireAt(target, dt);
        }

        return State.ACTIVE;
    }


    @Override
    public void stop() {
        if (subGoal != null) {
            subGoal.stop();
        }
    }


    public Bot getTarget() {
        return target;
    }


    private void complete() {
        stop();
    }


    @Override
    public String toString() {
        return "attack:" + String.valueOf(subGoal);
    }
}
