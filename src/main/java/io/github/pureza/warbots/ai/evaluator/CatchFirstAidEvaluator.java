package io.github.pureza.warbots.ai.evaluator;

import io.github.pureza.warbots.ai.goal.CatchItemGoal;
import io.github.pureza.warbots.ai.goal.Goal;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.FirstAidItem;


/**
 * Evaluates the desirability of catching a first aid item
 *
 * Catching a first aid item becomes more desirable when the bot's health is
 * declining or when the closest first aid item is nearby.
 */
public class CatchFirstAidEvaluator extends CatchItemEvaluator<FirstAidItem> {

    public CatchFirstAidEvaluator(Bot bot) {
        super(bot, bot.getGame().getMap().getFirstAidItems());
    }


    @Override
    public double desirability() {
        // Arbitrary multiplier, chosen empirically
        double k = 1;

        // The more health we have, the less desirable it is to catch a first aid item
        // The closest I am to a first aid item, the more desirable it becomes
        return k * (1 - bot.getHealth() / 100.0) / effortToSelectedTarget();
    }


    @Override
    public Goal makeGoal() {
        return new CatchItemGoal<>(bot, selectTarget());
    }
}
