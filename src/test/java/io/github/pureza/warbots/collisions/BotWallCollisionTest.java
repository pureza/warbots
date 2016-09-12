package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.game.Game;
import org.junit.Before;
import org.junit.Test;
import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.config.BotConfig;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.Wall;
import io.github.pureza.warbots.geometry.Size;
import io.github.pureza.warbots.navigation.Map;

import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.StrictMath.sqrt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static io.github.pureza.warbots.Matchers.closeTo;
import static io.github.pureza.warbots.Tests.buildBot;
import static io.github.pureza.warbots.Tests.mockTeam;
import static io.github.pureza.warbots.geometry.Point.pt;

public class BotWallCollisionTest {

    private Game game;

    private Wall wall1;

    private Wall wall2;


    @Before
    public void setUp() {
        Map map = new Map(10, 10);
        this.wall1 = new Wall(pt(5, 5), new Size(1, 1));
        map.addEntity(wall1);

        this.wall2 = new Wall(pt(6, 5), new Size(1, 1));
        map.addEntity(wall2);

        this.game = new Game(new TestConfig(), map, null, null);
    }


    /*
     * void handle()
     */

    @Test
    public void handleBacksUpBotUntilItNoLongerPenetratesTheWall() {
        // Bot wasn't penetrating the first wall before
        Bot bot = buildBot(game, pt(5.5, 4.5), mockTeam());

        // Bot is penetrating the first wall
        bot.setLocation(pt(5.5, 4.9));

        BotWallCollision collision = new BotWallCollision(bot, wall1, bot.getLocation(), wall1.getLocation());
        collision.handle();

        assertThat(bot.getLocation(), is(closeTo(pt(5.5, 4.7))));
    }


    @Test
    public void handleBacksUpPreviousLocation() {
        // Bot wasn't penetrating the first wall before
        Bot bot = buildBot(game, pt(5.5, 4.5), mockTeam());

        // Bot is penetrating the first wall
        bot.setLocation(pt(5.5, 4.9));

        BotWallCollision collision = new BotWallCollision(bot, wall1, bot.getLocation(), wall1.getLocation());
        collision.handle();

        assertThat(bot.getPreviousLocation(), is(closeTo(pt(5.5, 4.5))));
    }


    @Test
    public void handleBacksUpBotTwiceUntilItNoLongerPenetratesAnyWall() {
        BotConfig botConfig = new TestConfig().botConfig();

        // Bot wasn't penetrating any wall before
        // This bot is a bit larger than usual, for testing
        Bot bot = new Bot(game, pt(5.0, 4.0), mockTeam(), botConfig.maxSpeed(), botConfig.maxTurnRate(), sqrt(2)/2,
                0.1, botConfig.aimNoise());

        // Bot is now penetrating both walls
        bot.setLocation(pt(6.0, 5.0));

        BotWallCollision collision1 = new BotWallCollision(bot, wall1, bot.getLocation(), wall1.getLocation());
        BotWallCollision collision2 = new BotWallCollision(bot, wall2, bot.getLocation(), wall2.getLocation());

        // The handler for collision2 backs up the bot until it no longer
        // penetrates wall2, but it will still penetrate wall1
        collision2.handle();
        assertThat(bot.getLocation(), is(closeTo(pt(5.5, 4.5))));

        // The handler for collision1 backs up the bot even more until it no
        // longer penetrates any wall
        collision1.handle();
        assertThat(bot.getLocation(), is(closeTo(pt(6.0 - sqrt(2)/2, 5.0 - sqrt(2)/2))));
    }


    @Test
    public void handleIgnoresCollisionsThatHaveBeenHandledAlready() {
        BotConfig botConfig = new TestConfig().botConfig();

        // Bot wasn't penetrating any wall before
        // This bot is a bit larger than usual, for testing
        Bot bot = new Bot(game, pt(5.0, 4.0), mockTeam(), botConfig.maxSpeed(), botConfig.maxTurnRate(), sqrt(2)/2,
                0.1, botConfig.aimNoise());

        // Bot is now penetrating both walls
        bot.setLocation(pt(6.0, 5.0));

        BotWallCollision collision1 = new BotWallCollision(bot, wall1, bot.getLocation(), wall1.getLocation());
        BotWallCollision collision2 = new BotWallCollision(bot, wall2, bot.getLocation(), wall2.getLocation());

        // The handler for collision1 backs up the bot until it no longer
        // penetrates any wall
        collision1.handle();
        assertThat(bot.getLocation(), is(closeTo(pt(6.0 - sqrt(2)/2, 5.0 - sqrt(2)/2))));

        // The handler for collision2 doesn't do anything because the bot is no
        // longer penetrating wall2
        collision2.handle();
        assertThat(bot.getLocation(), is(closeTo(pt(6.0 - sqrt(2)/2, 5.0 - sqrt(2)/2))));
    }


    @Test
    public void handlerFiresOnCollisionWithStaticEntityEvent() {
        // Bot wasn't penetrating the first wall before
        Bot bot = buildBot(game, pt(5.5, 4.5), mockTeam());

        // Bot is penetrating the first wall
        bot.setLocation(pt(5.5, 4.9));

        final AtomicBoolean eventFired = new AtomicBoolean(false);
        bot.onCollisionWithStaticEntity().subscribe(ignore -> eventFired.set(true));

        BotWallCollision collision = new BotWallCollision(bot, wall1, bot.getLocation(), wall1.getLocation());
        collision.handle();

        assertThat(eventFired.get(), is(true));
    }
}
