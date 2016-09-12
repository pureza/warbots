package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.Tests;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Vector;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;
import static io.github.pureza.warbots.Tests.mockBot;

public class ArtilleryTest {

    private Bot bot;

    private Artillery artillery;


    @Before
    public void setUp() {
        this.bot = Tests.mockBot();
        this.artillery = new Artillery(bot);

        HandGun handGun = Tests.buildHandGun(bot);
        artillery.acquire(handGun);
    }


    /*
     * acquire(Weapon weapon)
     */

    @Test
    public void acquireAcquiresNewWeapon() {
        Bot bot = Tests.mockBot();

        artillery.acquire(Tests.buildLaserGun(bot));
        assertThat(artillery.getWeapons().keySet(), hasItem(Weapon.WeaponType.LASER_GUN));
    }


    @Test
    public void acquireSwitchesToNewWeapon() {
        Bot bot = Tests.mockBot();

        LaserGun laserGun = Tests.buildLaserGun(bot);
        artillery.acquire(laserGun);
        assertThat(artillery.getCurrentWeapon(), is(laserGun));
    }


    @Test
    public void acquireTakesAmmoFromExistingWeapon() {
        Bot bot = Tests.mockBot();

        int initialAmmo = artillery.getCurrentWeapon().getRemainingAmmo();

        HandGun otherGun = Tests.buildHandGun(bot);
        artillery.acquire(otherGun);

        assertThat(artillery.getCurrentWeapon().getRemainingAmmo(), is(greaterThan(initialAmmo)));
    }


    /*
     * totalWeaponStrength()
     */

    @Test
    public void totalWeaponStrengthAveragesIndividualWeaponStrength() {
        Weapon weapon = artillery.getCurrentWeapon();

        assertThat(artillery.totalWeaponStrength(), is(weapon.individualWeaponStrength() / Weapon.WeaponType.values().length));
    }


    /*
     * chooseWeapon(Bot other)
     */

    @Test
    public void chooseWeaponSelectsMostAppropriateWeapon() {
        // The enemy is far away, so the handgun will be preferred
        Bot enemy = Tests.mockBot(bot.getLocation().displace(20, 10), Vector.vec(0, 1), 0.3, 0);

        Weapon handGun = artillery.getCurrentWeapon();
        artillery.acquire(Tests.buildLaserGun(bot));

        assertThat(artillery.chooseWeapon(enemy), is(handGun));
    }


    @Test
    public void chooseWeaponDoesntChoosesUnloadedWeaponWhenPossible() {
        // The hand gun is almost empty
        HandGun handGun = new HandGun(bot, 1, 10, 1, null);

        // The rocket launcher is empty
        RocketLauncher rocketLauncher = new RocketLauncher(bot, 0, 10, 1, null);

        Artillery artillery = new Artillery(bot);
        artillery.acquire(handGun);
        artillery.acquire(rocketLauncher);

        // The enemy is very very close
        Bot enemy = Tests.mockBot(bot.getLocation().displace(0.01, 0.01));

        // However, the artillery prefers the hand gun because the rocket launcher is empty!
        assertThat(artillery.chooseWeapon(enemy), Is.is(handGun));
    }


    /*
     * fireAt(Bot enemy)
     */

    @Test
    public void fireAtIgnoresEnemiesAtMyBack() {
        // I'm at (0, 0) and looking towards (1, 0), so this enemy is outside my field of vision
        Bot enemy = Tests.mockBot(Point.pt(0, 1), Vector.vec(0, 1), 0.3, 0);
        when(bot.rotateFacing(anyObject(), anyLong())).thenReturn(false);

        assertThat(artillery.fireAt(enemy, 1000), is(nullValue()));
    }


    @Test
    public void fireAtSelectsWeaponAndFiresAtEnemiesInFront() {
        // I'm at (0, 0) and looking towards (1, 0), so this enemy is right in front of me
        Bot enemy = Tests.mockBot(Point.pt(2, 0), Vector.vec(0, 1), 0.3, 0);
        when(bot.rotateFacing(anyObject(), anyLong())).thenReturn(true);

        assertThat(artillery.fireAt(enemy, 1000), is(not(nullValue())));
    }
}
