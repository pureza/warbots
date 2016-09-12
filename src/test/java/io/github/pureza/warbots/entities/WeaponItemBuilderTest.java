package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.Tests;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.weaponry.HandGun;
import io.github.pureza.warbots.weaponry.LaserGun;
import io.github.pureza.warbots.weaponry.RocketLauncher;
import io.github.pureza.warbots.weaponry.Weapon;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static io.github.pureza.warbots.Tests.mockBot;

public class WeaponItemBuilderTest {

    /*
     * WeaponItem build(Point location, Weapon.WeaponType weaponType)
     */

    @Test
    public void handGunItemBuildsHandGuns() {
        WeaponItemBuilder builder = new WeaponItemBuilder(new TestConfig());
        WeaponItem item = builder.build(Point.pt(1.5, 1.5), Weapon.WeaponType.HANDGUN);

        Bot bot = Tests.mockBot();
        item.applyTo(bot);

        verify(bot).acquireWeapon(any(HandGun.class));
    }


    @Test
    public void laserGunItemBuildsLaserGuns() {
        WeaponItemBuilder builder = new WeaponItemBuilder(new TestConfig());
        WeaponItem item = builder.build(Point.pt(1.5, 1.5), Weapon.WeaponType.LASER_GUN);

        Bot bot = Tests.mockBot();
        item.applyTo(bot);

        verify(bot).acquireWeapon(any(LaserGun.class));
    }


    @Test
    public void rocketLauncherItemBuildsRocketLaunchers() {
        WeaponItemBuilder builder = new WeaponItemBuilder(new TestConfig());
        WeaponItem item = builder.build(Point.pt(1.5, 1.5), Weapon.WeaponType.ROCKET_LAUNCHER);

        Bot bot = Tests.mockBot();
        item.applyTo(bot);

        verify(bot).acquireWeapon(any(RocketLauncher.class));
    }
}
