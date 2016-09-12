package io.github.pureza.warbots.memory;

import org.hamcrest.Matchers;
import org.junit.Test;
import io.github.pureza.warbots.entities.Bot;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.when;
import static io.github.pureza.warbots.Tests.mockBot;
import static io.github.pureza.warbots.geometry.Point.pt;

public class BotMemoryTest {

    /*
     * void update(long dt)
     */

    @Test
    public void updateStoresLocationOfVisibleBots() {
        Bot bot = mockBot();

        Bot other = mockBot(pt(1, 1));
        when (bot.getBotsInFov()).thenReturn(Collections.singletonList(other));

        bot.getMemory().getBotMemory().update(100);

        assertThat(bot.getMemory().getBotRecord(other).getLastKnownLocation(), is(pt(1, 1)));
    }


    @Test
    public void updateIgnoresTeamMates() {
        Bot bot = mockBot();

        Bot other = mockBot(pt(1, 1));
        when (bot.getBotsInFov()).thenReturn(Collections.singletonList(other));
        when (bot.isSameTeam(other)).thenReturn(true);

        bot.getMemory().getBotMemory().update(100);

        assertThat(bot.getMemory().getBotRecord(other), is(nullValue()));
    }


    @Test
    public void updateRemovesDeadBots() {
        Bot bot = mockBot();

        Bot other = mockBot(pt(1, 1));

        {
            when(bot.getBotsInFov()).thenReturn(Collections.singletonList(other));
            bot.getMemory().getBotMemory().update(100);
            assertThat(bot.getMemory().getBotRecord(other).getLastKnownLocation(), is(pt(1, 1)));
        }

        {
            when(bot.getBotsInFov()).thenReturn(Collections.emptyList());
            when(other.isDead()).thenReturn(true);
            bot.getMemory().getBotMemory().update(100);
            assertThat(bot.getMemory().getBotRecord(other), is(nullValue()));
        }
    }


    @Test
    public void updateUpdatesExistingMemoryRecords() {
        Bot bot = mockBot();

        Bot other = mockBot(pt(1, 1));

        {
            when(bot.getBotsInFov()).thenReturn(Collections.singletonList(other));
            bot.getMemory().getBotMemory().update(100);
            assertThat(bot.getMemory().getBotRecord(other).getLastKnownLocation(), is(pt(1, 1)));
        }

        {
            when(bot.getBotsInFov()).thenReturn(Collections.emptyList());
            bot.getMemory().getBotMemory().update(100);
            assertThat(bot.getMemory().getBotRecord(other).getLastKnownLocation(), is(pt(1, 1)));
            assertThat(bot.getMemory().getBotRecord(other).getTimeSinceLastSeen(), is(100L));
        }
    }


    @Test
    public void updateRemembersRecordsUpToThreshold() {
        Bot bot = mockBot();

        Bot other = mockBot(pt(1, 1));

        {
            when(bot.getBotsInFov()).thenReturn(Collections.singletonList(other));
            bot.getMemory().getBotMemory().update(100);
            assertThat(bot.getMemory().getBotRecord(other).getLastKnownLocation(), is(pt(1, 1)));
        }

        {
            when(bot.getBotsInFov()).thenReturn(Collections.emptyList());
            bot.getMemory().getBotMemory().update(BotMemory.DURATION);
            assertThat(bot.getMemory().getBotRecord(other).getLastKnownLocation(), is(pt(1, 1)));
            assertThat(bot.getMemory().getBotRecord(other).getTimeSinceLastSeen(), Matchers.is(BotMemory.DURATION));
        }
    }


    @Test
    public void updateDeletesOldMemoryRecords() {
        Bot bot = mockBot();

        Bot other = mockBot(pt(1, 1));

        {
            when(bot.getBotsInFov()).thenReturn(Collections.singletonList(other));
            bot.getMemory().getBotMemory().update(100);
            assertThat(bot.getMemory().getBotRecord(other).getLastKnownLocation(), is(pt(1, 1)));
        }

        {
            when(bot.getBotsInFov()).thenReturn(Collections.emptyList());
            bot.getMemory().getBotMemory().update(BotMemory.DURATION + 1);
            assertThat(bot.getMemory().getBotRecord(other), is(nullValue()));
        }
    }


    @Test
    public void updateDeletesRecordsAtCurrentLocation() {
        Bot bot = mockBot();

        Bot other = mockBot(pt(1, 1));

        {
            when(bot.getBotsInFov()).thenReturn(Collections.singletonList(other));
            bot.getMemory().getBotMemory().update(100);
            assertThat(bot.getMemory().getBotRecord(other).getLastKnownLocation(), is(pt(1, 1)));
        }

        {
            when (bot.getLocation()).thenReturn(pt(1, 1));
            when(bot.getBotsInFov()).thenReturn(Collections.emptyList());
            bot.getMemory().getBotMemory().update(10);
            assertThat(bot.getMemory().getBotRecord(other), is(nullValue()));
        }
    }
}
