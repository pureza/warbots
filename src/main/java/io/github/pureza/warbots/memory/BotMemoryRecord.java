package io.github.pureza.warbots.memory;

import io.github.pureza.warbots.geometry.Point;

/**
 * The memory record for an enemy bot
 */
public class BotMemoryRecord {

    /** The location where I last saw this bot */
    private Point lastKnownLocation;

    /** For how long have I not seen this bot? */
    private long timeSinceLastSeen;


    /**
     * Registers this bot as visible at some location
     */
    public void store(Point location) {
        this.lastKnownLocation = location;
        this.timeSinceLastSeen = 0;
    }


    /**
     * Updates the record for a bot that I haven't seen in a while
     */
    public void update(long dt) {
        this.timeSinceLastSeen += dt;
    }


    public Point getLastKnownLocation() {
        assert lastKnownLocation != null;
        return lastKnownLocation;
    }


    public long getTimeSinceLastSeen() {
        assert lastKnownLocation != null;
        return timeSinceLastSeen;
    }
}
