package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.Tests;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.InventoryItem;
import io.github.pureza.warbots.entities.Wall;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Size;
import io.github.pureza.warbots.navigation.Map;
import io.github.pureza.warbots.weaponry.Projectile;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.junit.Before;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static io.github.pureza.warbots.Matchers.closeTo;

public class CollisionDetectorTest {

    private Game game;

    private CollisionDetector detector;

    @Before
    public void setUp() {
        Map map = new Map(10, 10);
        Game realGame = new Game(new TestConfig(), map, null, null);

        game = spy(realGame);

        // Mock Game.getImage() so that tests can run anyway
        doReturn(mock(BufferedImage.class)).when(game).getImage(any());

        this.detector = new CollisionDetector(game);
    }

    /*
     * List<Collision> detectBotBotCollisions()
     */

    @Test
    public void detectBotBotCollisionsDetectsCollisionBetweenTwoBots() {
        Bot a = Tests.buildBot(game, Point.pt(5.0, 5.0), Tests.mockTeam());
        game.addBot(a);

        Bot b = Tests.buildBot(game, Point.pt(5.1, 5.0), Tests.mockTeam());
        game.addBot(b);

        BotBotCollision expected = new BotBotCollision(a, b);
        assertThat((List<BotBotCollision>) (List) detector.detectBotBotCollisions(),
                IsIterableContainingInAnyOrder.containsInAnyOrder(io.github.pureza.warbots.Matchers.closeTo(expected)));
    }


    @Test
    public void detectBotBotCollisionsDetectsCollisionBetweenThreeBots() {
        Bot a = Tests.buildBot(game, Point.pt(5.0, 5.0), Tests.mockTeam());
        game.addBot(a);

        Bot b = Tests.buildBot(game, Point.pt(5.1, 5.0), Tests.mockTeam());
        game.addBot(b);

        Bot c = Tests.buildBot(game, Point.pt(5.0, 5.1), Tests.mockTeam());
        game.addBot(c);

        BotBotCollision expectedAB = new BotBotCollision(a, b);
        BotBotCollision expectedBC = new BotBotCollision(b, c);
        BotBotCollision expectedAC = new BotBotCollision(a, c);

        assertThat((List<BotBotCollision>) (List) detector.detectBotBotCollisions(),
                containsInAnyOrder(io.github.pureza.warbots.Matchers.closeTo(expectedAB), io.github.pureza.warbots.Matchers.closeTo(expectedBC), io.github.pureza.warbots.Matchers.closeTo(expectedAC)));
    }


    @Test
    public void detectBotBotCollisionsDetectsNoCollisionBetweenTwoFarAwatBots() {
        Bot a = Tests.buildBot(game, Point.pt(5.0, 5.0), Tests.mockTeam());
        game.addBot(a);

        Bot b = Tests.buildBot(game, Point.pt(1.0, 2.0), Tests.mockTeam());
        game.addBot(b);

        assertThat(detector.detectBotBotCollisions(), is(empty()));
    }


    /*
     * List<BotItemCollision> detectBotItemCollisions()
     */

    @Test
    public void detectBotItemCollisionsDetectsCollisionBetweenBotAndItem() {
        InventoryItem item = Tests.buildFirstAidItem(Point.pt(5.5, 5.5));
        game.getMap().addItem(item);

        Bot bot = Tests.buildBot(game, Point.pt(5.4, 5.4), Tests.mockTeam());
        game.addBot(bot);

        BotItemCollision expected = new BotItemCollision(bot, item);
        assertThat(detector.detectBotItemCollisions(), containsInAnyOrder(expected));
    }


    @Test
    public void detectBotItemCollisionsDoesntDetectCollisionBetweenBotAndFarAwayItem() {
        InventoryItem item = Tests.buildFirstAidItem(Point.pt(5.5, 5.5));
        game.getMap().addItem(item);

        Bot bot = Tests.buildBot(game, Point.pt(1.0, 1.0), Tests.mockTeam());
        game.addBot(bot);

        assertThat(detector.detectBotItemCollisions(), is(empty()));
    }


    @Test
    public void detectBotItemCollisionsDetectsCollisionBetweenTwoBotsAndOneItem() {
        InventoryItem item = Tests.buildFirstAidItem(Point.pt(5.5, 5.5));
        game.getMap().addItem(item);

        Bot a = Tests.buildBot(game, Point.pt(5.4, 5.4), Tests.mockTeam());
        game.addBot(a);

        Bot b = Tests.buildBot(game, Point.pt(5.6, 5.6), Tests.mockTeam());
        game.addBot(b);

        BotItemCollision expectedA = new BotItemCollision(a, item);
        BotItemCollision expectedB = new BotItemCollision(b, item);
        assertThat(detector.detectBotItemCollisions(), containsInAnyOrder(expectedA, expectedB));
    }


    @Test
    public void detectBotItemCollisionsDoesntDetectCollisionBetweenBotAndInactiveItem() {
        InventoryItem item = Tests.buildFirstAidItem(Point.pt(5.5, 5.5));
        item.deactivate();
        game.getMap().addItem(item);

        Bot bot = Tests.buildBot(game, Point.pt(5.4, 5.4), Tests.mockTeam());
        game.addBot(bot);

        assertThat(detector.detectBotItemCollisions(), is(empty()));
    }


