package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.config.WeaponConfig;
import org.junit.Test;
import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.entities.Bot;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.core.Is.is;
import static io.github.pureza.warbots.Tests.buildHandGun;
import static io.github.pureza.warbots.Tests.mockBot;

public class HandGunTest {

    @Test
    public void handgunIsVeryDesirableWhenFarAndLoaded() {
        Bot bot = mockBot();

        WeaponConfig config = new TestConfig().weapons().get(Weapon.WeaponType.HANDGUN);
        HandGun handGun = new HandGun(bot, 100, config.maxAmmo(), config.fireRate(), null);
        assertThat(handGun.desirability(49), is(greaterThan(75.0)));
    }


    @Test
    public void handgunIsUndesirableWhenCloseAndNearEmpty() {
        Bot bot = mockBot();

        WeaponConfig config = new TestConfig().weapons().get(Weapon.WeaponType.HANDGUN);
        HandGun handGun = new HandGun(bot, 1, config.maxAmmo(), config.fireRate(), null);
        assertThat(handGun.desirability(1), is(lessThan(25.0)));
    }


    @Test
    public void createProjectileAddsNoise() {
        Bot bot = mockBot();

        HandGun handGun = buildHandGun(bot);
        Projectile projectile = handGun.createProjectile();

        assertThat(projectile.getRotation(), is(not(bot.getRotation())));
    }
}
