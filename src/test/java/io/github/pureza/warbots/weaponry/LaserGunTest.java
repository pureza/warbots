package io.github.pureza.warbots.weaponry;

import org.junit.Test;
import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.config.WeaponConfig;
import io.github.pureza.warbots.entities.Bot;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static io.github.pureza.warbots.Tests.buildLaserGun;
import static io.github.pureza.warbots.Tests.mockBot;

public class LaserGunTest {

    @Test
    public void laserGunIsVeryDesirableAtMediumDistanceAndLoaded() {
        Bot bot = mockBot();

        WeaponConfig config = new TestConfig().weapons().get(Weapon.WeaponType.HANDGUN);
        LaserGun gun = new LaserGun(bot, 80, config.maxAmmo(), config.fireRate(), null);
        assertThat(gun.desirability(4), is(greaterThan(75.0)));
    }


    @Test
    public void laserGunIsUndesirableWhenCloseAndNearEmpty() {
        Bot bot = mockBot();

        WeaponConfig config = new TestConfig().weapons().get(Weapon.WeaponType.HANDGUN);
        LaserGun gun = new LaserGun(bot, 1, config.maxAmmo(), config.fireRate(), null);
        assertThat(gun.desirability(1), is(lessThan(25.0)));
    }


    @Test
    public void createProjectileAddsNoise() {
        Bot bot = mockBot();

        LaserGun gun = buildLaserGun(bot);
        Projectile projectile = gun.createProjectile();

        assertThat(projectile.getRotation(), is(not(bot.getRotation())));
    }
}
