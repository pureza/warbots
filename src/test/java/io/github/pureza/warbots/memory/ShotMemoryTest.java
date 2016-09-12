package io.github.pureza.warbots.memory;

import io.github.pureza.warbots.Tests;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Vector;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static io.github.pureza.warbots.Tests.mockBot;

public class ShotMemoryTest {

    /*
     * void store(Vector shotDirection)
     */

    @Test
    public void storePersistsShotDirection() {
        Bot bot = Tests.mockBot();
        ShotMemory shotMemory = bot.getMemory().getShotMemory();
        shotMemory.store(Vector.vec(-1, 0));

        assertThat(shotMemory.hasBeenShot(), is(true));
        assertThat(shotMemory.getTimeSinceLastShot(), is(0L));
        assertThat(shotMemory.getLastShotDirection(), Matchers.is(Vector.vec(-1, 0)));
    }


    @Test
    public void storeResetsTimeSinceLastShot() {
        Bot bot = Tests.mockBot();
        ShotMemory shotMemory = bot.getMemory().getShotMemory();

        {
            shotMemory.store(Vector.vec(-1, 0));
            shotMemory.update(100);
            assertThat(shotMemory.getTimeSinceLastShot(), is(100L));
        }

        {
            shotMemory.store(Vector.vec(0, 1));
            assertThat(shotMemory.getTimeSinceLastShot(), is(0L));
        }
    }


    /*
     * void update(long dt)
     */

    @Test
    public void updateDoesntDoAnythingIfTheBotWasNotShot() {
        Bot bot = Tests.mockBot();
        ShotMemory shotMemory = bot.getMemory().getShotMemory();
        shotMemory.update(100);

        assertThat(shotMemory.hasBeenShot(), is(false));
        assertThat(shotMemory.getTimeSinceLastShot(), is(nullValue()));
        assertThat(shotMemory.getLastShotDirection(), is(nullValue()));
    }


    @Test
    public void updateIncreasesTheTimePassedSinceTheLastShot() {
        Bot bot = Tests.mockBot();
        ShotMemory shotMemory = bot.getMemory().getShotMemory();

        shotMemory.store(Vector.vec(0, 1));
        shotMemory.update(100);

        assertThat(shotMemory.getTimeSinceLastShot(), is(100L));
    }


    @Test
    public void updateRemembersTheShotForSomeTime() {
        Bot bot = Tests.mockBot();
        ShotMemory shotMemory = bot.getMemory().getShotMemory();

        shotMemory.store(Vector.vec(0, 1));
        shotMemory.update(ShotMemory.DURATION);

        assertThat(shotMemory.getLastShotDirection(), Matchers.is(Vector.vec(0, 1)));
        assertThat(shotMemory.getTimeSinceLastShot(), Matchers.is(ShotMemory.DURATION));
    }


    @Test
    public void updateForgetsAboutOldShots() {
        Bot bot = Tests.mockBot();
        ShotMemory shotMemory = bot.getMemory().getShotMemory();

        shotMemory.store(Vector.vec(0, 1));
        shotMemory.update(ShotMemory.DURATION + 1);

        assertThat(shotMemory.hasBeenShot(), is(false));
        assertThat(shotMemory.getLastShotDirection(), is(nullValue()));
        assertThat(shotMemory.getTimeSinceLastShot(), is(nullValue()));
    }


    @Test
    public void updateForgetsWhenItIsLookingTowardsTheShotDirection() {
        Bot bot = Tests.mockBot();
        ShotMemory shotMemory = bot.getMemory().getShotMemory();

        // This is the direction the bot is looking at
        shotMemory.store(Vector.vec(1, 0));

        assertThat(shotMemory.hasBeenShot(), is(true));
        assertThat(shotMemory.getLastShotDirection(), Matchers.is(Vector.vec(1, 0)));

        shotMemory.update(1);

        assertThat(shotMemory.hasBeenShot(), is(false));
        assertThat(shotMemory.getLastShotDirection(), is(nullValue()));
        assertThat(shotMemory.getTimeSinceLastShot(), is(nullValue()));
    }


    /*
     * Long getTimeSinceLastShot()
     */

    @Test
    public void getTimeSinceLastShotReturnsNullIfTheBotDoesntRememberBeingShot() {
        Bot bot = Tests.mockBot();
        ShotMemory shotMemory = bot.getMemory().getShotMemory();

        assertThat(shotMemory.getTimeSinceLastShot(), is(nullValue()));
    }


    @Test
    public void getTimeSinceLastShotReturnsTheTimeSinceTheLastShot() {
        Bot bot = Tests.mockBot();
        ShotMemory shotMemory = bot.getMemory().getShotMemory();

        shotMemory.store(Vector.vec(0, 1));
        shotMemory.update(10);

        assertThat(shotMemory.getTimeSinceLastShot(), is(10L));
    }
}
