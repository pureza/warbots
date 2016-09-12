package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.ai.BotAI;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.geometry.Vector;
import io.github.pureza.warbots.memory.Memory;
import io.github.pureza.warbots.navigation.PathPlanner;
import io.github.pureza.warbots.steering.PursuitBehavior;
import io.github.pureza.warbots.steering.SteeringBehavior;
import io.github.pureza.warbots.weaponry.Projectile;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.navigation.Map;
import io.github.pureza.warbots.search.Path;
import io.github.pureza.warbots.steering.SeekBehavior;
import io.github.pureza.warbots.util.Event;
import io.github.pureza.warbots.weaponry.Artillery;
import io.github.pureza.warbots.weaponry.Weapon;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Just a simple bot.
 */
public class Bot extends MovingEntity {

    /** Bot's team */
    private final Team team;

    /** Bot's health */
    private int health;

    /** Is it dead? */
    private boolean killed = false;

    /** Bot's aim noise (i.e., max deviation added to the aim angle) */
    private final double aimNoise;

    /** Projectile's bounding radius */
    private final double projectilesBoundingRadius;

    /** Steering behavior in use */
    private SteeringBehavior steeringBehavior;

    /** Bot's path planner */
    private PathPlanner pathPlanner;

    /** Bot's weapons' manager */
    private Artillery artillery;

    /** Bot's AI Engine */
    private BotAI botAI;

    /** The bot's memory */
    private Memory memory;

    /** Event fired when this moving entity collides with some static entity */
    private Event<StaticEntity> onCollisionWithStaticEntity = new Event<>();

    /** Event fired when this moving entity collides with another bot */
    private Event<Bot> onCollisionWithBot = new Event<>();


    public Bot(Game game, Point location, Team team, double maxSpeed, double maxTurnRate, double boundingRadius,
               double projectilesBoundingRadius, double aimNoise) {
        super(game, location, team.getTeamIconPath(), new Vector(0, 0),
                0, maxSpeed, maxTurnRate, boundingRadius);

        this.team = team;

        this.aimNoise = aimNoise;
        this.projectilesBoundingRadius = projectilesBoundingRadius;
        this.health = 100;

        // Initialize weapons
        this.artillery = new Artillery(this);

        this.steeringBehavior = null;
        this.pathPlanner = new PathPlanner(this);

        this.memory = new Memory(this);

        // Initialize the AI engine
        this.botAI = new BotAI(this);
    }


    @Override
    public void update(long dt) {
        // Get the current steering behavior's desired velocity
        Vector desiredVelocity = this.steeringBehavior != null
                ? this.steeringBehavior.calculateVelocity(dt)
                : Vector.vec(0, 0);

        this.velocity = desiredVelocity.truncate(this.maxSpeed);

        // Update the memory
        this.memory.update(dt);

        // Update the brain
        this.botAI.update(dt);

        // Update position
        super.update(dt);
    }


    /**
     * Increases the health by a given amount, up to the maximum allowed
     */
    public void acquireHealth(int amount) {
        this.health = Math.min(this.health + amount, 100);
    }


    /**
     * Decreases the health by a given amount
     *
     * If the health reaches 0, the bot dies.
     */
    public void inflictDamage(Projectile projectile, int damage) {
        this.health = Math.max(this.health - damage, 0);

        // Remember where the shot came from
        this.memory.getShotMemory().store(projectile.getHeadingVector().scalarMul(-1));

        if (health <= 0) {
            this.die();
        }
    }


    /**
     * Checks if the bot is dead
     */
    public boolean isDead() {
        return killed;
    }


    /**
     * Acquires a weapon
     * 
     * If the bot is already carrying one of these, acquire
     * only the available ammunition (up to the maximum limit).
     */
    public void acquireWeapon(Weapon weapon) {
        this.artillery.acquire(weapon);
    }


    /**
     * Checks if this bot can see an entity
     */
    public boolean isInFov(Entity other) {
        // I can't see myself.
        if (this == other) {
            return false;
        }

        // The vector that points in the direction ahead of the bot
        Vector heading = this.getHeadingVector();

        // The vector that points to the other bot
        Vector toOther = other.getLocation().minus(this.getLocation());

        // If the angle between the heading vector and the vector that points
        // towards the other bot is less than 90º and there is no obstacle
        // between us, then I can see it
        return heading.angleWith(toOther) < Math.PI / 2
                && !game.getMap().isPathObstructed(this.getLocation(), other.getLocation());
    }


