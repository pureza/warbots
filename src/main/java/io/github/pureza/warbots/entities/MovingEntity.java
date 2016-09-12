package io.github.pureza.warbots.entities;

import com.golden.gamedev.object.Sprite;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.resources.Sprites;
import io.github.pureza.warbots.geometry.Size;
import io.github.pureza.warbots.geometry.Vector;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Set;

/**
 * A moving entity
 *
 * Examples of moving entities are Bots and Projectiles.
 */
public abstract class MovingEntity extends Entity {

    /** The game */
    protected final Game game;

    /** Current velocity */
    protected Vector velocity;

    /** Current rotation angle (in radians) */
    protected double rotation;

    /** The maximum allowed speed at which this entity may move */
    protected final double maxSpeed;

    /** The maximum rate (radians/second) at which this entity can rotate */
    protected final double maxTurnRate;

    /** Entity's bounding radius, for collision detection */
    protected final double boundingRadius;

    /** Entity's previous location, also for collision detection */
    protected io.github.pureza.warbots.geometry.Point previousLocation;

    /** The path to the entity's sprite */
    protected final String spritePath;

    /** The sprite used by this entity */
    protected Sprite sprite;


    public MovingEntity(Game game, io.github.pureza.warbots.geometry.Point location, String spritePath, Vector velocity, double rotation,
                        double maxSpeed, double maxTurnRate, double boundingRadius) {
        super(location);
        this.game = game;
        this.spritePath = spritePath;
        this.velocity = velocity;
        this.rotation = rotation;
        this.maxSpeed = maxSpeed;
        this.maxTurnRate = maxTurnRate;
        this.boundingRadius = boundingRadius;
    }


    /**
     * Updates the position of this entity according to the velocity vector
     */
    public void update(long dt) {
        this.setLocation(this.getLocation().plus(this.velocity.scalarMul(dt / 1000.0)));
    }


    public void render(Graphics2D graphics) {
        // Change the sprite's position
        Size cellSize = Sprites.CELL_SIZE;
        double spriteX = getLocation().x() * cellSize.width() - sprite.getWidth() / 2;
        double spriteY = (game.getMap().height() - getLocation().y()) * cellSize.height() - sprite.getHeight() / 2;
        this.sprite.setLocation(spriteX, spriteY);

        // Rotate the sprite as needed
        AffineTransform transform = graphics.getTransform();
        graphics.rotate(-this.rotation, this.sprite.getX() + this.sprite.getWidth() / 2, this.sprite.getY() + this.sprite.getHeight() / 2);
        this.sprite.render(graphics);
        graphics.setTransform(transform);
    }


    /**
     * Converts a point from the local coordinate system to the world's
     * coordinate system.
     */
    public io.github.pureza.warbots.geometry.Point toWorldCoordinates(io.github.pureza.warbots.geometry.Point point) {
        return point.rotate(this.rotation).plus(getLocation());
    }


    /**
     * Returns the set of cells the entity is occupying
     */
    public Set<io.github.pureza.warbots.geometry.Point> getOccupyingCells() {
        return game.getMap().getOccupyingCells(getLocation(), getBoundingRadius());
    }


    public Vector getHeadingVector() {
        return new Vector(Math.cos(rotation), Math.sin(rotation));
    }


    public Vector getVelocity() {
        return velocity;
    }


    public double getRotation() {
        return rotation;
    }


    public void setRotation(double rotation) {
        this.rotation = rotation;
    }


    public double getMaxSpeed() {
        return maxSpeed;
    }


    public double getMaxTurnRate() {
        return maxTurnRate;
    }


    public double getBoundingRadius() {
        return this.boundingRadius;
    }


    public Game getGame() {
        return this.game;
    }


    @Override
    public void setLocation(io.github.pureza.warbots.geometry.Point location) {
        // Save previous location, because it may be restored on a collision
        this.previousLocation = this.getLocation();
        super.setLocation(location);
    }


    /**
     * Restores the previous location
     */
    public void restoreLocation() {
        this.setLocation(this.getPreviousLocation());

        // Forget about the previous location
        this.previousLocation = this.getLocation();
    }


    public io.github.pureza.warbots.geometry.Point getPreviousLocation() {
        return this.previousLocation;
    }


    /**
     * Loads the sprite corresponding to this item
     */
    public void initResources() {
        this.sprite = new Sprite(game.getImage(this.spritePath));
    }
}
