package io.github.pureza.warbots.ai.evaluator;

import io.github.pureza.warbots.ai.goal.Goal;
import io.github.pureza.warbots.ai.goal.InvestigateShootingGoal;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.memory.ShotMemory;


/**
 * Evaluates the desirability of investigating a shooting when the bot gets
 * hit from behind
 */
public class InvestigateShootingEvaluator extends GoalEvaluator {


    public InvestigateShootingEvaluator(Bot bot) {
        super(bot);
    }


    @Override
    public double desirability() {
        ShotMemory shotMemory = bot.getMemory().getShotMemory();
        return shotMemory.hasBeenShot() ? 0.2 : 0;
    }


    @Override
    public Goal makeGoal() {
        return new InvestigateShootingGoal(bot, bot.getMemory().getShotMemory().getLastShotDirection());
    }


    @Override
    public boolean shouldReplace(Goal currentGoal, Goal alternativeGoal) {
        assert alternativeGoal instanceof InvestigateShootingGoal;

        if (currentGoal instanceof InvestigateShootingGoal) {
            // Don't replace an InvestigateShootingGoal by another, otherwise
            // the bot may keep turning around without leaving the same place
            return false;
        }

        return true;
    }
}
