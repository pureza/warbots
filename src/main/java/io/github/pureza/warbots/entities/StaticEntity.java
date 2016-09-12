package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Size;

/**
 * A static entity
 *
 * A static entity is a physical and inanimate entity that lies somewhere on the
 * game map. Trees, walls and buildings are examples of static entities.
 *
 * The most important thing to remember about static entities, is that they
 * occupy one or more contiguous map cells. Thus, a cell can either be entirely
 * occupied by a static entity (and an agent can't move over there), or
 * completely free. No cell can be partially occupied and partially free. This
 * simplifies geometry calculations, needed by modules like path-finding and
 * navigation.
 */
public abstract class StaticEntity extends Entity {

    /**
     * The size of the entity, i.e, the number of cells it occupies to the right
     * and above the location
     */
    private final Size size;


    public StaticEntity(Point location, Size size) {
        super(location);
        this.size = size;
    }


    /**
     * Returns the size of this entity, i.e., the number of cells it occupies
     * to the right and above the location
     */
    public Size getSize() {
        return size;
    }
}
