package io.github.pureza.warbots.ai.evaluator;

import io.github.pureza.warbots.ai.goal.CatchItemGoal;
import io.github.pureza.warbots.ai.goal.Goal;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.WeaponItem;

/**
 * Evaluates the desirability of catching a weapon item
 *
 * Catching a weapon becomes more desirable when the bot's weapon strength is
 * declining or when the closest weapon item is nearby.
 */
public class CatchWeaponEvaluator extends CatchItemEvaluator<WeaponItem> {

    public CatchWeaponEvaluator(Bot bot) {
        super(bot, bot.getGame().getMap().getWeaponItems());
    }


    @Override
    public double desirability() {
        // Arbitrary multiplier, chosen empirically
        double k = 0.3;

        // The more weapon strength I have, the less desirable it is to get more weapons
        // The closest I am to a weapon, the more desirable it becomes
        return k * (1 - bot.getTotalWeaponStrength()) / effortToSelectedTarget();
    }


    @Override
    public Goal makeGoal() {
        return new CatchItemGoal<>(bot, selectTarget());
    }
}
