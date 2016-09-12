package io.github.pureza.warbots.steering;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Vector;

/**
 * The pursuit behavior
 *
 * Tries to predict the future position of a bot and moves towards it
 */
public class PursuitBehavior extends SteeringBehavior {

    /** The bot I'm pursuing */
    private Bot target;


    public PursuitBehavior(Bot agent, Bot target) {
        super(agent);

        this.target = target;
    }


    @Override
    public Vector calculateVelocity(long dt) {
        Vector toTarget = target.getLocation().minus(this.bot.getLocation());

        // How long will I take to reach the target
        double lookAheadTime = toTarget.norm() / (bot.getMaxSpeed() + target.getVelocity().norm());

        // Seek to the predicted future position of the target
        Point predictedLocation = target.getLocation().plus(target.getVelocity().scalarMul(lookAheadTime));
        SeekBehavior seek = new SeekBehavior(bot, predictedLocation);
        return seek.calculateVelocity(dt);
    }
}
