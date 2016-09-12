package io.github.pureza.warbots.util;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class EventTest {

    /*
     * void subscribe(Consumer<T> consumer)
     */

    @Test
    public void subscribeSubscribesNewConsumer() {
        Event<Integer> event = new Event<>();
        AtomicInteger atomicValue = new AtomicInteger(0);

        event.subscribe(atomicValue::set);
        event.fire(123);

        assertThat(atomicValue.get(), is(123));
    }


    @Test
    public void subscribeSubscribesMultipleConsumers() {
        Event<Integer> event = new Event<>();
        AtomicInteger a = new AtomicInteger(0);
        AtomicInteger b = new AtomicInteger(0);

        event.subscribe(a::set);
        event.subscribe(b::set);
        event.fire(123);

        assertThat(a.get(), is(123));
        assertThat(b.get(), is(123));
    }


    /*
     * void unsubscribe(Consumer<T> consumer)
     */

    @Test
    public void unsubscribeUnsubscribesConsumer() {
        Event<Integer> event = new Event<>();
        AtomicInteger a = new AtomicInteger(0);
        AtomicInteger b = new AtomicInteger(0);

        Consumer<Integer> firstConsumer = a::set;
        event.subscribe(firstConsumer);
        event.subscribe(b::set);
        event.fire(123);

        assertThat(a.get(), is(123));
        assertThat(b.get(), is(123));

        event.unsubscribe(firstConsumer);

        event.fire(456);

        assertThat(a.get(), is(123));
        assertThat(b.get(), is(456));
    }
}
