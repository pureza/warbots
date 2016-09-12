package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.geometry.Vector;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.MovingEntity;
import io.github.pureza.warbots.entities.StaticEntity;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.geometry.Point;


/**
 * A projectile fired by a weapon
 *
 * A projectile is a moving entity, but, unlike bots, its velocity remains
 * constant until a collision happens. Then, the projectile stops, inflicts
 * damage in bots and is removed from the world.
 */
public abstract class Projectile extends MovingEntity {

    /** Who shot this projectile? */
    protected Bot shooter;

    /** Damage inflicted by this projectile on the enemy */
    protected int damage;


    /**
     * Creates and initializes a new projectile, ready to fly and hit someone
     */
    public Projectile(Game game, String spritePath, Bot shooter, Point location, Vector velocity, double rotation,
                      double boundingRadius, int damage) {
        super(game, location, spritePath, velocity, rotation, velocity.norm(), 0, boundingRadius);

        this.shooter = shooter;
        this.damage = damage;
    }


    /**
     * The damage inflicted by this projectile, when it hits an enemy
     */
    public int getDamage() {
        return this.damage;
    }


    /**
     * Called when the projectile hits a bot
     *
     * By default, inflicts damage in the bot (if it's an opponent) and removes
     * the projectile from the world.
     */
    public void hitBot(Bot bot) {
        game.removeProjectile(this);

        if (!this.shooter.isSameTeam(bot)) {
            bot.inflictDamage(this, getDamage());
        }
    }


    /**
     * Called when the projectile hits a wall or other kind of static entity
     * (i.e., everything in the map except bots and other projectiles)
     *
     * By default, removes the projectile from the game.
     */
    public void hitStaticEntity(StaticEntity entity) {
        game.removeProjectile(this);
    }
}
