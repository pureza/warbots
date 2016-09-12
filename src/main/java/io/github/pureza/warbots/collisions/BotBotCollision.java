package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.geometry.Vector;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;

/**
 * A collision between two bots
 *
 * Bot/Bot collisions are handled by retracting both bots until they no longer
 * overlap.
 */
public class BotBotCollision extends Collision {

    /**
     * Creates a new collision between two bots.
     */
    public BotBotCollision(Bot a, Bot b) {
        super(a, b);
    }


    @Override
    public void handle() {
        Bot a = (Bot) first;
        Bot b = (Bot) second;

        Point aPos = a.getLocation();
        Point bPos = b.getLocation();

        Vector aToB = bPos.minus(aPos);
        double distance = aToB.norm();
        double overlap = a.getBoundingRadius() + b.getBoundingRadius() - distance;

        // Do nothing if the bots are no longer colliding (maybe another
        // collision handler fixed this one as a side effect)
        if (overlap < 0) {
            return;
        }

        // How much should we move each bot until they no longer overlap?
        Vector displacement = aToB.normalize().scalarMul(overlap/2);

        // Restore the previous location, because we might need if there are
        // multiple collisions to handle
        a.restoreLocation();
        b.restoreLocation();

        // Move the bots along the displacement vector, in opposite directions
        a.setLocation(aPos.plus(displacement.scalarMul(-1)));
        b.setLocation(bPos.plus(displacement));

        // Fire the corresponding event
        a.onCollisionWithBot().fire(b);
        b.onCollisionWithBot().fire(a);

        // XXX Moving the bots may cause them to collide other other bots or
        // XXX even walls.
    }


    @Override
    public String toString() {
        return "BotBotCollision{" +
                "bot=" + first +
                "bot=" + second +
                "} ";
    }
}
