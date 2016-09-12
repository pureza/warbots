package io.github.pureza.warbots.memory;


import io.github.pureza.warbots.entities.Bot;

import java.util.*;

/**
 * The part of the bot's memory that deals with enemy bots
 */
public class BotMemory {

    /** For how long do records for bots currently out of sight stay in memory? */
    public static final long DURATION = 10000;

    /** The bot the memory belongs to */
    private final Bot bot;

    /** Memory records for the enemy bots */
    private final Map<Bot, BotMemoryRecord> records;


    public BotMemory(Bot bot) {
        this.bot = bot;
        this.records = new HashMap<>();
    }


    /**
     * Updates the memory
     */
    public void update(long dt) {
        // First, we update the records for the visible bots
        Set<Bot> visibleBots = new HashSet<>(bot.getBotsInFov());
        for (Bot other : visibleBots) {
            // Don't care about team mates
            if (!bot.isSameTeam(other)) {
                if (!records.containsKey(other)) {
                    records.put(other, new BotMemoryRecord());
                }

                // Mark this bot as visible and records its location
                BotMemoryRecord record = records.get(other);
                record.store(other.getLocation());
            }
        }

        // Now, deal with bots currently out of sight
        Iterator<Map.Entry<Bot, BotMemoryRecord>> it = records.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Bot, BotMemoryRecord> entry = it.next();
            Bot enemy = entry.getKey();
            BotMemoryRecord record = entry.getValue();

            // Ignore visible bots
            if (visibleBots.contains(enemy)) {
                continue;
            }

            // Forget about dead enemies
            if (enemy.isDead()) {
                it.remove();
            } else {
                // Refresh the memory record for this invisible enemy
                record.update(dt);

                // Forget about enemies I haven't seen for too long
                if (record.getTimeSinceLastSeen() > DURATION) {
                    it.remove();
                }

                // If I'm at the exact location where I last saw the bot and I
                // still don't know where he is, forget about him
                if (bot.getLocation().equals(record.getLastKnownLocation())) {
                    it.remove();
                }
            }
        }

        // The memory can only contain records for live enemies
        assert records.keySet().stream().allMatch(enemy -> !bot.isSameTeam(enemy) && !enemy.isDead());
    }


    /**
     * Returns the memory records for bots
     */
    public Map<Bot, BotMemoryRecord> getRecords() {
        return Collections.unmodifiableMap(records);
    }
}
