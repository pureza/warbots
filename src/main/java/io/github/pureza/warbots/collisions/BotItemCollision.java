package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.InventoryItem;

import java.util.Objects;


/**
 * A collision between a bot and an inventory item
 */
public class BotItemCollision extends Collision {


    public BotItemCollision(Bot bot, InventoryItem item) {
        super(bot, item);
    }


    @Override
    public void handle() {
        Bot bot = (Bot) first;
        InventoryItem item = (InventoryItem) second;

        // We have to check if the item is still active because two bots may
        // catch the same item simultaneously
        if (item.isActive()) {
            // This can't happen because we deal with bot/item collisions
            // before handling bot/projectile collisions
            assert (!bot.isDead());

            item.caughtBy(bot);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Collision collision = (Collision) o;
        return Objects.equals(first, collision.first) &&
                Objects.equals(second, collision.second);
    }


    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}
