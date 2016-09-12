package io.github.pureza.warbots.ai.evaluator;

import io.github.pureza.warbots.ai.goal.CatchItemGoal;
import io.github.pureza.warbots.ai.goal.Goal;
import io.github.pureza.warbots.memory.ItemMemoryRecord;
import io.github.pureza.warbots.memory.Memory;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.InventoryItem;
import io.github.pureza.warbots.util.Pair;

import java.util.List;


/**
 * Parent class for evaluators for goals related to catching items
 */
public abstract class CatchItemEvaluator<T extends InventoryItem> extends GoalEvaluator {

    /** All the items of the correct type on the map */
    private final List<T> items;


    public CatchItemEvaluator(Bot bot, List<T> items) {
        super(bot);
        this.items = items;
    }


    @Override
    public boolean shouldReplace(Goal currentGoal, Goal alternativeGoal) {
        assert alternativeGoal instanceof CatchItemGoal;

        // Both goals are about catching some item
        if (currentGoal instanceof CatchItemGoal) {
            CatchItemGoal<?> current = (CatchItemGoal<?>) currentGoal;
            CatchItemGoal<?> alternative = (CatchItemGoal<?>) alternativeGoal;

            InventoryItem currentTarget = current.getTarget();
            InventoryItem alternativeTarget = alternative.getTarget();

            // If both goals are about catching the exact same item, don't replace
            if (alternativeTarget == currentTarget) {
                return false;
            }

            /*
             * At this point we know that both goals want to catch different
             * items. But still, we don't always want to replace the current
             * goal.
             */

            Memory memory = bot.getMemory();
            ItemMemoryRecord.State currentState = memory.getItemRecord(currentTarget).getState();
            ItemMemoryRecord.State alternativeState = memory.getItemRecord(alternativeTarget).getState();

            // Do we want to switch to the alternative?
            switch (alternativeState) {
                case ACTIVE:
                    // Always switch if the alternative item is active
                    return true;

                case UNKNOWN:
                    // We prefer a target we know nothing about to an inactive
                    // target. This encourages the bots to explore the map.
                    if (currentState == ItemMemoryRecord.State.INACTIVE_UNCERTAIN || currentState == ItemMemoryRecord.State.INACTIVE_CERTAIN) {
                        return true;
                    }

                    break;
            }

            // Otherwise, don't replace the goal
            return false;
        }

        // If the current goal was not a CatchItemGoal, replace
        return true;
    }


    /**
     * Finds the item that looks more promising based on heuristics such as the
     * time it will take to reach it and the amount of time the bot thinks it
     * will take until the item becomes active
     */
    protected T selectTarget() {
        return items.stream()
                .map(item -> Pair.of(item, targetEffort(item)))
                .min((a, b) -> Double.compare(a.second(), b.second()))
                .map(Pair::first)
                .orElse(null);
    }


    /**
     * Estimates the amount of time it will take until the bot catches an item,
     * taking both the distance to the item and the time the bot thinks it will
     * take until the item becomes active into consideration
     */
    protected double targetEffort(T target) {
        // Fetch the memory record for this item
        ItemMemoryRecord record = bot.getMemory().getItemRecord(target);

        // Time until the item becomes active again
        long timeUntilActive = timeUntilActive(record);

        // Time that will take to move there (assuming no obstacles)
        double distance = bot.getLocation().distanceTo(target.getLocation());
        double moveDuration = distance / bot.getMaxSpeed();

        // Estimated amount of time to wait until the bot catches the item
        return Math.max(timeUntilActive, moveDuration);
    }


    /**
     * Estimates the amount of time it will take until the bot catches the
     * best target
     */
    protected double effortToSelectedTarget() {
        T target = selectTarget();
        if (target == null) {
            return Double.MAX_VALUE;
        }

        return targetEffort(target);
    }


    /**
     * Calculates the time left until the item is activated
     *
     * This can be an exact time or an estimation, depending on what the bot
     * knows about the item.
     */
    private long timeUntilActive(ItemMemoryRecord record) {
        switch (record.getState()) {
            case ACTIVE:
                return 0;
            case INACTIVE_CERTAIN:
                return record.getTimeUntilNextActivation();
            case INACTIVE_UNCERTAIN:
                // This is basically an estimation of the expected time until
                // the item becomes active again
                return (record.getActivationInterval() - record.getTimeHasBeenVisible()) / 2;
            case UNKNOWN:
                // Assumes unknown items are active. This encourages the bot to
                // explore the map.
                return 0;
            default:
                throw new IllegalArgumentException("Unexpected state");
        }
    }
}
