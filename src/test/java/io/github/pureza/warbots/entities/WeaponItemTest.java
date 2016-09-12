package io.github.pureza.warbots.entities;

import org.junit.Test;
import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.memory.ItemMemoryRecord;
import io.github.pureza.warbots.weaponry.HandGun;
import io.github.pureza.warbots.weaponry.LaserGun;
import io.github.pureza.warbots.weaponry.RocketLauncher;
import io.github.pureza.warbots.weaponry.Weapon;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static io.github.pureza.warbots.Tests.buildWeaponItem;
import static io.github.pureza.warbots.Tests.mockBot;
import static io.github.pureza.warbots.geometry.Point.pt;

public class WeaponItemTest {

    private Config config = new TestConfig();


    /*
     * void caughtBy(Bot bot)
     */

    @Test
    public void weaponItemOffersHandgunOnCaught() {
        WeaponItem item = buildWeaponItem(pt(5.5, 5.5), Weapon.WeaponType.HANDGUN);

        Bot bot = mockBot();
        bot.getMemory().getItemMemory().getRecords().put(item, new ItemMemoryRecord(item.getActivationInterval(), ItemMemoryRecord.State.ACTIVE));


        item.caughtBy(bot);
        verify(bot).acquireWeapon(any(HandGun.class));
    }

    
    @Test
    public void weaponItemOffersLaserGunOnCaught() {
        WeaponItem item = buildWeaponItem(pt(5.5, 5.5), Weapon.WeaponType.LASER_GUN);

        Bot bot = mockBot();
        bot.getMemory().getItemMemory().getRecords().put(item, new ItemMemoryRecord(item.getActivationInterval(), ItemMemoryRecord.State.ACTIVE));

        item.caughtBy(bot);
        verify(bot).acquireWeapon(any(LaserGun.class));
    }


    @Test
    public void weaponItemOffersRocketLauncherOnCaught() {
        WeaponItem item = buildWeaponItem(pt(5.5, 5.5), Weapon.WeaponType.ROCKET_LAUNCHER);

        Bot bot = mockBot();
        bot.getMemory().getItemMemory().getRecords().put(item, new ItemMemoryRecord(item.getActivationInterval(), ItemMemoryRecord.State.ACTIVE));

        item.caughtBy(bot);
        verify(bot).acquireWeapon(any(RocketLauncher.class));
    }
}
