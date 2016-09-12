package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Vector;

import java.util.Random;

/**
 * A weapon.
 *
 * Each weapon has a fuzzy rule system that calculates the desirability of
 * firing it against a specified target, based on the distance to that target
 * (projectiles from different weapons travel at different speeds) and the
 * remaining ammunition.
 */
public abstract class Weapon {

    /**
     * Existing types of weapons
     */
    public enum WeaponType {
        HANDGUN, LASER_GUN, ROCKET_LAUNCHER
    }


    /** The bot that owns this weapon */
    protected final Bot owner;

    /** Maximum ammo */
    private final int maxAmmo;

    /** Remaining ammunition */
    private int remainingAmmo;

    /** Fire rate */
    private final int fireRate;

    /** The fuzzy evaluator used to calculate the desirability of using this weapon */
    private final WeaponEvaluator weaponEvaluator;

    /** The time when the last shot was fired. There is a minimum delay between two shots */
    private long lastShotTime;

    /** Builder for projectiles for this weapon */
    private ProjectileBuilder projectileBuilder;

    /** Random number generator to add some variability to the shooting accuracy */
    private Random random = new Random(123);


    /**
     * Creates a new weapon owned by some agent
     */
    public Weapon(Bot owner, int initialAmmo, int maxAmmo, int fireRate, ProjectileBuilder projectileBuilder) {
        this.owner = owner;
        this.maxAmmo = maxAmmo;
        this.fireRate = fireRate;
        this.weaponEvaluator = initEvaluator();
        this.remainingAmmo = initialAmmo;
        this.projectileBuilder = projectileBuilder;
    }


    /**
     * Increments the remaining ammunition
     */
    public void acquireAmmo(int ammoCount) {
        this.remainingAmmo = Math.min(this.remainingAmmo + ammoCount, maxAmmo);
    }


    public int getRemainingAmmo() {
        return this.remainingAmmo;
    }


    public int getMaxAmmo() {
        return maxAmmo;
    }


    /**
     * Initializes the fuzzy evaluator used to calculate the desirability
     * of using this weapon
     */
    protected abstract WeaponEvaluator initEvaluator();


    /**
     * Calculates the desirability to use this weapon, based on the distance to
     * the target and the number of remaining ammo.
     */
    public double desirability(double distanceToTarget) {
        return this.weaponEvaluator.evaluate(distanceToTarget, this.getRemainingAmmo());
    }


    /**
     * Fires the weapon
     *
     * This fires a projectile heading in the direction the agent is pointed
     * at, with a small random noise added.
     */
    public Projectile fire() {
        long now = System.currentTimeMillis();
        if (this.isReady(now) && this.remainingAmmo > 0) {
            this.remainingAmmo--;
            this.lastShotTime = now;
            Projectile projectile = this.createProjectile();

            // Set's the bullet's location so that it doesn't overlap with the shooter
            Vector displacement = owner.getHeadingVector()
                    .scalarMul(owner.getBoundingRadius() + projectile.getBoundingRadius());
            projectile.setLocation(projectile.getLocation().plus(displacement));
            return projectile;
        }

        return null;
    }


    public long getLastShotTime() {
        return lastShotTime;
    }


    public int getFireRate() {
        return fireRate;
    }


    /**
     * Adds some noise when the weapon is about to shoot, so that the bot
     * doesn't always hit its opponent
     *
     * The resulting angle is in radians.
     */
    protected double noise() {
        return owner.getAimNoise() * (2 * random.nextDouble() - 1);
    }


    /**
     * Checks if the weapon is ready to shoot
     *
     * The weapon is ready to shoot if enough time has passed since the last
     * shot, where "enough time" depends on the weapon's fire rate.
     */
    protected boolean isReady(long now) {
        return now - this.lastShotTime > (1000 / this.fireRate);
    }


    /**
     * Calculates the strength of the weapon
     *
     * The strength is a number between 0 (weak) and 1 (strong). This number is
     * used by the planning layer to decide if the carrier should attack or retreat.
     *
     * A strength of 0 means the weapon can't be used (no ammunition left), and
     * a strength of 1 means the weapon is fully loaded.
     */
    protected double individualWeaponStrength() {
        return (double) this.getRemainingAmmo() / (double) this.maxAmmo;
    }


    /**
     * Creates a projectile for this weapon
     */
    public Projectile createProjectile() {
        return projectileBuilder.build(this);
    }


    /**
     * Returns the type of this weapon
     */
    protected abstract WeaponType getWeaponType();
}
