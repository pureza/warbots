package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.Matchers;
import io.github.pureza.warbots.config.ProjectileConfig;
import io.github.pureza.warbots.entities.FirstAidItem;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.memory.ItemMemoryRecord;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.Wall;
import io.github.pureza.warbots.entities.WeaponItem;
import io.github.pureza.warbots.geometry.Size;
import io.github.pureza.warbots.navigation.Map;
import io.github.pureza.warbots.weaponry.Bullet;
import io.github.pureza.warbots.weaponry.Projectile;
import io.github.pureza.warbots.weaponry.Weapon;

import java.awt.image.BufferedImage;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static io.github.pureza.warbots.Matchers.closeTo;
import static io.github.pureza.warbots.Tests.*;
import static io.github.pureza.warbots.geometry.Point.pt;

public class CollisionHandlerTest {

    private Game game;

    private CollisionHandler handler;

    private CollisionDetector detector;

    @Before
    public void setUp() {
        Map map = new Map(10, 10);

        Config config = new TestConfig();
        Game realGame = new Game(config, map, null, null);

        game = spy(realGame);

        // Mock Game.getImage() so that tests can run anyway
        doReturn(mock(BufferedImage.class)).when(game).getImage(any());

        this.handler = new CollisionHandler(game);
        this.detector = new CollisionDetector(game);
    }


    @Test
    public void handlesBotBotCollisions() {
        // Bots a and b are not colliding initially
        Bot a = buildBot(game, pt(5.8, 5.0), mockTeam());
        Bot b = buildBot(game, pt(6.7, 5.0), mockTeam());

        game.addBot(a);
        game.addBot(b);

        // Eventually, they collide (overlap=0.1)
        a.setLocation(pt(6.0, 5.0));
        b.setLocation(pt(6.5, 5.0));

        handler.handle();

        // The handler moves the bots away from each other
        assertThat(a.getLocation(), is(closeTo(pt(5.95, 5.0))));
        assertThat(b.getLocation(), is(closeTo(pt(6.55, 5.0))));
    }


    @Test
    public void handlesBotWallCollisions() {
        Wall wall = new Wall(pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall);

        // The bot was not penetrating the wall before
        Bot bot = buildBot(game, pt(6.5, 5.5), mockTeam());
        game.addBot(bot);

        // Eventually, the bot goes collides with the wall
        bot.setLocation(pt(6.1, 5.5));

        handler.handle();

        // The handler moves the bot away from the wall
        assertThat(bot.getLocation(), is(closeTo(pt(6.3, 5.5))));
    }


    @Test
    public void handlesBotProjectileCollisions() {
        Bot bot = buildBot(game, pt(6.5, 5.5), mockTeam());
        game.addBot(bot);

        int prevHealth = bot.getHealth();

        Projectile bullet = buildBullet(game, pt(5.5, 5.5));
        game.addProjectile(bullet);

        // The bullet hit the bot
        bullet.setLocation(pt(7.5, 5.5));

        handler.handle();

        assertThat(bot.getHealth(), is(not(prevHealth)));
    }


    @Test
    public void handlesBotItemCollisions() {
        // The bot was not colliding with the item before
        Bot bot = buildBot(game, pt(7, 5.5), mockTeam());
        game.addBot(bot);

        double prevStrength = bot.getTotalWeaponStrength();

        WeaponItem item = buildWeaponItem(pt(7.5, 5.5), Weapon.WeaponType.ROCKET_LAUNCHER);
        game.getMap().addItem(item);
        bot.getMemory().getItemMemory().getRecords().put(item, new ItemMemoryRecord(item.getActivationInterval(), ItemMemoryRecord.State.ACTIVE));

        // Eventually, the bot goes collides with the item
        bot.setLocation(pt(7.3, 5.5));

        handler.handle();

        // The handler deactivates the item
        assertThat(item.isActive(), is(false));

        // The weapon strength has increased after the bot picked up the item
        assertThat(bot.getTotalWeaponStrength(), is(not(prevStrength)));
    }


    @Test
    public void handlesProjectileWallCollisions() {
        Wall wall = new Wall(pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall);

        // The bullet is heading towards the wall
        Projectile bullet = buildBullet(game, pt(4, 5.5));
        game.addProjectile(bullet);

        // The bullet hits the wall
        bullet.setLocation(pt(5.5, 5.5));

        handler.handle();

        // The bullet gets marked for deletion
        assertThat(game.getLostProjectiles(), contains(bullet));
    }


