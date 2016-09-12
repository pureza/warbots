package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.config.RocketConfig;
import io.github.pureza.warbots.config.WeaponConfig;
import io.github.pureza.warbots.entities.Bot;


/**
 * Builder for Rocket Launchers
 *
 * Creates RocketLauncher instances and sets them up according to the global
 * game configuration.
 */
public class RocketLauncherBuilder implements WeaponBuilder {

    /** The global configuration */
    private final Config config;


    public RocketLauncherBuilder(Config config) {
        this.config = config;
    }


    @Override
    public Weapon build(Bot owner) {
        WeaponConfig rocketConfig = rocketConfig();
        return new RocketLauncher(owner, rocketConfig.initialAmmo(), rocketConfig.maxAmmo(), rocketConfig.fireRate(),
                bulletBuilder(owner));
    }


    private WeaponConfig rocketConfig() {
        return config.weapons().get(Weapon.WeaponType.ROCKET_LAUNCHER);
    }


    /**
     * Creates a builder to build rockets from this weapon
     */
    private ProjectileBuilder bulletBuilder(Bot owner) {
        RocketConfig rocketConfig = (RocketConfig) config.projectiles().get(Weapon.WeaponType.ROCKET_LAUNCHER);
        return (weapon) ->
                new Rocket(owner.getGame(), owner, owner.getLocation(), owner.getRotation() + weapon.noise(),
                        rocketConfig.speed(), config.projectileBoundingRadius(), rocketConfig.damage(),
                        rocketConfig.minExplosionRadius(), rocketConfig.maxExplosionRadius());
    }
}
