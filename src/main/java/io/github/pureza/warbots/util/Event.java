package io.github.pureza.warbots.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * An event type
 *
 * An event contains a list of consumers attached that are executed when the
 * event is fired.
 */
public class Event<T> {

    /** Consumers attached to this event */
    private List<Consumer<T>> consumers = new ArrayList<>();


    /**
     * Attaches a new consumer to this event
     */
    public void subscribe(Consumer<T> consumer) {
        this.consumers.add(consumer);
    }


    /**
     * Detaches a consumer from this event
     */
    public void unsubscribe(Consumer<T> consumer) {
        boolean removed = this.consumers.remove(consumer);
        assert removed;
    }


    /**
     * Fires the event with the given argument, by passing it on to all
     * subscribers
     */
    public void fire(T arg) {
        consumers.forEach(consumer -> consumer.accept(arg));
    }
}
