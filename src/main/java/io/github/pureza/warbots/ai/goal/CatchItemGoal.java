package io.github.pureza.warbots.ai.goal;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.InventoryItem;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.search.Path;


/**
 * Instructs the bot to catch an item of a certain type
 */
public class CatchItemGoal<T extends InventoryItem> extends Goal {

    /** The item the bot is currently going after */
    private final T target;

    /** Internal goal used to follow the path to the target item */
    private FollowPathGoal followPathGoal;


    public CatchItemGoal(Bot bot, T target) {
        super(bot);
        this.target = target;
    }


    @Override
    public State update(long dt) {
        // There were no items to choose from
        if (target == null) {
            return State.FAILED;
        }

        // Otherwise, just move the bot towards the item
        return followPathGoal.update(dt);
    }


    @Override
    public void start() {
        if (target != null) {
            Path<Point> path = bot.findPathTo(target.getLocation());
            followPathGoal = new FollowPathGoal(bot, path);
            followPathGoal.start();
        } else {
            followPathGoal = null;
        }
    }


    @Override
    public void stop() {
        if (followPathGoal != null) {
            // Stop the bot
            followPathGoal.stop();
        }
    }


    public T getTarget() {
        return target;
    }


    @Override
    public String toString() {
        return "catch " + target.toString();
    }
}
