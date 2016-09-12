package io.github.pureza.warbots.config;

import io.github.pureza.warbots.resources.PropertiesReader;
import io.github.pureza.warbots.weaponry.Weapon;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configuration loader
 */
public class ConfigLoader {

    /**
     * Loads the configuration from the application.properties file
     */
    public static Config load() throws IOException {
        Properties props = new Properties();
        props.load(ConfigLoader.class.getResourceAsStream("/application.properties"));
        PropertiesReader reader = new PropertiesReader(props);

        BotConfig botConfig = loadBotConfig(reader);
        Map<Weapon.WeaponType, WeaponConfig> weaponsConfig = loadWeaponsConfig(reader);
        Map<Weapon.WeaponType, ProjectileConfig> projectilesConfig = loadProjectilesConfig(reader);
        FirstAidItemConfig firstAidItemConfig = loadFirstAidItemConfig(reader);
        WeaponItemConfig weaponItemConfig = loadWeaponItemConfig(reader);
        double projectileBoundingRadius = reader.getDouble("projectiles.bounding-radius");

        return new Config() {

            @Override
            public BotConfig botConfig() {
                return botConfig;
            }

            @Override
            public Map<Weapon.WeaponType, WeaponConfig> weapons() {
                return weaponsConfig;
            }

            @Override
            public Map<Weapon.WeaponType, ProjectileConfig> projectiles() {
                return projectilesConfig;
            }

            @Override
            public double projectileBoundingRadius() {
                return projectileBoundingRadius;
            }

            @Override
            public FirstAidItemConfig firstAidItemConfig() {
                return firstAidItemConfig;
            }

            @Override
            public WeaponItemConfig weaponItemConfig() {
                return weaponItemConfig;
            }
        };
    }


    /**
     * Loads the bot's configuration
     */
    private static BotConfig loadBotConfig(PropertiesReader reader) {
        return new BotConfig() {

            private double maxSpeed = reader.getDouble("bot.max-speed");
            private double maxTurnRate = reader.getDouble("bot.max-turn-rate");
            private double boundingRadius = reader.getDouble("bot.bounding-radius");
            private double aimNoise = reader.getDouble("bot.aim-noise");


            @Override
            public double maxSpeed() {
                return maxSpeed;
            }

            @Override
            public double maxTurnRate() {
                return maxTurnRate;
            }

            @Override
            public double boundingRadius() {
                return boundingRadius;
            }

            @Override
            public double aimNoise() {
                return aimNoise;
            }
        };
    }


    /**
     * Loads the weapon's configuration
     */
    private static Map<Weapon.WeaponType, WeaponConfig> loadWeaponsConfig(PropertiesReader reader) {
        WeaponConfig handgunCfg = new WeaponConfig() {

            private int initialAmmo = reader.getInt("weapons.handgun.initial-ammo");
            private int maxAmmo = reader.getInt("weapons.handgun.max-ammo");
            private int fireRate = reader.getInt("weapons.handgun.fire-rate");

            @Override
            public int initialAmmo() {
                return initialAmmo;
            }

            @Override
            public int maxAmmo() {
                return maxAmmo;
            }

            @Override
            public int fireRate() {
                return fireRate;
            }
        };

        WeaponConfig laserGunCfg = new WeaponConfig() {

            private int initialAmmo = reader.getInt("weapons.laser-gun.initial-ammo");
            private int maxAmmo = reader.getInt("weapons.laser-gun.max-ammo");
            private int fireRate = reader.getInt("weapons.laser-gun.fire-rate");

            @Override
            public int initialAmmo() {
                return initialAmmo;
            }

            @Override
            public int maxAmmo() {
                return maxAmmo;
            }

            @Override
            public int fireRate() {
                return fireRate;
            }
        };


        WeaponConfig rocketLauncherCfg = new WeaponConfig() {

            private int initialAmmo = reader.getInt("weapons.rocket-launcher.initial-ammo");
            private int maxAmmo = reader.getInt("weapons.rocket-launcher.max-ammo");
            private int fireRate = reader.getInt("weapons.rocket-launcher.fire-rate");

            @Override
            public int initialAmmo() {
                return initialAmmo;
            }

            @Override
            public int maxAmmo() {
                return maxAmmo;
            }

            @Override
            public int fireRate() {
                return fireRate;
            }
        };

        Map<Weapon.WeaponType, WeaponConfig> config = new HashMap<>();
        config.put(Weapon.WeaponType.HANDGUN, handgunCfg);
        config.put(Weapon.WeaponType.LASER_GUN, laserGunCfg);
        config.put(Weapon.WeaponType.ROCKET_LAUNCHER, rocketLauncherCfg);
        return config;
    }


