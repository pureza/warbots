package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.geometry.Point;

/**
 * Game entity
 *
 * Entities can either be static, such as walls, or movable, such as bots and
 * projectiles.
 */
public abstract class Entity {

    /** The location of the bottom left corner of the entity */
    private Point location;


    public Entity(Point location) {
        this.location = location;
    }


    public Point getLocation() {
        return location;
    }


    protected void setLocation(Point location) {
        this.location = location;
    }
}