    /*
     * List<Collision> detectBotWallCollisions()
     */

    @Test
    public void detectBotWallCollisionsDetectsCollisionBetweenBotAndWall() {
        Wall wall = new Wall(Point.pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall);

        Bot bot = Tests.buildBot(game, Point.pt(4.9, 4.9), Tests.mockTeam());
        game.addBot(bot);

        assertThat(detector.detectBotWallCollisions(), containsInAnyOrder(new BotWallCollision(bot, wall, Point.pt(4.9, 4.9), Point.pt(5, 5))));
    }


    @Test
    public void detectBotWallCollisionsDetectsCollisionBetweenBotAndTwoWalls() {
        Wall wall1 = new Wall(Point.pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall1);

        Wall wall2 = new Wall(Point.pt(4, 5), new Size(1, 1));
        game.getMap().addEntity(wall2);

        Bot bot = Tests.buildBot(game, Point.pt(4.9, 4.9), Tests.mockTeam());
        game.addBot(bot);

        assertThat(detector.detectBotWallCollisions(),
                containsInAnyOrder(new BotWallCollision(bot, wall1, Point.pt(4.9, 4.9), Point.pt(5, 5)), new BotWallCollision(bot, wall2, Point.pt(4.9, 4.9), Point.pt(4, 5))));
    }


    @Test
    public void detectBotWallCollisionsDetectsCollisionBetweenTwoBotsAndOneWall() {
        Wall wall = new Wall(Point.pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall);

        Bot a = Tests.buildBot(game, Point.pt(4.9, 4.9), Tests.mockTeam());
        game.addBot(a);

        Bot b = Tests.buildBot(game, Point.pt(6.1, 6.1), Tests.mockTeam());
        game.addBot(b);

        assertThat(detector.detectBotWallCollisions(),
                containsInAnyOrder(new BotWallCollision(a, wall, Point.pt(4.9, 4.9), Point.pt(5, 5)), new BotWallCollision(b, wall, Point.pt(6.1, 6.1), Point.pt(5, 5))));
    }


    @Test
    public void detectBotWallCollisionsDoesntDetectCollisionBetweenBotAndFarAwayWall() {
        Wall wall = new Wall(Point.pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall);

        Bot bot = Tests.buildBot(game, Point.pt(1.0, 1.0), Tests.mockTeam());
        game.addBot(bot);

        assertThat(detector.detectBotWallCollisions(), is(empty()));
    }


    /*
     * List<Collision> detectProjectileWallCollisions
     */


    @Test
    public void detectProjectileWallCollisionsDetectsCollisionBetweenProjectileAndWall() {
        Wall wall = new Wall(Point.pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall);

        Projectile bullet = Tests.buildBullet(game, Point.pt(4.999, 4.999));
        game.addProjectile(bullet);

        assertThat(detector.detectProjectileWallCollisions(), containsInAnyOrder(new ProjectileWallCollision(bullet, wall)));
    }


    @Test
    public void detectProjectileWallCollisionsDetectsProjectileOnTopOfWall() {
        Wall wall = new Wall(Point.pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall);

        Projectile bullet = Tests.buildBullet(game, Point.pt(5.4, 5.7));
        game.addProjectile(bullet);

        assertThat(detector.detectProjectileWallCollisions(), containsInAnyOrder(new ProjectileWallCollision(bullet, wall)));
    }


    @Test
    public void detectProjectileWallCollisionsDetectsCollisionBetweenProjectileAndTwoWalls() {
        Wall wall1 = new Wall(Point.pt(4, 5), new Size(1, 1));
        game.getMap().addEntity(wall1);

        Wall wall2 = new Wall(Point.pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall2);

        Projectile bullet = Tests.buildBullet(game, Point.pt(4.999, 4.999));
        game.addProjectile(bullet);

        assertThat(detector.detectProjectileWallCollisions(),
                containsInAnyOrder(new ProjectileWallCollision(bullet, wall1), new ProjectileWallCollision(bullet, wall2)));
    }


    @Test
    public void detectProjectileWallCollisionsDetectsCollisionBetweenTwoBotsAndOneWall() {
        Wall wall = new Wall(Point.pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall);

        Projectile a = Tests.buildBullet(game, Point.pt(4.999, 4.999));
        game.addProjectile(a);

        Projectile b = Tests.buildBullet(game, Point.pt(6.001, 6.001));
        game.addProjectile(b);

        assertThat(detector.detectProjectileWallCollisions(),
                containsInAnyOrder(new ProjectileWallCollision(a, wall), new ProjectileWallCollision(b, wall)));
    }


    @Test
    public void detectProjectileWallCollisionsDoesntDetectCollisionBetweenProjectileAndFarAwayWall() {
        Wall wall = new Wall(Point.pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall);

        Projectile bullet = Tests.buildBullet(game, Point.pt(1, 2));
        game.addProjectile(bullet);

        assertThat(detector.detectProjectileWallCollisions(), is(empty()));
    }


