package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.Tests;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.weaponry.Projectile;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static io.github.pureza.warbots.Tests.mockBot;

public class BotProjectileCollisionTest {

    @Test
    public void handlerHitsBot() {
        Bot bot = Tests.mockBot();
        Projectile projectile = mock(Projectile.class);

        BotProjectileCollision collision = new BotProjectileCollision(bot, projectile);
        collision.handle();

        verify(projectile).hitBot(bot);
    }


    @Test
    public void handlerIgnoresDeadBots() {
        Bot bot = Tests.mockBot();
        when(bot.isDead()).thenReturn(true);

        Projectile projectile = mock(Projectile.class);

        BotProjectileCollision collision = new BotProjectileCollision(bot, projectile);
        collision.handle();

        verify(projectile, never()).hitBot(bot);
    }
}
