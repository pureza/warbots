package io.github.pureza.warbots.memory;

import io.github.pureza.warbots.geometry.Point;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class BotMemoryRecordTest {

    /*
     * void store(Point location)
     */

    @Test
    public void storeSavesBotLocation() {
        BotMemoryRecord record = new BotMemoryRecord();
        record.store(Point.pt(5, 5));

        assertThat(record.getLastKnownLocation(), Matchers.is(Point.pt(5, 5)));
    }


    @Test
    public void storeResetsTimer() {
        BotMemoryRecord record = new BotMemoryRecord();
        record.store(Point.pt(5, 5));

        assertThat(record.getTimeSinceLastSeen(), is(0L));
    }


    /*
     * void update(long dt)
     */

    @Test
    public void updateIncreasesTimer() {
        BotMemoryRecord record = new BotMemoryRecord();
        record.store(Point.pt(5, 5));

        record.update(100);

        assertThat(record.getTimeSinceLastSeen(), is(100L));
    }


    /*
     * Point getLastKnownLocation()
     */

    @Test
    public void getLastKnownLocationReturnsLastKnownLocation() {
        BotMemoryRecord record = new BotMemoryRecord();
        record.store(Point.pt(5, 5));

        assertThat(record.getLastKnownLocation(), Matchers.is(Point.pt(5, 5)));
    }


    @Test(expected=AssertionError.class)
    public void getLastKnownLocationFailsIfBotHasntBeenSeenRecently() {
        BotMemoryRecord record = new BotMemoryRecord();
        record.getLastKnownLocation();
    }


    /*
     * long getTimeSinceLastSeen()
     */

    @Test
    public void getTimeSinceLastSeenReturnsTimeSinceLastSeen() {
        BotMemoryRecord record = new BotMemoryRecord();
        record.store(Point.pt(5, 5));

        record.update(200);

        assertThat(record.getTimeSinceLastSeen(), is(200L));
    }


    @Test(expected=AssertionError.class)
    public void getTimeSinceLastSeenFailsIfBotHasntBeenSeenRecently() {
        BotMemoryRecord record = new BotMemoryRecord();
        record.getTimeSinceLastSeen();
    }
}
