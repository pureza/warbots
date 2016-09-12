package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.Tests;
import io.github.pureza.warbots.entities.Bot;
import org.junit.Test;
import io.github.pureza.warbots.entities.InventoryItem;

import static org.mockito.Mockito.*;
import static io.github.pureza.warbots.Tests.mockBot;

public class BotItemCollisionTest {

    @Test
    public void handlerAppliesItemToBot() {
        Bot bot = Tests.mockBot();
        InventoryItem item = mock(InventoryItem.class);
        when(item.isActive()).thenReturn(true);

        BotItemCollision collision = new BotItemCollision(bot, item);
        collision.handle();

        verify(item).caughtBy(bot);
    }


    @Test
    public void handlerIgnoresInactiveItems() {
        Bot bot = Tests.mockBot();
        InventoryItem item = mock(InventoryItem.class);
        when(item.isActive()).thenReturn(false);

        BotItemCollision collision = new BotItemCollision(bot, item);
        collision.handle();

        verify(item, never()).caughtBy(bot);
    }


    @Test(expected=AssertionError.class)
    public void handlerFailsOnDeadBots() {
        Bot bot = Tests.mockBot();
        when(bot.isDead()).thenReturn(true);

        InventoryItem item = mock(InventoryItem.class);
        when(item.isActive()).thenReturn(true);

        BotItemCollision collision = new BotItemCollision(bot, item);
        collision.handle();
    }
}
