package io.github.pureza.warbots.memory;


import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.InventoryItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The part of the bot's memory that deals with items
 */
public class ItemMemory {

    /** The bot the memory belongs to */
    private final Bot bot;

    /** Memory records for the items */
    private final Map<InventoryItem, ItemMemoryRecord> records;


    public ItemMemory(Bot bot) {
        this(bot, bot.getGame().getMap().getItems());
    }


    /**
     * Used for testing
     */
    ItemMemory(Bot bot, List<InventoryItem> items) {
        this.bot = bot;
        this.records = new HashMap<>();

        // Initially, all items are in the unknown state
        items.forEach(item ->
                records.put(item, new ItemMemoryRecord(item.getActivationInterval(), ItemMemoryRecord.State.UNKNOWN)));
    }


    /**
     * Updates the memory
     */
    public void update(long dt) {
        records.forEach((item, record) -> {
            boolean visible = bot.isInFov(item);
            boolean isActive = visible && item.isActive();
            record.update(dt, visible, isActive);
        });
    }


    public ItemMemoryRecord get(InventoryItem item) {
        return records.get(item);
    }


    public Map<InventoryItem, ItemMemoryRecord> getRecords() {
        return records;
    }
}
