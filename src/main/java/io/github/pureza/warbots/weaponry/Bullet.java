package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Vector;
import io.github.pureza.warbots.resources.Sprites;

/**
 * A projectile fired by the handgun
 */
public class Bullet extends Projectile {

    public Bullet(Game game, Bot shooter, Point location, double rotation, double speed, double boundingRadius,
                  int damage) {
        super(game, Sprites.SPRITE_BULLET_PATH, shooter, location,
                new Vector(Math.cos(rotation), Math.sin(rotation)).scalarMul(speed),
                rotation, boundingRadius, damage);
    }
}
