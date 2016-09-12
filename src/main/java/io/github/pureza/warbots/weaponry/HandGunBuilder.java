package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.config.ProjectileConfig;
import io.github.pureza.warbots.config.WeaponConfig;
import io.github.pureza.warbots.entities.Bot;


/**
 * Builder for HandGuns
 *
 * Creates HandGun instances and sets them up according to the global game
 * configuration.
 */
public class HandGunBuilder implements WeaponBuilder {

    /** The global configuration */
    private final Config config;


    public HandGunBuilder(Config config) {
        this.config = config;
    }


    @Override
    public Weapon build(Bot owner) {
        WeaponConfig handGunConfig = handGunConfig();
        return new HandGun(owner, handGunConfig.initialAmmo(), handGunConfig.maxAmmo(), handGunConfig.fireRate(),
                bulletBuilder(owner));
    }


    private WeaponConfig handGunConfig() {
        return config.weapons().get(Weapon.WeaponType.HANDGUN);
    }


    /**
     * Creates a builder to build bullets from this weapon
     */
    private ProjectileBuilder bulletBuilder(Bot owner) {
        ProjectileConfig bulletConfig = config.projectiles().get(Weapon.WeaponType.HANDGUN);
        return (weapon) ->
                new Bullet(owner.getGame(), owner, owner.getLocation(), owner.getRotation() + weapon.noise(),
                        bulletConfig.speed(), config.projectileBoundingRadius(), bulletConfig.damage());
    }
}
