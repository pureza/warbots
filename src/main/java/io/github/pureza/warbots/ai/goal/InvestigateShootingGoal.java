package io.github.pureza.warbots.ai.goal;

import io.github.pureza.warbots.geometry.Vector;
import io.github.pureza.warbots.entities.Bot;

/**
 * Tries to figure out who shot a projectile that hit a bot by rotating the bot
 * towards the direction it came from
 */
public class InvestigateShootingGoal extends Goal {

    /** The direction the projectile came from */
    private final Vector targetDirection;


    public InvestigateShootingGoal(Bot bot, Vector targetDirection) {
        super(bot);
        this.targetDirection = targetDirection;
    }


    @Override
    public void start() {
        // Nothing to do
    }


    @Override
    public State update(long dt) {
        // Rotate the bot
        if (bot.rotateFacing(targetDirection, dt)) {
            return State.COMPLETED;
        }

        return State.ACTIVE;
    }


    @Override
    public void stop() { }


    @Override
    public String toString() {
        return "investigate";
    }
}
