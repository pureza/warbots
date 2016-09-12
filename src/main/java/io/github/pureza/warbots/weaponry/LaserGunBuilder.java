package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.config.ProjectileConfig;
import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.config.WeaponConfig;
import io.github.pureza.warbots.entities.Bot;


/**
 * Builder for LaserGuns
 *
 * Creates LaserGun instances and sets them up according to the global game
 * configuration.
 */
public class LaserGunBuilder implements WeaponBuilder {

    /** The global configuration */
    private final Config config;


    public LaserGunBuilder(Config config) {
        this.config = config;
    }


    @Override
    public Weapon build(Bot owner) {
        WeaponConfig laserGunConfig = laserGunConfig();
        return new LaserGun(owner, laserGunConfig.initialAmmo(), laserGunConfig.maxAmmo(), laserGunConfig.fireRate(),
                laserRayBuilder(owner));
    }


    private WeaponConfig laserGunConfig() {
        return config.weapons().get(Weapon.WeaponType.LASER_GUN);
    }


    /**
     * Creates a builder to build laser rays from this weapon
     */
    private ProjectileBuilder laserRayBuilder(Bot owner) {
        ProjectileConfig bulletConfig = config.projectiles().get(Weapon.WeaponType.LASER_GUN);
        return (weapon) ->
                new LaserRay(owner.getGame(), owner, owner.getLocation(), owner.getRotation() + weapon.noise(), bulletConfig.speed(),
                        config.projectileBoundingRadius(), bulletConfig.damage());
    }
}
