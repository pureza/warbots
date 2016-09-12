package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.resources.Sprites;
import io.github.pureza.warbots.entities.StaticEntity;
import io.github.pureza.warbots.geometry.Vector;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Rockets are fired by rocket launchers
 *
 * Rockets are a bit different from the regular projectiles, in that when they
 * collide with something, they explode and inflict damage upon any bot that is
 * nearby. Also, note that this damage is proportional to the distance between
 * the bot and the center of the explosion.
 */
public class Rocket extends Projectile {

    /** Is the rocket exploding? */
    private boolean exploding;

    /** Is the rocket's explosion radius decreasing? */
    private boolean backwards = false;

    /** The center of the explosion, in screen coordinates */
    private Point2D explosionCenter;

    /** The explosion radius */
    private double explosionRadius;

    /** The minimum (and initial) explosion radius */
    private double minExplosionRadius;

    /** The maximum explosion radius */
    private double maxExplosionRadius;


    public Rocket(Game game, Bot shooter, io.github.pureza.warbots.geometry.Point location, double rotation, double speed, double boundingRadius,
                  int damage, double minExplosionRadius, double maxExplosionRadius) {
        super(game, Sprites.SPRITE_ROCKET_LAUNCHER_PATH, shooter, location,
                new Vector(Math.cos(rotation), Math.sin(rotation)).scalarMul(speed),
                rotation, boundingRadius, damage);
        this.exploding = false;
        this.minExplosionRadius = minExplosionRadius;
        this.maxExplosionRadius = maxExplosionRadius;
    }


    @Override
    public void hitBot(Bot bot) {
        explode();
    }


    @Override
    public void hitStaticEntity(StaticEntity entity) {
        explode();
    }


    /**
     * If this rocket is exploding, updates the radius of the explosion.
     */
    @Override
    public void update(long dt) {
        if (this.exploding) {
            if (!backwards) {
                // The radius is still growing
                explosionRadius += dt / 150.0;
            } else {
                // The radius is decreasing
                explosionRadius -= dt / 300.0;
            }

            if (explosionRadius >= maxExplosionRadius) {
                backwards = true;
            }

            if (this.explosionRadius < minExplosionRadius) {
                game.removeProjectile(this);
            }
        }

        super.update(dt);
    }


    @Override
    public void render(Graphics2D graphics) {
        if (!exploding) {
            super.render(graphics);
        } else if (this.explosionRadius < 0.1) {
            game.removeProjectile(this);
        } else {
            renderExplosion(graphics);
        }
    }


    /**
     * Explodes the rocket
     *
     * Stops the projectile, starts the explosion animation and inflicts
     * damage upon nearby bots.
     */
    void explode() {
        // The if is to make sure we only hit the entity once
        if (!exploding) {
            this.velocity = new Vector(0, 0);

            this.explosionCenter = new Point2D.Double(this.sprite.getX() + this.sprite.getWidth() / 2, this.sprite.getY()  + this.sprite.getHeight() / 2);
            this.explosionRadius = minExplosionRadius;
            this.exploding = true;

            // Inflict the damage upon the bots in range
            inflictDamageNearby();
        }
    }


    public boolean isExploding() {
        return exploding;
    }


    public double getExplosionRadius() {
        return explosionRadius;
    }


    /**
     * Gets the bots in range, calculates the damage to inflict upon each one
     * (based on the distance from the center of the explosion), and applies it
     */
    private void inflictDamageNearby() {
        for (Bot bot : game.getBotsInRange(this.getLocation(), maxExplosionRadius)) {
            double distance = this.getLocation().distanceTo(bot.getLocation());
            int damage = (int) ((1 - distance / maxExplosionRadius) * this.getDamage());
            assert damage >= 0 : damage;
            bot.inflictDamage(this, damage);
        }
    }


    /**
     * Renders the explosion
     */
    private void renderExplosion(Graphics2D graphics) {
        int radius = (int) (explosionRadius * Sprites.CELL_SIZE.width());

        // Gradient options
        float[] dist = { 0f, 1f };
        Color[] colors = { Color.YELLOW,  new Color(255, 165, 0, 25) };
        RadialGradientPaint p = new RadialGradientPaint(explosionCenter, radius, dist, colors);

        // Set paint options
        graphics.setPaint(p);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        graphics.fillOval((int) explosionCenter.getX() - radius, (int) explosionCenter.getY() - radius, 2 * radius, 2 * radius);
    }
}