    @Test
    public void detectProjectileWallCollisionsRemovesProjectilesThatAreOutsideTheMap() {
        Wall wall = new Wall(Point.pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall);

        Projectile bullet = Tests.buildBullet(game, Point.pt(10, 3));
        game.addProjectile(bullet);

        assertThat(detector.detectProjectileWallCollisions(), is(empty()));
        MatcherAssert.assertThat(game.getLostProjectiles(), contains(bullet));
    }


    /*
     * List<Collision> detectBotProjectileCollisions()
     */

    @Test
    public void detectBotProjectileCollisionsDetectsCollisionBetweenProjectileAndBot() {
        Bot bot = Tests.buildBot(game, Point.pt(5.0, 5.0), Tests.mockTeam());
        game.addBot(bot);

        Projectile bullet = Tests.buildBullet(game, Point.pt(4.0, 5.0));
        game.addProjectile(bullet);

        // A bullet travels at 25 units/s
        bullet.update(100);

        // The bullet just went across the bot
        assertThat(bullet.getLocation(), is(io.github.pureza.warbots.Matchers.closeTo(Point.pt(6.5, 5.0))));
        assertThat(detector.detectBotProjectileCollisions(), containsInAnyOrder(new BotProjectileCollision(bot, bullet)));
    }


    @Test
    public void detectBotProjectileCollisionsIgnoresBotBehindProjectile() {
        Bot bot = Tests.buildBot(game, Point.pt(3.0, 5.0), Tests.mockTeam());
        game.addBot(bot);

        Projectile bullet = Tests.buildBullet(game, Point.pt(4.0, 5.0));
        game.addProjectile(bullet);

        // A bullet travels at 25 units/s
        bullet.update(100);

        assertThat(bullet.getLocation(), is(io.github.pureza.warbots.Matchers.closeTo(Point.pt(6.5, 5.0))));
        assertThat(detector.detectBotProjectileCollisions(), is(empty()));
    }


    @Test
    public void detectBotProjectileCollisionsSelectsFirstBotToBeHit() {
        Bot closest = Tests.buildBot(game, Point.pt(5.0, 5.0), Tests.mockTeam());
        game.addBot(closest);

        Bot farthest = Tests.buildBot(game, Point.pt(6.0, 5.0), Tests.mockTeam());
        game.addBot(farthest);

        Projectile bullet = Tests.buildBullet(game, Point.pt(4.0, 5.0));
        game.addProjectile(bullet);

        // A bullet travels at 25 units/s
        bullet.update(100);

        // The bullet just went across both bots
        assertThat(bullet.getLocation(), is(io.github.pureza.warbots.Matchers.closeTo(Point.pt(6.5, 5.0))));

        // It only collides with the closest one, though
        assertThat(detector.detectBotProjectileCollisions(), containsInAnyOrder(new BotProjectileCollision(closest, bullet)));
    }


    @Test
    public void detectBotProjectileCollisionsIgnoresBotThatHasntBeenReachedYet() {
        Bot bot = Tests.buildBot(game, Point.pt(7.0, 5.0), Tests.mockTeam());
        game.addBot(bot);

        Projectile bullet = Tests.buildBullet(game, Point.pt(4.0, 5.0));
        game.addProjectile(bullet);

        // A bullet travels at 25 units/s
        bullet.update(100);

        // The hasn't reached the bot yet
        assertThat(bullet.getLocation(), is(io.github.pureza.warbots.Matchers.closeTo(Point.pt(6.5, 5.0))));
        assertThat(detector.detectBotProjectileCollisions(), is(empty()));
    }


    @Test
    public void detectBotProjectileCollisionsDoesntDetectCollisionBetweenProjectileAndBotNotOnRouteOfCollision() {
        Bot bot = Tests.buildBot(game, Point.pt(1.0, 3.0), Tests.mockTeam());
        game.addBot(bot);

        Projectile bullet = Tests.buildBullet(game, Point.pt(4.0, 5.0));
        game.addProjectile(bullet);

        // A bullet travels at 25 units/s
        bullet.update(100);

        // The hasn't reached the bot yet
        assertThat(bullet.getLocation(), is(io.github.pureza.warbots.Matchers.closeTo(Point.pt(6.5, 5.0))));
        assertThat(detector.detectBotProjectileCollisions(), is(empty()));
    }


    @Test
    public void detectBotProjectileCollisionsSelectsFirstPointOfContactWithBot() {
        Bot bot = Tests.buildBot(game, Point.pt(6.5, 5.0), Tests.mockTeam());
        game.addBot(bot);

        Projectile bullet = Tests.buildBullet(game, Point.pt(4.0, 5.0));
        game.addProjectile(bullet);

        // A bullet travels at 25 units/s
        bullet.update(100);

        // The projectile is on top of the bot and so only one point of
        // intersection has been reached
        assertThat(bullet.getLocation(), is(io.github.pureza.warbots.Matchers.closeTo(Point.pt(6.5, 5.0))));
        assertThat(detector.detectBotProjectileCollisions(), containsInAnyOrder(new BotProjectileCollision(bot, bullet)));
    }
}