    /**
     * Returns the bots that this bot can see
     */
    public List<Bot> getBotsInFov() {
        return game.getBots().stream()
                .filter(this::isInFov)
                .collect(Collectors.toList());
    }


    /**
     * Checks if there is a clear shot between this bot and an enemy bot
     */
    public boolean isShootable(Bot enemy) {
        // XXX This method basically forces all projectiles to have the same
        // XXX bounding radius
        return getGame().getMap().canMoveBetween(getLocation(), enemy.getLocation(),
                projectilesBoundingRadius);
    }


    /**
     * Checks if the bot can move directly to a target point
     */
    public boolean canMoveTo(Point target) {
        Map map = getGame().getMap();
        return map.canMoveTo(getLocation(), target, boundingRadius);
    }


    /**
     * Moves the bot towards the target location using the Seek steering
     * behavior
     */
    public void seek(Point target) {
        this.steeringBehavior = new SeekBehavior(this, target);
    }


    /**
     * Moves the bot towards a target bot by trying to predict its future
     * position
     */
    public void pursue(Bot target) {
        this.steeringBehavior = new PursuitBehavior(this, target);
    }


    /**
     * Stops the bot
     */
    public void stop() {
        this.steeringBehavior = null;
    }


    /**
     * Finds a path between the bot and the given target position
     * 
     * Assumes that such a path exists.
     */
    public Path<Point> findPathTo(Point target) {
        return pathPlanner.findPathTo(target);
    }


    /**
     * Shoots at some other bot
     * 
     * The enemy bot must be inside the field of view.
     */
    public void fireAt(Bot other, long dt) {
        Projectile projectile = this.artillery.fireAt(other, dt);
        if (projectile != null) {
            this.getGame().addProjectile(projectile);
            projectile.initResources();
        }
    }


    /**
     * Rotates the bot towards the direction vector
     * 
     * Rotates a maximum of maxTurnRate degrees on each call.
     * 
     * Returns true if the bot is pointing towards the given direction, and
     * false otherwise.
     */
    public boolean rotateFacing(Vector direction, long dt) {
        if (direction.isNull()) {
            throw new IllegalArgumentException("Rotate facing null vector");
        }

        Vector heading = this.getHeadingVector();

        double angleBetween = heading.angleWith(direction);
        double maxAngle = maxTurnRate / (1000.0 / dt);
        double rotateBy = Math.min(angleBetween, maxAngle);

        // Rotate in clockwise or anti-clockwise direction?
        int sign = heading.cross(direction) > 0 ? 1 : -1;
        this.setRotation(this.getRotation() + rotateBy * sign);

        return rotateBy == angleBetween;
    }


    public double getAimNoise() {
        return this.aimNoise;
    }


    public int getHealth() {
        return health;
    }


    public Team getTeam() {
        return this.team;
    }


    /**
     * Checks if some other bot is from my team
     */
    public boolean isSameTeam(Bot other) {
        return getTeam() == other.getTeam();
    }


    /**
     * Averages the sum of each weapon's individual strength to obtain the total
     * "weapon's strength" of this bot. This number is then used by the planning
     * layer to decide if the carrier should attack or retreat
     */
    public double getTotalWeaponStrength() {
        return this.artillery.totalWeaponStrength();
    }


    SteeringBehavior getSteeringBehavior() {
        return steeringBehavior;
    }


    Artillery getArtillery() {
        return artillery;
    }


    public Memory getMemory() {
        return memory;
    }


    /**
     * Returns the event fired when the bot collides with a static entity
     */
    public Event<StaticEntity> onCollisionWithStaticEntity() {
        return this.onCollisionWithStaticEntity;
    }


    /**
     * Returns the event fired when the bot collides with another bot
     */
    public Event<Bot> onCollisionWithBot() {
        return this.onCollisionWithBot;
    }


    /**
     * Kill the bot
     */
    private void die() {
        this.killed = true;
        game.removeBot(this);
    }


    @Override
    public void render(Graphics2D graphics) {
        super.render(graphics);
    }
}
