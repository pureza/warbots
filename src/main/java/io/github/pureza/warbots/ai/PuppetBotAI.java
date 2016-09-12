package io.github.pureza.warbots.ai;

import io.github.pureza.warbots.ai.goal.FollowPathGoal;
import io.github.pureza.warbots.ai.goal.Goal;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;

import java.awt.*;

/**
 * Entry point for the Bot's Artificial Intelligence engine
 */
public class PuppetBotAI extends BotAI {

    private Bot bot;

    private Point target;

    /** The current goal */
    private Goal goal;


    public PuppetBotAI(Bot bot) {
        super(bot);
        this.bot = bot;
        this.target = bot.getLocation();
    }


    /**
     * Updates the AI after a certain amount of time has passed, by choosing
     * a new goal
     *
     * If the goal is the same, retains the previous goal.
     */
    public void update(long dt) {
        if (goal != null) {
            Goal.State state = goal.update(dt);
            switch (state) {
                case COMPLETED:
                case FAILED:
                    this.goal = null;
            }
        }
    }


    public void setTarget(Point target) {
        this.target = target;

        if (this.goal != null) {
            this.goal.stop();
        }
        this.goal = new FollowPathGoal(bot, bot.findPathTo(target));
        this.goal.start();
    }


    public void render(Graphics2D graphics, int x, int y) {
        graphics.drawString("puppet", x, y);
    }

    public Point getTarget() {
        return target;
    }
}
