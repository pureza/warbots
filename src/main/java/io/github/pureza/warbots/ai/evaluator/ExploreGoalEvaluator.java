package io.github.pureza.warbots.ai.evaluator;

import io.github.pureza.warbots.ai.goal.ExploreGoal;
import io.github.pureza.warbots.ai.goal.Goal;
import io.github.pureza.warbots.entities.Bot;


/**
 * Evaluates the desirability of exploring the map
 *
 * Basically, the bot will explore the map if there is nothing more interesting
 * to do.
 */
public class ExploreGoalEvaluator extends GoalEvaluator {

    public ExploreGoalEvaluator(Bot bot) {
        super(bot);
    }


    @Override
    public double desirability() {
        return 0.01;
    }


    @Override
    public ExploreGoal makeGoal() {
        return new ExploreGoal(bot);
    }


    @Override
    public boolean shouldReplace(Goal currentGoal, Goal alternativeGoal) {
        assert alternativeGoal instanceof ExploreGoal;

        if (currentGoal instanceof ExploreGoal) {
            // Never replace an ExploreGoal with another: the bot won't make up its
            // mind about where it wants to go exploring and will just stay in the
            // same place
            return false;
        }

        return true;
    }
}
