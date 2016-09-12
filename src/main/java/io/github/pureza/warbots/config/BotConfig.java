package io.github.pureza.warbots.config;


/**
 * Bot configuration
 */
public interface BotConfig {

    /**
     * Maximum speed
     */
    double maxSpeed();


    /**
     * Maximum turn rate, in radians
     */
    double maxTurnRate();


    /**
     * Bounding radius for the bot
     */
    double boundingRadius();


    /**
     * The random noise that makes projectile's direction unpredictable
     */
    double aimNoise();
}
