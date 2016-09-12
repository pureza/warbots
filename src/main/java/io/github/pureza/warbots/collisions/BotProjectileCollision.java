package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.weaponry.Projectile;

import java.util.Objects;

/**
 * Represents a collision between a bot and a projectile
 */
public class BotProjectileCollision extends Collision {

    public BotProjectileCollision(Bot bot, Projectile projectile) {
        super(bot, projectile);
    }


    @Override
    public void handle() {
        Bot bot = (Bot) first;
        Projectile projectile = (Projectile) second;

        // Two projectiles could hit the bot at the same time and kill it twice!
        if (!bot.isDead()) {
            projectile.hitBot(bot);
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
