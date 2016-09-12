package io.github.pureza.warbots.memory;

import io.github.pureza.warbots.entities.FirstAidItem;
import io.github.pureza.warbots.geometry.Vector;
import org.junit.Test;
import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.entities.Bot;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static io.github.pureza.warbots.Tests.buildFirstAidItem;
import static io.github.pureza.warbots.Tests.mockBot;
import static io.github.pureza.warbots.geometry.Point.pt;

public class MemoryTest {

    private Config config = new TestConfig();


    /*
     * void update(long dt)
     */

    @Test
    public void updateUpdatesItemsMemory() {
        FirstAidItem firstAidItem = buildFirstAidItem(pt(2.5, 3.5));

        Bot bot = mockBot();
        when(bot.getGame().getMap().getItems()).thenReturn(Collections.singletonList(firstAidItem));
        when(bot.isInFov(firstAidItem)).thenReturn(true);

        Memory memory = new Memory(bot);
        assertThat(memory.getItemRecord(firstAidItem).getState(), is(ItemMemoryRecord.State.UNKNOWN));

        memory.update(100);

        assertThat(memory.getItemRecord(firstAidItem).getState(), is(ItemMemoryRecord.State.ACTIVE));
    }


    @Test
    public void updateUpdatesBotsMemory() {
        Bot bot = mockBot();

        Bot other = mockBot(pt(1, 1));
        when (bot.getBotsInFov()).thenReturn(Collections.singletonList(other));

        bot.getMemory().update(100);

        assertThat(bot.getMemory().getBotRecord(other).getLastKnownLocation(), is(pt(1, 1)));
    }


    @Test
    public void updateUpdatesShotMemory() {
        Bot bot = mockBot();
        bot.getMemory().getShotMemory().store(Vector.vec(-1, 0));

        bot.getMemory().update(100);

        assertThat(bot.getMemory().getShotMemory().getTimeSinceLastShot(), is(100L));
    }
}
