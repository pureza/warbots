package io.github.pureza.warbots.ai.goal;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.StaticEntity;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Vector;

import java.util.function.Consumer;

/**
 * Makes the bot move left and right while attacking an enemy, to make it more
 * difficult for the opponent to predict his future position
 */
public class StrafeGoal extends Goal {

    /**
     * Distance at which the target is considered to be too far and this
     * goal fails
     */
    public static final double FAR_DISTANCE = 1.5 * ApproachGoal.CLOSE_DISTANCE;

    /** How much to strafe in each direction? */
    private static final double STRAFE_LENGTH = 1;

    /**
     * Minimum amount of free space the bot needs to have before choosing the
     * strafing direction
     */
    private static final double MIN_FREE_SPACE = 0.25;

    /**
     * Directions in which the bot can strafe
     *
     * The order of a direction represents the preference the bot assigns to
     * strafing in that direction. As you can see, the bot prefers strafing to
     * the sides rather than strafing forwards and backwards.
     */
    enum StrafeDirection {
        LEFT(Vector.vec(0, MIN_FREE_SPACE)),
        RIGHT(Vector.vec(0, -MIN_FREE_SPACE)),
        FRONT(Vector.vec(MIN_FREE_SPACE, 0)),
        BACK(Vector.vec(-MIN_FREE_SPACE, 0));

        /** Displacement vector for the direction */
        private final Vector vector;


        StrafeDirection(Vector vector) {
            this.vector = vector;
        }


        public Vector vector() {
            return vector;
        }
    }

    /** The bot I'm targeting */
    private final Bot target;

    /** Has the bot rotated towards the target? */
    private boolean hasRotated = false;

    /** The direction of strafing that is currently active */
    private StrafeDirection direction;

    /** The point the bot is moving towards in the current strafe cycle */
    private Point strafeTarget;

    /**
     * Indicates whether the bot should switch the strafing direction on the
     * next call to update(). Set when there is a collision.
     */
    private boolean switchDirection = false;

    /**
     * Forces the bot to switch the strafing direction when it collides with a
     * wall
     */
    private Consumer<StaticEntity> triggerSwitchOnCollisionWithWall = (entity) -> switchDirection = true;


    /**
     * Forces the bot to switch the strafing direction when it collides with
     * another bot
     */
    private Consumer<Bot> triggerSwitchOnCollisionWithBot = (bot) -> switchDirection = true;


    public StrafeGoal(Bot bot, Bot target) {
        super(bot);
        this.target = target;
    }


    @Override
    public State update(long dt) {
        // Rotate the bot towards the target before it starts moving
        if (!hasRotated) {
            Vector toTarget = target.getLocation().minus(bot.getLocation());
            if (!toTarget.isNull() && bot.rotateFacing(toTarget, dt)) {
                hasRotated = true;

                this.direction = StrafeDirection.LEFT;

                // Choose the initial direction of strafing
                // The bot always prefers strafing left and right, but will
                // strafe front and back if necessary
                for (StrafeDirection direction : StrafeDirection.values()) {
                    Point point = bot.toWorldCoordinates(direction.vector().toPoint());
                    if (bot.canMoveTo(point)) {
                        this.direction = direction;
                        this.strafeTarget = point;
                        break;
                    }
                }
            }
        } else {
            if (strafeTarget != null) {
                bot.seek(strafeTarget);
            }

            // Switch from top/bottom strafing to left/right strafing as soon
            // as possible
            switch (direction) {
                case FRONT:
                    chooseFreeDirection(StrafeDirection.RIGHT, StrafeDirection.LEFT);
                    break;
                case BACK:
                    chooseFreeDirection(StrafeDirection.LEFT, StrafeDirection.RIGHT);
                    break;
            }

            // If the bot is stuck or completed strafing through this direction,
            // switch direction
            if (bot.getLocation().equals(strafeTarget) || switchDirection) {
                switch (direction) {
                    case FRONT:
                        // Back up. If not possible, keep going straight. Otherwise, strafe right and left.
                        chooseFreeDirectionOrDefaultToFirst(StrafeDirection.BACK, StrafeDirection.FRONT,
                                StrafeDirection.RIGHT, StrafeDirection.LEFT);
                        break;
                    case BACK:
                        // Move forward. If not possible, keep moving backwards. Otherwise, strafe left and right.
                        chooseFreeDirectionOrDefaultToFirst(StrafeDirection.FRONT, StrafeDirection.BACK,
                                StrafeDirection.LEFT, StrafeDirection.RIGHT);
                        break;
                    case RIGHT:
                        // Move to the left. If not possible, keep moving to the right.
                        // Otherwise, try strafing forward and backward
                        chooseFreeDirectionOrDefaultToFirst(StrafeDirection.LEFT, StrafeDirection.RIGHT,
                                StrafeDirection.FRONT, StrafeDirection.BACK);
                        break;
                    case LEFT:
                        // Move to the right. If not possible, keep moving to the left.
                        // If even that is not possible, try strafing forward and backward.
                        chooseFreeDirectionOrDefaultToFirst(StrafeDirection.RIGHT, StrafeDirection.LEFT,
                                StrafeDirection.BACK, StrafeDirection.FRONT);
                        break;
                }

                switchDirection = false;
            }
        }

        // If I can't shoot at the target or he is too far away, we have to
        // approach it again
        if (!bot.isShootable(target) || bot.getLocation().distanceTo(target.getLocation()) > FAR_DISTANCE) {
            fail();
            return State.FAILED;
        }

        // We haven't reached the target yet, go on
        return State.ACTIVE;
    }


    @Override
    public void start() {
        // Switch direction when the bot hits a wall or another bot
        bot.onCollisionWithStaticEntity().subscribe(triggerSwitchOnCollisionWithWall);
        bot.onCollisionWithBot().subscribe(triggerSwitchOnCollisionWithBot);
    }


    @Override
    public void stop() {
        bot.stop();

        // Unsubscribe from the event sources
        bot.onCollisionWithStaticEntity().unsubscribe(triggerSwitchOnCollisionWithWall);
        bot.onCollisionWithBot().unsubscribe(triggerSwitchOnCollisionWithBot);
    }


    /**
     * What to do when the goal fails
     */
    private void fail() {
        stop();
    }


    /**
     * Selects the first valid strafing direction from the argument options,
     * defaulting to the first option if none of the candidates are valid
     *
     * This avoid the bot getting stuck in tight areas.
     */
    private void chooseFreeDirectionOrDefaultToFirst(StrafeDirection... directions) {
        if (!chooseFreeDirection(directions)) {
            setStrafeDirection(directions[0]);
        }
    }


    /**
     * Selects the first valid strafing direction from the argument options
     *
     * A strafing direction is considered valid if there is enough room to
     * strafe along that direction.
     */
    private boolean chooseFreeDirection(StrafeDirection... directions) {
        for (StrafeDirection direction : directions) {
            if (hasEnoughRoom(direction)) {
                setStrafeDirection(direction);
                return true;
            }
        }

        return false;
    }


    /**
     * Sets the current strafing direction
     */
    private void setStrafeDirection(StrafeDirection direction) {
        strafeTarget = bot.toWorldCoordinates(direction.vector().scalarMul(STRAFE_LENGTH / MIN_FREE_SPACE).toPoint());
        this.direction = direction;
    }


    /**
     * Does the bot have enough room to strafe in the given direction?
     */
    private boolean hasEnoughRoom(StrafeDirection direction) {
        Point target = bot.toWorldCoordinates(direction.vector().toPoint());
        return bot.canMoveTo(target);
    }


    @Override
    public String toString() {
        return "strafe";
    }
}
