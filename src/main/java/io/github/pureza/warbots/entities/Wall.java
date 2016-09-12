package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Size;

/**
 * A wall
 */
public class Wall extends StaticEntity {

    public Wall(Point location, Size size) {
        super(location, size);
    }
}
