package io.github.pureza.warbots.game;

import com.golden.gamedev.object.Background;
import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.Tests;
import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.InventoryItem;
import io.github.pureza.warbots.geometry.*;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.navigation.Map;
import io.github.pureza.warbots.weaponry.Projectile;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

public class GameTest {

    private Game game;

    @Before
    public void setUp() {
        Map map = new Map(10, 10);

        Config config = new TestConfig();
        Game realGame = new Game(config, map, Tests.mockTeam(), Tests.mockTeam());
        realGame.background = mock(Background.class);

        game = spy(realGame);

        // Mock Game.getImage() so that tests can run anyway
        doReturn(mock(BufferedImage.class)).when(game).getImage(any());
    }


    /*
     * void render(Graphics2D graphics)
     */

    @Test
    public void renderRendersTheBackground() {
        Graphics2D graphics = mock(Graphics2D.class);
        game.render(graphics);

        verify(game.background).render(graphics);
    }


    @Test
    public void renderRendersTheBots() {
        Bot bot = Tests.mockBot();
        game.addBot(bot);

        Graphics2D graphics = mock(Graphics2D.class);
        game.render(graphics);

        verify(bot).render(graphics);
    }


    @Test
    public void renderRendersTheProjectiles() {
        Projectile projectile = mock(Projectile.class);
        game.addProjectile(projectile);

        Graphics2D graphics = mock(Graphics2D.class);
        game.render(graphics);

        verify(projectile).render(graphics);
    }


    @Test
    public void renderRendersTheItems() {
        InventoryItem item = mock(InventoryItem.class);
        when(item.getLocation()).thenReturn(io.github.pureza.warbots.geometry.Point.pt(5, 5));
        when(item.isActive()).thenReturn(true);
        game.getMap().addItem(item);

        Graphics2D graphics = mock(Graphics2D.class);
        game.render(graphics);

        verify(item).render(graphics);
    }


    /*
     * void update(long dt)
     */

    @Test
    public void updateUpdatesItems() {
        InventoryItem item = mock(InventoryItem.class);
        when(item.getLocation()).thenReturn(Point.pt(5, 5));
        game.getMap().addItem(item);

        game.update(1000);

        verify(item).update(1000);
    }


    @Test
    public void updateUpdatesBots() {
        Bot bot = Tests.mockBot();
        game.addBot(bot);

        game.update(1000);

        verify(bot).update(1000);
    }


    @Test
    public void updateUpdatesProjectiles() {
        Projectile projectile = mock(Projectile.class);
        when(projectile.getLocation()).thenReturn(Point.pt(1, 1));
        when(projectile.getPreviousLocation()).thenReturn(Point.pt(0, 0));
        when(projectile.getHeadingVector()).thenReturn(Vector.vec(1, 0));
        game.addProjectile(projectile);

        game.update(1000);

        verify(projectile).update(1000);
    }


    @Test
    public void updateRemovesDeadBotsAndProjectilesThatHitThem() {
        // Create a bot and a projectile that will hit it
        Bot bot = Tests.buildBot(game, Point.pt(5.0, 5.0), Tests.mockTeam());

        Projectile firstProjectile = mock(Projectile.class);
        when(firstProjectile.getHeadingVector()).thenReturn(Vector.vec(1, 0));

        // The bot is almost dying
        bot.inflictDamage(firstProjectile, bot.getHealth() - 1);
        game.addBot(bot);

        // The projectile is heading towards the bot
        Projectile secondProjectile = Tests.buildBullet(game, Point.pt(4.0, 5.0));
        game.addProjectile(secondProjectile);

        game.update(1000);

        MatcherAssert.assertThat(game.getBots(), is(empty()));
        MatcherAssert.assertThat(game.getProjectiles(), is(empty()));
    }


    @Test
    public void updateRemovesProjectilesThatHitWalls() {
        // The projectile is heading towards a wall
        Projectile projectile = Tests.buildBullet(game, Point.pt(0.1, 0.1), Math.PI);
        game.addProjectile(projectile);

        game.update(1000);

        MatcherAssert.assertThat(game.getProjectiles(), is(empty()));
    }


    /*
     * List<Bot> getBots()
     */

    @Test
    public void getBotsReturnsEmptyWhenThereAreNoBots() {
        MatcherAssert.assertThat(game.getBots(), is(empty()));
    }


    @Test
    public void getBotsReturnsBotsInTheGame() {
        Bot bot = Tests.mockBot();
        game.addBot(bot);

        MatcherAssert.assertThat(game.getBots(), containsInAnyOrder(bot));
    }


    @Test(expected=UnsupportedOperationException.class)
    public void getBotsReturnsAnUnmodifiableList() {
        Bot bot = Tests.mockBot();
        game.addBot(bot);

        List<Bot> bots = game.getBots();
        bots.remove(bot);
    }


