package io.github.pureza.warbots.memory;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.InventoryItem;

/**
 * The bot's memory
 */
public class Memory {

    /** Memory related to enemy bots */
    private BotMemory botMemory;

    /** Memory related to items */
    private ItemMemory itemMemory;

    /** Memory related to the last shot to hit the bot */
    private ShotMemory shotMemory;


    public Memory(Bot bot) {
        this.botMemory = new BotMemory(bot);
        this.itemMemory = new ItemMemory(bot);
        this.shotMemory = new ShotMemory(bot);
    }


    /**
     * Updates the bot's memory
     */
    public void update(long dt) {
        this.botMemory.update(dt);
        this.itemMemory.update(dt);
        this.shotMemory.update(dt);
    }


    public ItemMemory getItemMemory() {
        return itemMemory;
    }


    /**
     * Retrieves the memory record for a specific item
     */
    public ItemMemoryRecord getItemRecord(InventoryItem item) {
        return getItemMemory().get(item);
    }


    public BotMemory getBotMemory() {
        return botMemory;
    }


    /**
     * Retrieves the memory record for an enemy bot
     */
    public BotMemoryRecord getBotRecord(Bot bot) {
        return getBotMemory().getRecords().get(bot);
    }


    public ShotMemory getShotMemory() {
        return shotMemory;
    }
}
