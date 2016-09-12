package io.github.pureza.warbots.config;


import io.github.pureza.warbots.weaponry.Weapon;

import java.util.Map;

/**
 * Application configuration
 */
public interface Config {

    /**
     * Bot configuration
     */
    BotConfig botConfig();


    /**
     * Weapons configuration
     */
    Map<Weapon.WeaponType, WeaponConfig> weapons();


    /**
     * Projectiles configuration
     */
    Map<Weapon.WeaponType, ProjectileConfig> projectiles();


    /**
     * Projectile's bounding radius
     */
    double projectileBoundingRadius();


    /**
     * First aid item configuration
     */
    FirstAidItemConfig firstAidItemConfig();


    /**
     * Weapon item configuration
     */
    WeaponItemConfig weaponItemConfig();
}
