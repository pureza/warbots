package io.github.pureza.warbots.memory;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class ItemMemoryRecordTest {

    /*
     * void update(long dt, boolean visible, boolean isActive)
     */

    @Test
    public void activeAndVisibleItemThatBecomesInactiveHasBeenCaught() {
        ItemMemoryRecord record = new ItemMemoryRecord(1000, ItemMemoryRecord.State.ACTIVE);
        record.update(10, true, false);

        assertThat(record.getState(), is(ItemMemoryRecord.State.INACTIVE_CERTAIN));
        assertThat(record.getTimeUntilNextActivation(), is(record.getActivationInterval() - 10));
    }


    @Test
    public void activeAndVisibleItemThatStaysActiveHasNotBeenCaught() {
        ItemMemoryRecord record = new ItemMemoryRecord(1000, ItemMemoryRecord.State.ACTIVE);
        record.update(10, true, true);

        assertThat(record.getState(), is(ItemMemoryRecord.State.ACTIVE));
        assertThat(record.getTimeUntilNextActivation(), is(nullValue()));
    }


    @Test(expected=AssertionError.class)
    public void updateFailsIfItemIsInvisibleAndActive() {
        ItemMemoryRecord record = new ItemMemoryRecord(1000, ItemMemoryRecord.State.ACTIVE);
        record.update(10, false, true);
    }


    @Test
    public void activeAndVisibleItemThatBecomesInvisibleGoesToUnknownState() {
        ItemMemoryRecord record = new ItemMemoryRecord(1000, ItemMemoryRecord.State.ACTIVE);
        record.update(10, false, false);

        assertThat(record.getState(), is(ItemMemoryRecord.State.UNKNOWN));
        assertThat(record.getTimeUntilNextActivation(), is(nullValue()));
    }


    @Test
    public void updateDecreasesTimeUntilActivationOfInactiveCertainItem() {
        ItemMemoryRecord record = new ItemMemoryRecord(1000, ItemMemoryRecord.State.ACTIVE);

        {
            record.update(10, true, false);
            assertThat(record.getState(), is(ItemMemoryRecord.State.INACTIVE_CERTAIN));
            assertThat(record.getTimeUntilNextActivation(), is(record.getActivationInterval() - 10));
        }

        {
            record.update(20, false, false);
            assertThat(record.getState(), is(ItemMemoryRecord.State.INACTIVE_CERTAIN));
            assertThat(record.getTimeUntilNextActivation(), is(record.getActivationInterval() - 10 - 20));
        }
    }


    @Test
    public void inactiveCertainItemActivesAndGoesToActiveStateIfVisible() {
        ItemMemoryRecord record = new ItemMemoryRecord(1000, ItemMemoryRecord.State.ACTIVE);

        {
            record.update(10, true, false);
            assertThat(record.getState(), is(ItemMemoryRecord.State.INACTIVE_CERTAIN));
            assertThat(record.getTimeUntilNextActivation(), is(record.getActivationInterval() - 10));
        }

        {
            record.update(record.getActivationInterval(), true, true);
            assertThat(record.getState(), is(ItemMemoryRecord.State.ACTIVE));
            assertThat(record.getTimeUntilNextActivation(), is(nullValue()));
        }
    }


    @Test
    public void inactiveCertainItemActivesAndGoesToUnknownStateIfInvisible() {
        ItemMemoryRecord record = new ItemMemoryRecord(1000, ItemMemoryRecord.State.ACTIVE);

        {
            record.update(10, true, false);
            assertThat(record.getState(), is(ItemMemoryRecord.State.INACTIVE_CERTAIN));
            assertThat(record.getTimeUntilNextActivation(), is(record.getActivationInterval() - 10));
        }

        {
            record.update(record.getActivationInterval(), false, false);
            assertThat(record.getState(), is(ItemMemoryRecord.State.UNKNOWN));
            assertThat(record.getTimeUntilNextActivation(), is(nullValue()));
        }
    }


    @Test
    public void inactiveUncertainItemGoesToUnknownStateIfInvisible() {
        ItemMemoryRecord record = new ItemMemoryRecord(1000, ItemMemoryRecord.State.INACTIVE_UNCERTAIN);

        record.update(record.getActivationInterval(), false, false);
        assertThat(record.getState(), is(ItemMemoryRecord.State.UNKNOWN));
        assertThat(record.getTimeUntilNextActivation(), is(nullValue()));
    }


    @Test
    public void updateIncreasesTimeHasBeenVisibleOfInactiveUncertainItem() {
        ItemMemoryRecord record = new ItemMemoryRecord(1000, ItemMemoryRecord.State.UNKNOWN);

        {
            record.update(20, true, false);
            assertThat(record.getState(), is(ItemMemoryRecord.State.INACTIVE_UNCERTAIN));
            assertThat(record.getTimeHasBeenVisible(), is(20L));
        }

        {
            record.update(20, true, false);
            assertThat(record.getState(), is(ItemMemoryRecord.State.INACTIVE_UNCERTAIN));
            assertThat(record.getTimeHasBeenVisible(), is(40L));
        }
    }


    @Test
    public void inactiveUncertainItemGoesToActiveStateIfBecomesActiveAndIsVisible() {
        ItemMemoryRecord record = new ItemMemoryRecord(1000, ItemMemoryRecord.State.INACTIVE_UNCERTAIN);

        record.update(20, true, true);
        assertThat(record.getState(), is(ItemMemoryRecord.State.ACTIVE));
        assertThat(record.getTimeHasBeenVisible(), is(nullValue()));
        assertThat(record.getTimeUntilNextActivation(), is(nullValue()));
    }


    @Test
    public void unknownItemGoesToInactiveUncertainStateWhenVisible() {
        ItemMemoryRecord record = new ItemMemoryRecord(1000, ItemMemoryRecord.State.UNKNOWN);

        record.update(10, true, false);

        assertThat(record.getState(), is(ItemMemoryRecord.State.INACTIVE_UNCERTAIN));
        assertThat(record.getTimeHasBeenVisible(), is(10L));
    }


    @Test
    public void unknownItemGoesToActiveStateWhenVisibleAndActive() {
        ItemMemoryRecord record = new ItemMemoryRecord(1000, ItemMemoryRecord.State.UNKNOWN);

        record.update(10, true, true);

        assertThat(record.getState(), is(ItemMemoryRecord.State.ACTIVE));
        assertThat(record.getTimeHasBeenVisible(), is(nullValue()));
    }


    /*
     * void caught()
     */

    @Test
    public void caughtRemembersItemBeingCaught() {
        ItemMemoryRecord record = new ItemMemoryRecord(1000, ItemMemoryRecord.State.ACTIVE);
        record.caught();

        assertThat(record.getState(), is(ItemMemoryRecord.State.INACTIVE_CERTAIN));
        assertThat(record.getTimeUntilNextActivation(), is(record.getActivationInterval()));
    }
}
