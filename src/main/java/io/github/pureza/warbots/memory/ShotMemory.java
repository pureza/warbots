package io.github.pureza.warbots.memory;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Vector;

/**
 * Remembers when the bot was shot for the last time
 */
public class ShotMemory {

    /** For how long does the bot remember the last time it was hit? */
    public static final long DURATION = 1000;

    /** The bot the memory belongs to */
    private final Bot bot;

    /** Time passed since the bot was shot for the last time */
    private long timeSinceLastShot;

    /** Direction where the shot came from */
    private Vector shotDirection;


    public ShotMemory(Bot bot) {
        this.bot = bot;
    }


    /**
     * Persists the direction a shot came from into the bot's memory
     */
    public void store(Vector shotDirection) {
        this.timeSinceLastShot = 0;
        this.shotDirection = shotDirection;
    }


    /**
     * Updates the memory of the last shot
     */
    public void update(long dt) {
        if (shotDirection == null) {
            return;
        }

        timeSinceLastShot += dt;

        // Forget about shots that happened long ago
        if (timeSinceLastShot > DURATION) {
            timeSinceLastShot = 0;
            shotDirection = null;
        } else if (bot.getHeadingVector().angleWith(shotDirection) < 0.1) {
            // Forget about shots that came from the direction I'm looking
            // towards now
            timeSinceLastShot = 0;
            shotDirection = null;
        }
    }


    /**
     * Checks if the bot remembers being shot in the recent past
     */
    public boolean hasBeenShot() {
        return shotDirection != null;
    }


    /**
     * Returns the direction from where the last shot came
     */
    public Vector getLastShotDirection() {
        return shotDirection;
    }


    /**
     * Returns the time passed since the bot was shot for the last time,
     * or null if the bot doesn't remember when it was shot
     */
    public Long getTimeSinceLastShot() {
        return hasBeenShot() ? timeSinceLastShot : null;
    }
}