    @Test
    public void handlingBotBotCollisionsMayGenerateNewBotBotCollisions() {
        // The bots are not colliding initially
        Bot a = buildBot(game, pt(7, 5), mockTeam());
        Bot b = buildBot(game, pt(7.7, 5), mockTeam());
        Bot c = buildBot(game, pt(8.5, 5), mockTeam());

        game.addBot(a);
        game.addBot(b);
        game.addBot(c);

        // Suddently, bot collides with bot b
        b.setLocation(pt(7.7, 5));
        c.setLocation(pt(8.0, 5));

        BotBotCollision bC = new BotBotCollision(b, c);
        assertThat(detector.detectBotBotCollisions(), contains((Matcher) Matchers.closeTo(bC)));

        handler.handle();

        // As a response, bots b and c are moved and now bot b collides with bot a
        assertThat(b.getLocation(), is(closeTo(pt(7.55, 5))));
        assertThat(c.getLocation(), is(closeTo(pt(8.15, 5))));

        BotBotCollision aB = new BotBotCollision(a, b);
        assertThat(detector.detectBotBotCollisions(), contains((Matcher) Matchers.closeTo(aB)));
    }


    @Test
    public void handlingBotBotCollisionsMayGenerateNewBotWallCollisions() {
        Wall wall = new Wall(pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall);

        // Bot a is very close to the wall
        Bot a = buildBot(game, pt(6.4, 5), mockTeam());

        // Bot b is next to bot a
        Bot b = buildBot(game, pt(7.1, 5), mockTeam());

        game.addBot(a);
        game.addBot(b);

        // Suddently, bot b collides with bot a
        a.setLocation(a.getLocation());
        b.setLocation(pt(6.6, 5));

        BotBotCollision botBotCollision = new BotBotCollision(a, b);
        assertThat(detector.detectBotBotCollisions(), contains((Matcher) Matchers.closeTo(botBotCollision)));

        handler.handleBotBotCollisions();

        // As a response, bot a is moved and collides with the wall
        assertThat(a.getLocation(), is(closeTo(pt(6.2, 5))));

        BotWallCollision botWallCollision = new BotWallCollision(a, wall, a.getLocation(), wall.getLocation());
        assertThat(detector.detectBotWallCollisions(), contains(botWallCollision));
    }


    @Test
    public void handlerFixesNewBotWallCollisionsDueToBotBotCollisionResponse() {
        Wall wall = new Wall(pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall);

        // Bot a is very close to the wall
        Bot a = buildBot(game, pt(6.4, 5), mockTeam());

        // Bot b is next to bot a
        Bot b = buildBot(game, pt(7.1, 5), mockTeam());

        game.addBot(a);
        game.addBot(b);

        // Suddently, bot b collides with bot a
        a.setLocation(a.getLocation());
        b.setLocation(pt(6.6, 5));

        BotBotCollision botBotCollision = new BotBotCollision(a, b);
        assertThat(detector.detectBotBotCollisions(), contains((Matcher) Matchers.closeTo(botBotCollision)));

        handler.handle();

        // As a response, bot a is moved but not too much, so as not to collide
        // with the wall
        assertThat(a.getLocation(), is(closeTo(pt(6.3, 5))));
        assertThat(detector.detectBotWallCollisions(), is(empty()));
    }


