package io.github.pureza.warbots.weaponry;

import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.config.WeaponConfig;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static io.github.pureza.warbots.Tests.buildHandGun;
import static io.github.pureza.warbots.Tests.mockBot;

public class WeaponTest {

    /*
     * acquireAmmo(int ammoCount)
     */

    @Test
    public void acquireAmmoIncrementsAmmunition() {
        Bot bot = mockBot();

        HandGun gun = buildHandGun(bot);
        int initialAmmo = gun.getRemainingAmmo();
        gun.acquireAmmo(1);

        assertThat(gun.getRemainingAmmo(), is(initialAmmo + 1));
    }


    @Test
    public void acquireAmmoIncrementsOnlyUpToMaxAmmo() {
        Bot bot = mockBot();

        HandGun gun = buildHandGun(bot);
        gun.acquireAmmo(gun.getMaxAmmo());
        assertThat(gun.getRemainingAmmo(), Is.is(gun.getMaxAmmo()));
    }


    /*
     * isReady(long now)
     */


    @Test
    public void isNotReadyIfNotEnoughTimeHasPassed() {
        Bot bot = mockBot();

        HandGun gun = buildHandGun(bot);
        gun.fire();
        long time = gun.getLastShotTime();
        long interval = (int) (1000.0 / gun.getFireRate());
        assertThat(gun.isReady(time + interval - 1), is(false));
    }


    @Test
    public void isReadyIfEnoughTimeHasPassed() {
        Bot bot = mockBot();

        HandGun gun = buildHandGun(bot);
        gun.fire();
        long time = gun.getLastShotTime();
        long interval = (int) (1000.0 / gun.getFireRate());
        assertThat(gun.isReady(time + interval + 1), is(true));
    }


    /*
     * fire()
     */


    @Test
    public void fireFailsIfNotReady() {
        Bot bot = mockBot();

        HandGun gun = buildHandGun(bot);
        assertThat(gun.fire(), is(not(Matchers.nullValue())));
        assertThat(gun.fire(), is(nullValue()));
    }


    @Test
    public void fireFailsIfEmpty() {
        Bot bot = mockBot();

        WeaponConfig config = new TestConfig().weapons().get(Weapon.WeaponType.HANDGUN);
        HandGun gun = new HandGun(bot, 0, config.maxAmmo(), config.fireRate(), null);
        assertThat(gun.fire(), is(nullValue()));
    }


    @Test
    public void fireDecreasesRemainingAmmo() {
        Bot bot = mockBot();

        HandGun gun = buildHandGun(bot);
        int initialAmmo = gun.getRemainingAmmo();
        gun.fire();
        assertThat(gun.getRemainingAmmo(), is(initialAmmo - 1));
    }


    @Test
    public void fireUpdatesLastShotTime() {
        Bot bot = mockBot();

        HandGun gun = buildHandGun(bot);
        long before = System.currentTimeMillis();
        gun.fire();
        long after = System.currentTimeMillis();

        assertThat(gun.getLastShotTime(), is(greaterThanOrEqualTo(before)));
        assertThat(gun.getLastShotTime(), is(lessThanOrEqualTo(after)));
    }


    @Test
    public void fireCreatesProjectileInFrontOfBot() {
        Bot bot = mockBot();

        HandGun gun = buildHandGun(bot);
        Projectile projectile = gun.fire();

        // The bot is at (0, 0) looking at (1, 0), its bounding radius is 0.3
        // and the bullet's bounding radius is 0.1
        assertThat(projectile.getLocation(), is(Point.pt(0.4, 0)));
    }


    /*
     * double individualWeaponStrength()
     */

    @Test
    public void individualWeaponStrengthReturnsThePercentageOfAmmunitionLeft() {
        Bot bot = mockBot();

        WeaponConfig config = new TestConfig().weapons().get(Weapon.WeaponType.HANDGUN);
        HandGun gun = new HandGun(bot, 30, config.maxAmmo(), config.fireRate(), null);
        assertThat(gun.individualWeaponStrength(), is(30.0/120.0));
    }
}
