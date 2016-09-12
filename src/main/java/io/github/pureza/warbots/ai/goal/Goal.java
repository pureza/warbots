package io.github.pureza.warbots.ai.goal;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.navigation.Map;


/**
 * A goal the bot may want to achieve
 */
public abstract class Goal {

    /**
     * The states a Goal can be in
     */
    public enum State {
        ACTIVE,
        COMPLETED,
        FAILED
    }


    /** The bot that owns this goal */
    protected final Bot bot;

    /** The game's map */
    protected final Map map;


    /**
     * Creates a new goal
     *
     * The goal constructor should be as lightweight as possible. Just because
     * the goal was instantiated, it doesn't mean it will run. Leave any
     * expensive operations (such as finding a path) to the start() method.
     */
    public Goal(Bot bot) {
        this.bot = bot;
        this.map = bot.getGame().getMap();
    }


    /**
     * Starts this goal
     *
     * When this method is called, the goal is surely going to be executed.
     * Thus, any expensive operation the goal requires to execute, such as
     * finding a path, should be done here.
     */
    public abstract void start();


    /**
     * Updates the goal after some time has passed
     *
     * Returns the current state of the goal.
     */
    public abstract State update(long dt);


    /**
     * Interrupts this goal before it is complete
     *
     * This happens if, for instance, a new goal becomes more important.
     */
    public abstract void stop();
}
