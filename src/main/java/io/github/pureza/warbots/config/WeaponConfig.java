package io.github.pureza.warbots.config;


/**
 * Weapon configuration
 */
public interface WeaponConfig {

    /**
     * Initial ammunition
     */
    int initialAmmo();


    /**
     * Maximum ammunition
     */
    int maxAmmo();


    /**
     * Number of projectiles per unit of time this weapon can shoot
     */
    int fireRate();
}
