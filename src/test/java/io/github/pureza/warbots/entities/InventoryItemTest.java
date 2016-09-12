package io.github.pureza.warbots.entities;

import com.golden.gamedev.object.Sprite;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.memory.ItemMemoryRecord;
import org.junit.Test;
import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.config.Config;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static io.github.pureza.warbots.Tests.buildFirstAidItem;
import static io.github.pureza.warbots.Tests.mockBot;
import static io.github.pureza.warbots.Tests.mockGame;
import static io.github.pureza.warbots.geometry.Point.pt;

public class InventoryItemTest {

    private Config config = new TestConfig();


    /*
     * InventoryItem(Point location, double boundingRadius, long activationInterval)
     */

    @Test
    public void inventoryItemStartsActive() {
        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));
        assertThat(item.isActive(), is(true));
    }


    @Test
    public void constructorSetsBoundingRadius() {
        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));
        assertThat(item.boundingRadius, is(config.firstAidItemConfig().boundingRadius()));
    }


    @Test
    public void constructorSetsActivationInterval() {
        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));
        assertThat(item.activationInterval, is(config.firstAidItemConfig().activationInterval()));
    }


    /*
     * void update(long dt)
     */

    @Test
    public void updateIgnoresActiveItems() {
        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));
        item.update(1000);

        assertThat(item.isActive(), is(true));
    }


    @Test
    public void updateIncreasesTimeSinceDeactivationForInactiveItem() {
        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));
        item.deactivate();
        item.update(1000);
        assertThat(item.timeSinceDeactivation, is(1000L));
    }


    @Test
    public void updateActivatesItemIfEnoughTimeHasPassed() {
        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));
        item.deactivate();
        item.update(100000);
        assertThat(item.isActive(), is(true));
    }


    /*
     * void caughtBy(Bot bot)
     */

    @Test
    public void caughtByDeactivatesItem() {
        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));

        Bot bot = mockBot();
        bot.getMemory().getItemMemory().getRecords().put(item, new ItemMemoryRecord(item.getActivationInterval(), ItemMemoryRecord.State.ACTIVE));

        item.caughtBy(bot);
        assertThat(item.isActive(), is(false));
    }


    @Test
    public void caughtByUpdatesBotMemory() {
        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));

        Bot bot = mockBot();
        bot.getMemory().getItemMemory().getRecords().put(item, new ItemMemoryRecord(item.getActivationInterval(), ItemMemoryRecord.State.ACTIVE));

        item.caughtBy(bot);

        assertThat(bot.getMemory().getItemRecord(item).getState(), is(ItemMemoryRecord.State.INACTIVE_CERTAIN));
    }


    /*
     * void render(Graphics2D graphics)
     */

    @Test
    public void renderRendersActiveItems() {
        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));
        item.sprite = mock(Sprite.class);

        item.render(mock(Graphics2D.class));
        verify(item.sprite).render(any());
    }


    @Test
    public void renderIgnoresInactiveItems() {
        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));
        item.sprite = mock(Sprite.class);

        item.deactivate();
        item.render(mock(Graphics2D.class));
        verify(item.sprite, never()).render(any());
    }


    /*
     * void initResources(Game game)
     */

    @Test
    public void loadSpritePositionsSpriteCorrectly() {
        Game game = mockGame();

        when(game.getMap().width()).thenReturn(10);
        when(game.getMap().height()).thenReturn(10);

        BufferedImage image = mock(BufferedImage.class);
        when(image.getWidth()).thenReturn(16);
        when(image.getHeight()).thenReturn(16);
        when(game.getImage(any())).thenReturn(image);

        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));
        item.loadSprite(game);

        assertThat(item.sprite.getX(), is(5.5 * 32 - 8));
        assertThat(item.sprite.getY(), is((10 - 5.5) * 32 - 8));
    }


    /*
     * void activate()
     */

    @Test(expected=IllegalStateException.class)
    public void activateFailsIfItemIsAlreadyActive() {
        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));
        item.activate();
    }


    @Test
    public void activateActivatesItem() {
        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));
        item.deactivate();
        item.activate();
        assertThat(item.isActive(), is(true));
    }


    /*
     * void deactivate()
     */

    @Test(expected=IllegalStateException.class)
    public void deactivateFailsIfItemIsAlreadyInactive() {
        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));
        item.deactivate();
        item.deactivate();
    }


    @Test
    public void deactivateDeactivatesItem() {
        InventoryItem item = buildFirstAidItem(pt(5.5, 5.5));
        item.deactivate();
        assertThat(item.isActive(), is(false));
    }
}
