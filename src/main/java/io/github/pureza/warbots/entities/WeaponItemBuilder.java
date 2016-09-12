package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.config.WeaponItemConfig;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.weaponry.*;
import io.github.pureza.warbots.config.Config;


/**
 * Builder for weapon items
 *
 * Builds weapon items according to the global game configuration.
 */
public class WeaponItemBuilder {

    /** The game configuration */
    private final Config config;


    public WeaponItemBuilder(Config config) {
        this.config = config;
    }


    /**
     * Builds a weapon item at the given location
     */
    public WeaponItem build(Point location, Weapon.WeaponType weaponType) {
        WeaponBuilder weaponBuilder = null;
        switch (weaponType) {
            case HANDGUN:
                weaponBuilder = new HandGunBuilder(config);
                break;
            case LASER_GUN:
                weaponBuilder = new LaserGunBuilder(config);
                break;
            case ROCKET_LAUNCHER:
                weaponBuilder = new RocketLauncherBuilder(config);
                break;
        }

        WeaponItemConfig cfg = config.weaponItemConfig();
        return new WeaponItem(location, weaponType, cfg.boundingRadius(), cfg.activationInterval(), weaponBuilder);
    }
}
