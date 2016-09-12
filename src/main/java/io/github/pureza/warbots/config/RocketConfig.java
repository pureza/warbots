package io.github.pureza.warbots.config;


/**
 * Rocket configuration
 */
public interface RocketConfig extends ProjectileConfig {

    /**
     * Minimum (and initial) radius of the explosion
     */
    double minExplosionRadius();


    /**
     * Maximum radius of the explosion
     */
    double maxExplosionRadius();
}
