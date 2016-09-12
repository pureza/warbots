package io.github.pureza.warbots.entities;

import com.golden.gamedev.object.Sprite;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.geometry.Size;
import io.github.pureza.warbots.resources.Sprites;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * An inventory item
 *
 * An inventory item is any kind of item that lies on the map and can be picked
 * by a bot. After being picked, they disappear and become inactive for a while
 * (the activation interval).
 *
 * Note that an inventory item is placed at the center of its cell in the map.
 * There can only be one item per cell.
 *
 * Examples: weapons, first aid kits, etc
 */
public abstract class InventoryItem extends Entity {

    /** Item's bounding radius */
    protected final double boundingRadius;

    /** Activation interval, in ms */
    protected final long activationInterval;

    /** Is the item active? */
    protected boolean active;

    /** Time passed since the item was picked for the last time */
    protected long timeSinceDeactivation;

    /** The item's sprite */
    protected Sprite sprite;


    public InventoryItem(io.github.pureza.warbots.geometry.Point location, double boundingRadius, long activationInterval) {
        super(location);
        this.boundingRadius = boundingRadius;
        this.activationInterval = activationInterval;
        activate();
    }


    /**
     * Updates the item after some time has passed
     */
    public void update(long dt) {
        if (!this.active) {
            this.timeSinceDeactivation += dt;

            // Enable the item again if sufficient time has already passed.
            if (this.timeSinceDeactivation >= this.activationInterval) {
                activate();
            }
        }
    }


    /**
     * What to do when a bot catches this item?
     */
    public void caughtBy(Bot bot) {
        this.deactivate();
        applyTo(bot);

        // Remember that we caught this item
        bot.getMemory().getItemMemory().get(this).caught();
    }


    /**
     * Renders the item, but only if it is active
     */
    public void render(Graphics2D graphics) {
        if (this.active) {
            this.sprite.render(graphics);
        }
    }


    /**
     * Loads and positions the sprite corresponding to this item
     */
    public void loadSprite(Game game) {
        this.sprite = new Sprite(loadImage(game));

        // Set the sprite's location
        Size size = Sprites.CELL_SIZE;
        double x = this.getLocation().x() * size.width() - sprite.getWidth() / 2;
        double y = (game.getMap().height() - this.getLocation().y()) * size.height() - sprite.getHeight() / 2;
        this.sprite.setLocation(x, y);
    }


    /**
     * Returns the item's bounding radius.
     */
    public double getBoundingRadius() {
        return this.boundingRadius;
    }


    /**
     * Checks if this item is active
     */
    public boolean isActive() {
        return this.active;
    }


    /**
     * Activates this item
     */
    public void activate() {
        if (isActive()) {
            throw new IllegalStateException();
        }

        this.active = true;
    }


    /**
     * Deactivates the item
     */
    public void deactivate() {
        if (!isActive()) {
            throw new IllegalStateException();
        }

        this.active = false;
        timeSinceDeactivation = 0;
    }


    /**
     * Returns the item's activation interval
     */
    public long getActivationInterval() {
        return activationInterval;
    }


    /**
     * Loads the sprite corresponding to this item
     */
    protected abstract BufferedImage loadImage(com.golden.gamedev.Game game);


    /**
     * What to do to the bot when it catches this item?
     */
    protected abstract void applyTo(Bot bot);
}
