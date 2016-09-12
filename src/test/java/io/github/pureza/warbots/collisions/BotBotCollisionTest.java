package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.Tests;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.navigation.Map;
import org.junit.Before;
import org.junit.Test;
import io.github.pureza.warbots.TestConfig;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static io.github.pureza.warbots.Matchers.closeTo;
import static io.github.pureza.warbots.geometry.Point.pt;

public class BotBotCollisionTest {

    private Game game;


    @Before
    public void setUp() {
        Map map = new Map(10, 10);
        this.game = new Game(new TestConfig(), map, null, null);
    }


    /*
     * void handle()
     */

    @Test
    public void handleIgnoresNonOverlappingBots() {
        Bot a = Tests.buildBot(game, pt(5.5, 4.5), Tests.mockTeam());
        Bot b = Tests.buildBot(game, pt(1.5, 2.5), Tests.mockTeam());

        BotBotCollision collision = new BotBotCollision(a, b);
        collision.handle();

        assertThat(a.getLocation(), is(closeTo(pt(5.5, 4.5))));
        assertThat(b.getLocation(), is(closeTo(pt(1.5, 2.5))));
    }


    @Test
    public void handleBacksUpBothBots() {
        // Bots were lined up but not penetrating
        Bot a = Tests.buildBot(game, pt(4.5, 4.5), Tests.mockTeam());
        Bot b = Tests.buildBot(game, pt(5.5, 4.5), Tests.mockTeam());

        // Suddenly, they collide
        a.setLocation(pt(4.9, 4.5));
        b.setLocation(pt(5.2, 4.5));

        BotBotCollision collision = new BotBotCollision(a, b);
        collision.handle();

        assertThat(a.getLocation(), is(closeTo(pt(4.75, 4.5))));
        assertThat(b.getLocation(), is(closeTo(pt(5.35, 4.5))));
    }


    @Test
    public void handleFiresOnCollisionWithBotEventOnBothBots() {
        // Bots were lined up but not penetrating
        Bot a = Tests.buildBot(game, pt(4.5, 4.5), Tests.mockTeam());
        Bot b = Tests.buildBot(game, pt(5.5, 4.5), Tests.mockTeam());

        final AtomicBoolean aEventFired = new AtomicBoolean(false);
        a.onCollisionWithBot().subscribe(ignore -> aEventFired.set(true));

        final AtomicBoolean bEventFired = new AtomicBoolean(false);
        b.onCollisionWithBot().subscribe(ignore -> bEventFired.set(true));

        // Suddenly, they collide
        a.setLocation(pt(4.9, 4.5));
        b.setLocation(pt(5.2, 4.5));

        BotBotCollision collision = new BotBotCollision(a, b);
        collision.handle();

        assertThat(aEventFired.get(), is(true));
        assertThat(bEventFired.get(), is(true));
    }
}
