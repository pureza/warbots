package io.github.pureza.warbots.memory;


/**
 * Record containing the information a bot remembers about an item
 */
public class ItemMemoryRecord {

    /**
     * The states the record can be in
     */
    public enum State {
        /** The item is active */
        ACTIVE,

        /** The item is inactive and I know when it will become active again */
        INACTIVE_CERTAIN,

        /** The item is inactive, but I don't know when it will become active */
        INACTIVE_UNCERTAIN,

        /** I don't know if the item is active or inactive */
        UNKNOWN
    }

    /** Item' activation interval, in ms */
    private final long activationInterval;

    /** The item's state */
    private State state;

    /** Time left until the item becomes active again (if INACTIVE_CERTAIN) */
    private Long timeUntilNextActivation;

    /**
     * Amount of time for how long this item has been visible
     * Only applicable if the state is INACTIVE_UNCERTAIN
     */
    private Long timeHasBeenVisible;


    public ItemMemoryRecord(long activationInterval, State initialState) {
        this.activationInterval = activationInterval;
        this.state = initialState;
    }


    /**
     * Updates the memory record for this item
     */
    @SuppressWarnings("StatementWithEmptyBody")
    public void update(long dt, boolean visible, boolean isActive) {

        // If the bot knows the item is active, then it must be visible
        assert !isActive || visible;

        switch (state) {
            case ACTIVE:
                // For the state to be ACTIVE, the item must have been active
                // and visible before
                if (visible) {
                    // The item is still visible now
                    if (!isActive) {
                        // The item is no longer active and thus it must have
                        // been caught during the previous iteration (collisions
                        // are handled after the memory is updated)
                        this.state = State.INACTIVE_CERTAIN;
                        this.timeUntilNextActivation = activationInterval - dt;
                    } else {
                        // The item is still active. Nothing to do
                    }
                } else {
                    // The item is no longer visible. I don't know anymore if it
                    // is still active
                    this.state = State.UNKNOWN;
                }
                break;

            case INACTIVE_CERTAIN:
                this.timeUntilNextActivation -= dt;
                if (this.timeUntilNextActivation <= 0) {
                    // The item must have been activated now
                    // But we can only set its state to ACTIVE if the item is visible!
                    if (visible) {
                        assert isActive;

                        this.state = State.ACTIVE;
                        this.timeUntilNextActivation = null;
                    } else {
                        // The item has become active, but since we don't see it, it could
                        // have already been caught!
                        this.state = State.UNKNOWN;
                        this.timeUntilNextActivation = null;
                    }
                } else {
                    if (isActive) {
                        System.err.println("merda");
                    }
                    // If not enough time has passed isActive must be false!
                    assert !isActive : this.timeUntilNextActivation;
                }

                break;

            case INACTIVE_UNCERTAIN:
                if (visible) {
                    if (isActive) {
                        // The item has just become active, because we were
                        // looking at it
                        this.state = State.ACTIVE;
                        this.timeHasBeenVisible = null;
                    } else {
                        // The item is still inactive and in FOV
                        this.timeHasBeenVisible += dt;

                        // We have been looking at this item for more than the
                        // activation interval and nothing has happened?!
                        assert this.timeHasBeenVisible <= activationInterval;
                    }
                } else {
                    // We are not monitoring this item anymore. It could have
                    // become active and been caught again
                    this.state = State.UNKNOWN;
                    this.timeHasBeenVisible = null;
                }

                break;

            case UNKNOWN:
                if (visible) {
                    // The item has become visible, so now we know its real state!
                    if (isActive) {
                        // The item is active
                        this.state = State.ACTIVE;
                    } else {
                        // The item is inactive and we don't know when it will become active
                        this.state = State.INACTIVE_UNCERTAIN;
                        this.timeHasBeenVisible = dt;
                    }
                } else {
                    // The item is still invisible. Nothing to do
                }

                break;
        }
    }


    /**
     * Tell the memory that the item was caught by the same bot it belongs to
     *
     * This is necessary when the bot catches the item without seeing it (for
     * example, because it was sitting right on top of it).
     */
    public void caught() {
        this.state = State.INACTIVE_CERTAIN;
        this.timeUntilNextActivation = activationInterval;
    }


    public State getState() {
        return state;
    }


    public long getActivationInterval() {
        return activationInterval;
    }


    public Long getTimeUntilNextActivation() {
        return timeUntilNextActivation;
    }


    public Long getTimeHasBeenVisible() {
        return timeHasBeenVisible;
    }
}
