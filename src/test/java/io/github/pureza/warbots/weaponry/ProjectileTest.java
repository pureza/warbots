package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.game.Game;
import org.junit.Test;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.Wall;
import io.github.pureza.warbots.geometry.Size;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static io.github.pureza.warbots.Matchers.closeTo;
import static io.github.pureza.warbots.Tests.*;
import static io.github.pureza.warbots.geometry.Point.pt;

public class ProjectileTest {

    @Test
    public void projectileIsRemovedFromWorldAfterHittingBot() {
        Bot bot = mockBot();

        Bullet bullet = buildBullet(bot.getGame(), pt(0, 0), Math.PI);

        bullet.hitBot(bot);
        verify(bot.getGame()).removeProjectile(bullet);
    }


    @Test
    public void projectileInflictsDamageOnOpponentBots() {
        Bot bot = mockBot();
        when(bot.isSameTeam(bot)).thenReturn(false);

        Bullet bullet = buildBullet(bot.getGame(), pt(0, 0), Math.PI);
        bullet.hitBot(bot);

        verify(bot).inflictDamage(bullet, bullet.getDamage());
    }


    @Test
    public void projectileDoesNotInflictDamageOnSameTeamBots() {
        Game game = mockGame();

        Bullet bullet = buildBullet(game, pt(0, 0), Math.PI);
        Bot bot = buildBot(game, pt(1, 0), bullet.shooter.getTeam());
        int initialHealth = bot.getHealth();

        bullet.hitBot(bot);

        assertThat(bot.getHealth(), is(initialHealth));
    }


    @Test
    public void projectileIsRemovedFromWorldAfterHittingStaticEntities() {
        Bot bot = mockBot();

        Bullet bullet = buildBullet(bot.getGame(), pt(0, 0), Math.PI);
        Wall wall = new Wall(bullet.getLocation(), new Size(1, 1));

        bullet.hitStaticEntity(wall);
        verify(bot.getGame()).removeProjectile(bullet);
    }
}
