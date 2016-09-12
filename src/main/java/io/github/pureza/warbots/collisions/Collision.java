package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.entities.Entity;

/**
 * A collision between two entities
 */
public abstract class Collision {

    /** The first entity */
    protected final Entity first;

    /** The other entity */
    protected final Entity second;


    public Collision(Entity entityA, Entity second) {
        this.first = entityA;
        this.second = second;
    }


    /**
     * Handle the collision
     */
    public abstract void handle();


    public Entity first() {
        return first;
    }


    public Entity second() {
        return second;
    }
}