    /**
     * Loads the projectiles configuration
     */
    private static Map<Weapon.WeaponType, ProjectileConfig> loadProjectilesConfig(PropertiesReader reader) {
        ProjectileConfig bulletCfg = new ProjectileConfig() {

            private int speed = reader.getInt("projectiles.bullet.speed");
            private int damage = reader.getInt("projectiles.bullet.damage");

            @Override
            public int speed() {
                return speed;
            }

            @Override
            public int damage() {
                return damage;
            }
        };

        ProjectileConfig laserRayCfg = new ProjectileConfig() {

            private int speed = reader.getInt("projectiles.laser-ray.speed");
            private int damage = reader.getInt("projectiles.laser-ray.damage");

            @Override
            public int speed() {
                return speed;
            }

            @Override
            public int damage() {
                return damage;
            }
        };


        ProjectileConfig rocketCfg = new RocketConfig() {

            private int speed = reader.getInt("projectiles.rocket.speed");
            private int damage = reader.getInt("projectiles.rocket.damage");
            private double minExplosionRadius = reader.getDouble("projectiles.rocket.min-explosion-radius");
            private double maxExplosionRadius = reader.getDouble("projectiles.rocket.max-explosion-radius");

            @Override
            public int speed() {
                return speed;
            }

            @Override
            public int damage() {
                return damage;
            }

            @Override
            public double minExplosionRadius() {
                return minExplosionRadius;
            }

            @Override
            public double maxExplosionRadius() {
                return maxExplosionRadius;
            }
        };

        Map<Weapon.WeaponType, ProjectileConfig> config = new HashMap<>();
        config.put(Weapon.WeaponType.HANDGUN, bulletCfg);
        config.put(Weapon.WeaponType.LASER_GUN, laserRayCfg);
        config.put(Weapon.WeaponType.ROCKET_LAUNCHER, rocketCfg);
        return config;
    }


    /**
     * Loads the configuration of a first aid item
     */
    private static FirstAidItemConfig loadFirstAidItemConfig(PropertiesReader reader) {
        return new FirstAidItemConfig() {

            private double boundingRadius = reader.getDouble("entities.first-aid-item.bounding-radius");
            private int activationInterval = reader.getInt("entities.first-aid-item.activation-interval");
            private int healthAmount = reader.getInt("entities.first-aid-item.health-amount");

            @Override
            public double boundingRadius() {
                return boundingRadius;
            }

            @Override
            public long activationInterval() {
                return activationInterval;
            }

            @Override
            public int healthAmount() {
                return healthAmount;
            }
        };
    }


    /**
     * Loads the configuration of a weapon item
     */
    private static WeaponItemConfig loadWeaponItemConfig(PropertiesReader reader) {
        return new WeaponItemConfig() {

            private double boundingRadius = reader.getDouble("entities.weapon-item.bounding-radius");
            private int activationInterval = reader.getInt("entities.weapon-item.activation-interval");

            @Override
            public double boundingRadius() {
                return boundingRadius;
            }

            @Override
            public long activationInterval() {
                return activationInterval;
            }
        };
    }
}