    /*
     * void addBot(Bot bot)
     */

    @Test
    public void addBotAddsBot() {
        Bot bot = Tests.mockBot();
        game.addBot(bot);
        MatcherAssert.assertThat(game.getBots(), contains(bot));
    }


    /*
     * void removeBot(Bot bot)
     */

    @Test
    public void removeBotMarksBotForDeletion() {
        Bot bot = Tests.mockBot();
        game.addBot(bot);
        game.removeBot(bot);

        // Bot has not been deleted yet...
        MatcherAssert.assertThat(game.getBots(), contains(bot));

        // ... but it has been marked for deletion
        MatcherAssert.assertThat(game.zombies, contains(bot));
    }


    /*
     * List<Bot> getBotsInRange(Point center, double radius)
     */

    @Test
    public void getBotsInRangeReturnsBotAtTheCenter() {
        Bot bot = Tests.mockBot(Point.pt(5, 5));
        game.addBot(bot);

        MatcherAssert.assertThat(game.getBotsInRange(Point.pt(5, 5), 1), contains(bot));
    }


    @Test
    public void getBotsInRangeDoesntReturnAnyBotWhenThereIsNoneInRange() {
        Bot bot = Tests.mockBot(Point.pt(5, 5));
        game.addBot(bot);

        MatcherAssert.assertThat(game.getBotsInRange(Point.pt(7, 5), 1), is(empty()));
    }


    @Test
    public void getBotsInRangeReturnsBotAlmostOutsideOfRange() {
        Bot bot = Tests.mockBot(Point.pt(5.999, 5));
        game.addBot(bot);

        MatcherAssert.assertThat(game.getBotsInRange(Point.pt(5, 5), 1), contains(bot));
    }


    @Test
    public void getBotsInRangeExcludesBotJustOutsideOfRange() {
        Bot bot = Tests.mockBot(Point.pt(6.001, 5));
        game.addBot(bot);

        MatcherAssert.assertThat(game.getBotsInRange(Point.pt(5, 5), 1), is(empty()));
    }


    @Test
    public void getBotsInRangeReturnsMultipleBotsInRangeAndExcludesBotsOutsideRange() {
        Bot a = Tests.mockBot(Point.pt(5.5, 5.5));
        game.addBot(a);

        Bot b = Tests.mockBot(Point.pt(5.3, 4.8));
        game.addBot(b);

        Bot c = Tests.mockBot(Point.pt(4.0, 5.1));
        game.addBot(c);

        Bot d = Tests.mockBot(Point.pt(2.7, 7.1));
        game.addBot(d);

        Bot e = Tests.mockBot(Point.pt(6.7, 1.0));
        game.addBot(e);

        MatcherAssert.assertThat(game.getBotsInRange(Point.pt(5, 5), 2), contains(a, b, c));
    }


    /*
     * void addProjectile(Projectile projectile)
     */

    @Test
    public void addProjectileAddsProjectile() {
        Projectile bullet = Tests.buildBullet(game, Point.pt(0.1, 0.1), Math.PI);
        game.addProjectile(bullet);
        MatcherAssert.assertThat(game.getProjectiles(), contains(bullet));
    }


    /*
     * void removeProjectile(Projectile projectile)
     */

    @Test
    public void removeProjectileMarksProjectileForDeletion() {
        Projectile bullet = Tests.buildBullet(game, Point.pt(0.1, 0.1), Math.PI);
        game.addProjectile(bullet);
        game.removeProjectile(bullet);

        // Bullet has not been deleted yet...
        MatcherAssert.assertThat(game.getProjectiles(), contains(bullet));

        // ... but it has been marked for deletion
        MatcherAssert.assertThat(game.getLostProjectiles(), contains(bullet));
    }

    /*
     * void removeDeadBots()
     */

    @Test
    public void removeDeadBotsRemovesDeadBots() {
        Bot a = Tests.mockBot();
        Bot b = Tests.mockBot();
        game.addBot(a);
        game.addBot(b);

        game.removeBot(a);
        game.removeDeadBots();

        MatcherAssert.assertThat(game.getBots(), contains(b));
        MatcherAssert.assertThat(game.zombies, is(empty()));
    }

    /*
     * void removeLostProjectiles()
     */

    @Test
    public void removeOldProjectilesRemovesOldProjectiles() {
        Projectile bullet = Tests.buildBullet(game, Point.pt(0.1, 0.1), Math.PI);
        Projectile rocket = Tests.buildRocket(game, Point.pt(0.2, 0.2), Math.PI);
        game.addProjectile(bullet);
        game.addProjectile(rocket);

        game.removeProjectile(bullet);
        game.removeLostProjectiles();

        MatcherAssert.assertThat(game.getProjectiles(), contains(rocket));
        MatcherAssert.assertThat(game.getLostProjectiles(), is(empty()));
    }
}
