package io.github.pureza.warbots.ai;

import io.github.pureza.warbots.ai.goal.Goal;
import io.github.pureza.warbots.entities.Bot;


/**
 * Entry point for the Bot's Artificial Intelligence engine
 */
public class BotAI {

    /** The arbitrator that decides which goal to pursuit at any given moment */
    private final GoalArbitrator arbitrator;

    /** The current goal */
    private Goal goal;

    /** The bot */
    private Bot bot;


    public BotAI(Bot bot) {
        this.bot = bot;
        this.arbitrator = new GoalArbitrator(bot);
    }


    /**
     * Updates the AI after a certain amount of time has passed, by choosing
     * a new goal
     *
     * If the goal is the same, retains the previous goal.
     */
    public void update(long dt) {
        Goal previousGoal = this.goal;
        this.goal = arbitrator.arbitrate(previousGoal);

        // Interrupt the previous goal if it's not the same
        if (previousGoal != goal) {
            if (previousGoal != null) {
                previousGoal.stop();
            }

            goal.start();
        }

        // Update the current goal
        Goal.State state = this.goal.update(dt);
        switch (state) {
            case COMPLETED:
            case FAILED:
                // Force the AI to re-plan
                this.goal = null;
        }
    }
}