    @Test
    public void handlingBotWallCollisionsMayGenerateNewBotWallCollisions() {
        Wall leftWall = new Wall(pt(4, 5), new Size(1, 1));
        game.getMap().addEntity(leftWall);

        Wall rightWall = new Wall(pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(rightWall);

        // The bot is very to the left of both walls
        Bot bot = buildBot(game, pt(3, 5.5), mockTeam());
        game.addBot(bot);

        // Suddently, the bot collides with the rightmost wall (and passes over
        // the leftmost wall)
        bot.setLocation(pt(6.1, 5.5));

        BotWallCollision rightWallCollision = new BotWallCollision(bot, rightWall, bot.getLocation(), rightWall.getLocation());
        assertThat(detector.detectBotWallCollisions(), contains(rightWallCollision));

        handler.handleBotWallCollisions(1);

        // As a response, the bot is moved to the left of the rightmost wall,
        // colliding with the leftmost one!
        assertThat(bot.getLocation(), is(closeTo(pt(4.7, 5.5))));

        BotWallCollision leftWallCollision = new BotWallCollision(bot, leftWall, pt(4.7, 5.5), leftWall.getLocation());
        assertThat(detector.detectBotWallCollisions(), contains(leftWallCollision));
    }


    @Test
    public void handlerFixesNewBotWallCollisionsDueToBotWallCollisionResponse() {
        Wall leftWall = new Wall(pt(4, 5), new Size(1, 1));
        game.getMap().addEntity(leftWall);

        Wall rightWall = new Wall(pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(rightWall);

        // The bot is to the left of both walls
        Bot bot = buildBot(game, pt(3, 5.5), mockTeam());
        game.addBot(bot);

        // Suddently, the bot collides with the rightmost wall (and passes over
        // the leftmost wall)
        bot.setLocation(pt(6.1, 5.5));

        BotWallCollision rightWallCollision = new BotWallCollision(bot, rightWall, bot.getLocation(), rightWall.getLocation());
        assertThat(detector.detectBotWallCollisions(), contains(rightWallCollision));

        handler.handle();

        // As a response, the bot is moved to the left of the leftmost wall
        assertThat(bot.getLocation(), is(closeTo(pt(3.7, 5.5))));

        // All collisions are fixed!
        assertThat(detector.detectBotWallCollisions(), is(empty()));
    }


    @Test
    public void handlerMovesCollidingBotsBeforeProjectilesHitThem() {
        // Bots a and b are not colliding initially
        Bot a = buildBot(game, pt(5.8, 5.0), mockTeam());
        Bot b = buildBot(game, pt(6.7, 5.0), mockTeam());

        game.addBot(a);
        game.addBot(b);

        // The bullet is heading towards bot a
        ProjectileConfig config = new TestConfig().projectiles().get(Weapon.WeaponType.HANDGUN);
        Projectile bullet = new Bullet(game, mockBot(), pt(4, 5.0), 0, config.speed(), 0.1, a.getHealth() + 1);
        game.addProjectile(bullet);

        // Eventually, the bots collide (overlap=0.1)
        a.setLocation(pt(6.0, 5.0));
        b.setLocation(pt(6.5, 5.0));

        // At the same time, the bullet is really close to bot a, but not touching it yet
        bullet.setLocation(pt(5.65, 5.0));
        assertThat(detector.detectBotProjectileCollisions(), is(empty()));

        handler.handle();

        // The handler moves the bots away from each other
        assertThat(a.getLocation(), is(closeTo(pt(5.95, 5.0))));
        assertThat(b.getLocation(), is(closeTo(pt(6.55, 5.0))));

        // The bullet hits bot a due to the bot/bot collision
        assertThat(a.isDead(), is(true));

        // The bullet is removed from the game
        assertThat(game.getLostProjectiles(), contains(bullet));
    }


    @Test
    public void handlerMovesBotsCollidingWithWallBeforeProjectilesHitThem() {
        Wall wall = new Wall(pt(5, 5), new Size(1, 1));
        game.getMap().addEntity(wall);

        // The bot is very to the left of the wall
        Bot bot = buildBot(game, pt(4.5, 5.5), mockTeam());
        game.addBot(bot);

        // The bullet is heading towards the bot
        ProjectileConfig config = new TestConfig().projectiles().get(Weapon.WeaponType.HANDGUN);
        Projectile bullet = new Bullet(game, mockBot(), pt(4, 5.5), 0, config.speed(), 0.1, bot.getHealth() + 1);
        game.addProjectile(bullet);

        // Eventually, the bot enters the wall
        bot.setLocation(pt(5.0, 5.5));

        // At the same time, the bullet is really close to bot a, but not touching it yet
        bullet.setLocation(pt(4.65, 5.5));
        assertThat(detector.detectBotProjectileCollisions(), is(empty()));

        handler.handle();

        // The handler moves the bot away from the wall
        assertThat(bot.getLocation(), is(closeTo(pt(4.7, 5.5))));

        // The bullet hits bot a due to the bot/wall collision handler
        assertThat(bot.isDead(), is(true));

        // The bullet is removed from the game
        assertThat(game.getLostProjectiles(), contains(bullet));
    }


    @Test
    public void botPicksItemBeforeBeingHitByProjectile() {
        // The bot is to the left of the first aid kit
        Bot bot = buildBot(game, pt(4.0, 5.5), mockTeam());
        game.addBot(bot);

        // The bullet is heading towards the bot
        Projectile bullet = buildBullet(game, pt(3, 5.5));
        game.addProjectile(bullet);

        // The bot is almost dying
        bot.inflictDamage(bullet, bot.getHealth() - 1);

        FirstAidItem item = buildFirstAidItem(pt(4.5, 5.5));
        game.getMap().addItem(item);
        bot.getMemory().getItemMemory().getRecords().put(item, new ItemMemoryRecord(item.getActivationInterval(), ItemMemoryRecord.State.ACTIVE));

        // The bot collides with the item
        bot.setLocation(pt(4.3, 5.5));

        // At the same time, the projectile collides with the bot
        bullet.setLocation(pt(4.2, 5.5));

        handler.handle();

        // The bot is still alive, because it caught the item before being hit
        assertThat(bot.isDead(), is(false));
        assertThat(bot.getHealth(), is(1 + item.getHealthAmount() - bullet.getDamage()));

        // The projectile has hit the bot
        assertThat(game.getLostProjectiles(), contains(bullet));
    }
}
