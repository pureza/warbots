package io.github.pureza.warbots;

import io.github.pureza.warbots.config.*;
import io.github.pureza.warbots.weaponry.Weapon;

import java.util.HashMap;
import java.util.Map;

public class TestConfig implements Config {

    private BotConfig botConfig;
    private Map<Weapon.WeaponType, WeaponConfig> weapons = new HashMap<>();
    private Map<Weapon.WeaponType, ProjectileConfig> projectiles = new HashMap<>();
    private FirstAidItemConfig firstAidItemConfig;
    private WeaponItemConfig weaponItemConfig;


    public TestConfig() {
        botConfig = new BotConfig() {
            @Override
            public double maxSpeed() {
                return 2;
            }

            @Override
            public double maxTurnRate() {
                return 0.2;
            }

            @Override
            public double boundingRadius() {
                return 0.3;
            }

            @Override
            public double aimNoise() {
                return 0.2;
            }
        };

        WeaponConfig handgunCfg = new WeaponConfig() {
            @Override
            public int initialAmmo() {
                return 60;
            }

            @Override
            public int maxAmmo() {
                return 120;
            }

            @Override
            public int fireRate() {
                return 5;
            }
        };


        WeaponConfig laserGunCfg = new WeaponConfig() {
            @Override
            public int initialAmmo() {
                return 30;
            }

            @Override
            public int maxAmmo() {
                return 80;
            }

            @Override
            public int fireRate() {
                return 2;
            }
        };

        WeaponConfig rocketLauncherCfg = new WeaponConfig() {
            @Override
            public int initialAmmo() {
                return 10;
            }

            @Override
            public int maxAmmo() {
                return 40;
            }

            @Override
            public int fireRate() {
                return 1;
            }
        };

        weapons.put(Weapon.WeaponType.HANDGUN, handgunCfg);
        weapons.put(Weapon.WeaponType.LASER_GUN, laserGunCfg);
        weapons.put(Weapon.WeaponType.ROCKET_LAUNCHER, rocketLauncherCfg);

        ProjectileConfig bulletCfg = new ProjectileConfig() {

            @Override
            public int speed() {
                return 25;
            }

            @Override
            public int damage() {
                return 5;
            }
        };

        ProjectileConfig laserRayCfg = new ProjectileConfig() {

            @Override
            public int speed() {
                return 10;
            }

            @Override
            public int damage() {
                return 10;
            }
        };


        ProjectileConfig rocketCfg = new RocketConfig() {

            @Override
            public int speed() {
                return 3;
            }

            @Override
            public int damage() {
                return 30;
            }

            @Override
            public double minExplosionRadius() {
                return 0.1;
            }

            @Override
            public double maxExplosionRadius() {
                return 1;
            }
        };

        projectiles.put(Weapon.WeaponType.HANDGUN, bulletCfg);
        projectiles.put(Weapon.WeaponType.LASER_GUN, laserRayCfg);
        projectiles.put(Weapon.WeaponType.ROCKET_LAUNCHER, rocketCfg);

        firstAidItemConfig = new FirstAidItemConfig() {
            @Override
            public double boundingRadius() {
                return 0.3;
            }

            @Override
            public long activationInterval() {
                return 10000;
            }

            @Override
            public int healthAmount() {
                return 30;
            }
        };

        weaponItemConfig = new WeaponItemConfig() {
            @Override
            public double boundingRadius() {
                return 0.3;
            }

            @Override
            public long activationInterval() {
                return 10000;
            }
        };
    }


    @Override
    public BotConfig botConfig() {
        return botConfig;
    }

    @Override
    public Map<Weapon.WeaponType, WeaponConfig> weapons() {
        return weapons;
    }

    @Override
    public Map<Weapon.WeaponType, ProjectileConfig> projectiles() {
        return projectiles;
    }

    @Override
    public double projectileBoundingRadius() {
        return 0.1;
    }

    @Override
    public FirstAidItemConfig firstAidItemConfig() {
        return firstAidItemConfig;
    }

    @Override
    public WeaponItemConfig weaponItemConfig() {
        return weaponItemConfig;
    }
}
