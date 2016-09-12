package io.github.pureza.warbots.ai.evaluator;


import io.github.pureza.warbots.ai.goal.Goal;
import io.github.pureza.warbots.entities.Bot;


/**
 * A Goal Evaluator calculates the desirability of pursuing a specific goal
 */
public abstract class GoalEvaluator {

    /** The bot the evaluator applies to */
    protected final Bot bot;


    public GoalEvaluator(Bot bot) {
        this.bot = bot;
    }


    /**
     * Calculate the desirability of pursuing the goal
     */
    public abstract double desirability();


    /**
     * Instantiate the goal
     */
    public abstract Goal makeGoal();


    /**
     * Decides whether the new goal instantiated by makeGoal() should replace
     * the goal that is currently active, when both goals are of the same type
     *
     * For example, it is not worth it to replace an ExploreGoal by another,
     * because it's just more of the same. Even worse, if the arbitrator
     * decides on a new ExploreGoal every second, the bot will just keep
     * turning around without moving anywhere, because the target random
     * position to pursue keeps changing all the time.
     */
    public abstract boolean shouldReplace(Goal currentGoal, Goal alternativeGoal);
}
