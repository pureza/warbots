package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.config.BotConfig;
import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.weaponry.HandGunBuilder;
import io.github.pureza.warbots.weaponry.LaserGunBuilder;
import io.github.pureza.warbots.weaponry.RocketLauncherBuilder;


/**
 * Builder for bots
 *
 * Builds bots according to the given settings, defaulting to the global
 * game configuration.
 */
public class BotBuilder {

    /** The maximum speed of the bot */
    private double maxSpeed;

    /** The maximum turn rate of the bot */
    private double maxTurnRate;

    /** The bot's bounding radius */
    private double boundingRadius;

    /** Bot's aim noise (i.e., max deviation added to the aim angle) */
    private double aimNoise;

    /** The global game configuration */
    private final Config config;


    public BotBuilder(Config config) {
        this.config = config;
        BotConfig botConfig = config.botConfig();

        setMaxSpeed(botConfig.maxSpeed());
        setMaxTurnRate(botConfig.maxTurnRate());
        setBoundingRadius(botConfig.boundingRadius());
        setAimNoise(botConfig.aimNoise());
    }


    /**
     * Sets the maximum speed of the bot
     */
    public BotBuilder setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
        return this;
    }


    /**
     * Sets the maximum turn rate of the bot
     */
    public BotBuilder setMaxTurnRate(double maxTurnRate) {
        this.maxTurnRate = maxTurnRate;
        return this;
    }


    /**
     * Sets the bounding radius of the bot
     */
    public BotBuilder setBoundingRadius(double boundingRadius) {
        this.boundingRadius = boundingRadius;
        return this;
    }


    /**
     * Sets the aim noise of the bot
     */
    public BotBuilder setAimNoise(double aimNoise) {
        this.aimNoise = aimNoise;
        return this;
    }


    /**
     * Builds a bot according to the configuration
     *
     * Also loads the bot with some pre-configured guns.
     */
    public Bot build(Game game, Point location, Team team) {
        Bot bot = new Bot(game, location, team, maxSpeed, maxTurnRate, boundingRadius,
                config.projectileBoundingRadius(), aimNoise);

        bot.acquireWeapon(new HandGunBuilder(config).build(bot));
        bot.acquireWeapon(new LaserGunBuilder(config).build(bot));
        bot.acquireWeapon(new RocketLauncherBuilder(config).build(bot));

        return bot;
    }
}
