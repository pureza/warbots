package io.github.pureza.warbots.steering;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Vector;


/**
 * The seek behavior
 */
public class SeekBehavior extends SteeringBehavior {

    /** The target I'm trying to reach */
    private final Point target;

    public SeekBehavior(Bot bot, Point target) {
        super(bot);
        this.target = target;
    }


    @Override
    public Vector calculateVelocity(long timeElapsed) {
        Vector toTarget = target.minus(bot.getLocation());
        double dist = toTarget.norm();

        // Can we reach the destination in this iteration?
        if (dist > bot.getMaxSpeed() * (timeElapsed / 1000.0)) {
            // We are still too far away from the target. Go as fast as possible.
            return toTarget.scalarMul(bot.getMaxSpeed() / toTarget.norm());
        } else {
            // Return the right velocity so that the agent reaches the target
            // during this iteration
            return toTarget.scalarDiv(timeElapsed / 1000.0);
        }
    }
}
