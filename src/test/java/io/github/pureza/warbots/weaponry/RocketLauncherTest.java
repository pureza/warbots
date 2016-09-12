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
import static io.github.pureza.warbots.Tests.buildRocketLauncher;
import static io.github.pureza.warbots.Tests.mockBot;

public class RocketLauncherTest {

    @Test
    public void rocketLauncherIsVeryDesirableAtCloseDistanceAndLoaded() {
        Bot bot = mockBot();

        WeaponConfig config = new TestConfig().weapons().get(Weapon.WeaponType.ROCKET_LAUNCHER);
        RocketLauncher gun = new RocketLauncher(bot, 40, config.maxAmmo(), config.fireRate(), null);
        assertThat(gun.desirability(1), is(greaterThan(75.0)));
    }


    @Test
    public void rocketLauncherIsUndesirableWhenFarAndNearEmpty() {
        Bot bot = mockBot();

        WeaponConfig config = new TestConfig().weapons().get(Weapon.WeaponType.ROCKET_LAUNCHER);
        RocketLauncher gun = new RocketLauncher(bot, 1, config.maxAmmo(), config.fireRate(), null);
        assertThat(gun.desirability(30), is(lessThan(25.0)));
    }


    @Test
    public void rocketLauncherIsVeryUndesirableWhenEmpty() {
        Bot bot = mockBot();

        WeaponConfig config = new TestConfig().weapons().get(Weapon.WeaponType.ROCKET_LAUNCHER);
        RocketLauncher gun = new RocketLauncher(bot, 0, config.maxAmmo(), config.fireRate(), null);
        assertThat(gun.desirability(1), is(lessThan(25.0)));
    }


    @Test
    public void createProjectileAddsNoise() {
        Bot bot = mockBot();

        RocketLauncher gun = buildRocketLauncher(bot);
        Projectile projectile = gun.createProjectile();

        assertThat(projectile.getRotation(), is(not(bot.getRotation())));
    }
}
