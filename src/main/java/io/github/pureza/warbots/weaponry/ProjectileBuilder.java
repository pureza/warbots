package io.github.pureza.warbots.weaponry;


/**
 * Builder for projectiles
 *
 * Instantiates and configures projectiles according to the global game
 * configuration.
 */
public interface ProjectileBuilder {

    /**
     * Creates a Projectile that was fired from the given weapon
     */
    Projectile build(Weapon weapon);
}
