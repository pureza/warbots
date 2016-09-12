package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.Tests;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.memory.ItemMemoryRecord;
import org.junit.Test;
import io.github.pureza.warbots.config.Config;

import static org.mockito.Mockito.verify;
import static io.github.pureza.warbots.Tests.mockBot;

public class FirstAidItemTest {

    private Config config = new TestConfig();


    /*
     * void caughtBy(Bot bot)
     */

    @Test
    public void firstAidItemIncreasesBotHealthWhenCaught() {
        FirstAidItem item = Tests.buildFirstAidItem(Point.pt(5.5, 5.5));

        Bot bot = Tests.mockBot();
        bot.getMemory().getItemMemory().getRecords().put(item, new ItemMemoryRecord(item.getActivationInterval(), ItemMemoryRecord.State.ACTIVE));

        item.caughtBy(bot);
        verify(bot).acquireHealth(item.getHealthAmount());
    }
}
