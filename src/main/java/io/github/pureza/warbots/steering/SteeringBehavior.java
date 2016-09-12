package io.github.pureza.warbots.steering;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Vector;


/**
 * Abstract steering behavior
 *
 * All steering behaviors work by calculating the velocity that should be
 * applied to the bots in order to satisfy their definition.
 *
 * Individual steering behaviors can be enabled or disabled, and there can be
 * several of them active at the same time.
 */
public abstract class SteeringBehavior {

    /** The bot this behavior applies to */
    protected Bot bot;


    public SteeringBehavior(Bot bot) {
        this.bot = bot;
    }


    /**
     * Calculates the velocity to apply to the bot in order to create the motion
     * desired by this steering behavior
     */
    public abstract Vector calculateVelocity(long timeElapsed);
}
