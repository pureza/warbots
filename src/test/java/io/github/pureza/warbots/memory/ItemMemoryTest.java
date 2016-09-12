package io.github.pureza.warbots.memory;

import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.Tests;
import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.FirstAidItem;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.weaponry.Weapon;
import org.junit.Test;
import io.github.pureza.warbots.entities.WeaponItem;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static io.github.pureza.warbots.Tests.mockBot;

public class ItemMemoryTest {

    private Config config = new TestConfig();

    /*
     * ItemMemory(Bot bot, List<InventoryItem> items)
     */

    @Test
    public void constructorInitializesAllItemsInUnknownState() {
        FirstAidItem firstAidItem = Tests.buildFirstAidItem(Point.pt(2.5, 3.5));
        WeaponItem weaponItem = Tests.buildWeaponItem(Point.pt(1.5, 0.5), Weapon.WeaponType.HANDGUN);

        Bot bot = Tests.mockBot();
        ItemMemory itemMemory = new ItemMemory(bot, asList(firstAidItem, weaponItem));

        assertThat(itemMemory.get(firstAidItem).getState(), is(ItemMemoryRecord.State.UNKNOWN));
        assertThat(itemMemory.get(weaponItem).getState(), is(ItemMemoryRecord.State.UNKNOWN));
    }


    /*
     * void update(long dt)
     */

    @Test
    public void updateUpdatesTheMemoryRecordsForAllItems() {
        FirstAidItem firstAidItem = Tests.buildFirstAidItem(Point.pt(2.5, 3.5));
        WeaponItem weaponItem = Tests.buildWeaponItem(Point.pt(1.5, 0.5), Weapon.WeaponType.HANDGUN);

        Bot bot = Tests.mockBot();
        ItemMemory itemMemory = new ItemMemory(bot, asList(firstAidItem, weaponItem));

        firstAidItem.deactivate();

        when(bot.isInFov(firstAidItem)).thenReturn(true);
        when(bot.isInFov(weaponItem)).thenReturn(false);

        itemMemory.update(100);

        assertThat(itemMemory.get(firstAidItem).getState(), is(ItemMemoryRecord.State.INACTIVE_UNCERTAIN));
        assertThat(itemMemory.get(weaponItem).getState(), is(ItemMemoryRecord.State.UNKNOWN));
    }
}
