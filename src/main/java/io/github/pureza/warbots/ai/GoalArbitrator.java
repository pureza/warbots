package io.github.pureza.warbots.ai;


import io.github.pureza.warbots.ai.evaluator.*;
import io.github.pureza.warbots.ai.goal.Goal;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.util.Pair;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * The arbitrator decides which goal the bot should pursue at any given moment
 */
public class GoalArbitrator {

    /** The bot this arbitrator belongs to */
    private final Bot bot;

    /** The evaluators for each goal */
    private final List<GoalEvaluator> evaluators;


    public GoalArbitrator(Bot bot) {
        this.bot = bot;
        this.evaluators = initEvaluators();
    }


    /**
     * Selects the best goal to pursuit
     *
     * If nothing substantial has changed, the current goal is returned.
     * In this scenario, the caller should not stop the current goal
     * before replacing it with the new one.
     */
    public Goal arbitrate(Goal currentGoal) {
        // Runs all evaluators and returns the one with the highest desirability
        GoalEvaluator best = evaluators.stream()
                .map(evaluator -> Pair.of(evaluator, evaluator.desirability()))
                .max((a, b) -> Double.compare(a.second(), b.second()))
                .map(Pair::first)
                .orElse(null);

        if (best == null) {
            return currentGoal;
        }

        Goal desiredGoal = best.makeGoal();
        return best.shouldReplace(currentGoal, desiredGoal) ? desiredGoal : currentGoal;
    }


    /**
     * Initializes the evaluators
     */
    private List<GoalEvaluator> initEvaluators() {
        return asList(
                new ExploreGoalEvaluator(bot),
                new InvestigateShootingEvaluator(bot),
                new CatchFirstAidEvaluator(bot),
                new CatchWeaponEvaluator(bot),
                new AttackGoalEvaluator(bot));
    }
}
