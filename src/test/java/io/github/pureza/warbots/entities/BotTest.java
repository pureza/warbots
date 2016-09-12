package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.Matchers;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.geometry.Vector;
import io.github.pureza.warbots.memory.ItemMemoryRecord;
import io.github.pureza.warbots.steering.PursuitBehavior;
import io.github.pureza.warbots.weaponry.Projectile;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Size;
import io.github.pureza.warbots.navigation.Map;
import io.github.pureza.warbots.steering.SeekBehavior;
import io.github.pureza.warbots.weaponry.HandGun;
import io.github.pureza.warbots.weaponry.Weapon;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static io.github.pureza.warbots.Tests.*;
import static io.github.pureza.warbots.Matchers.closeTo;
import static io.github.pureza.warbots.geometry.Point.pt;

public class BotTest {

    private Bot bot;


    @Before
    public void setUp() {
        Map map = new Map(10, 10);
        map.buildNavGraph();

        Game game = mockGame(map);
        Team team = mockTeam();
        bot = buildBot(game, pt(0, 0), team);
    }


    /*
     * void acquireHealth(int amount)
     */

    @Test
    public void acquireHealthIncreasesHealth() {
        Projectile projectile = mock(Projectile.class);
        when(projectile.getHeadingVector()).thenReturn(Vector.vec(1, 0));

        bot.inflictDamage(projectile, 20);
        assertThat(bot.getHealth(), is(80));
        bot.acquireHealth(10);
        assertThat(bot.getHealth(), is(90));
    }


    @Test
    public void acquireHealthIncreasesHealthUpToMaximum() {
        Projectile projectile = mock(Projectile.class);
        when(projectile.getHeadingVector()).thenReturn(Vector.vec(1, 0));

        bot.inflictDamage(projectile, 20);
        assertThat(bot.getHealth(), is(80));
        bot.acquireHealth(200);
        assertThat(bot.getHealth(), is(100));
    }


    /*
     * void inflictDamage(int damage)
     */

    @Test
    public void inflictDamageDecreasesHealth() {
        Projectile projectile = mock(Projectile.class);
        when(projectile.getHeadingVector()).thenReturn(Vector.vec(1, 0));

        bot.inflictDamage(projectile, 20);
        assertThat(bot.getHealth(), is(80));
    }


    @Test
    public void inflictDamageKillsTheBotWhenHealthReachesZero() {
        Projectile projectile = mock(Projectile.class);
        when(projectile.getHeadingVector()).thenReturn(Vector.vec(1, 0));

        bot.inflictDamage(projectile, 100);
        assertThat(bot.getHealth(), is(0));
        assertThat(bot.isDead(), is(true));

        Mockito.verify(bot.getGame()).removeBot(bot);
    }


    @Test
    public void inflictDamageRemembersShotDirection() {
        Projectile projectile = mock(Projectile.class);
        when(projectile.getHeadingVector()).thenReturn(Vector.vec(1, 0));

        bot.inflictDamage(projectile, 100);

        assertThat(bot.getMemory().getShotMemory().getLastShotDirection(), is(Matchers.closeTo(Vector.vec(-1, 0))));
    }


    /*
     * boolean isInFov(Bot other)
     */

    @Test
    public void botCantSeeHimself() {
        assertThat(bot.isInFov(bot), is(false));
    }


    @Test
    public void botCanSeeBotInFront() {
        Bot other = mockBot(pt(5, 0));
        assertThat(bot.isInFov(other), is(true));
    }


    @Test
    public void botCantSeeBotBehind() {
        bot.setLocation(pt(5, 5));
        Bot other = mockBot(pt(1, 5));
        assertThat(bot.isInFov(other), is(false));
    }


    @Test
    public void rotatedBotCanSeeBotInFront() {
        bot.setLocation(pt(2, 0));
        bot.setRotation(Math.PI / 2);
        Bot other = mockBot(pt(1, 1));
        assertThat(bot.isInFov(other), is(true));
    }


    @Test
    public void rotatedBotCantSeeBotBehind() {
        bot.setLocation(pt(1, 1));
        bot.setRotation(Math.PI / 2);
        Bot other = mockBot(pt(2, 0));
        assertThat(bot.isInFov(other), is(false));
    }


    @Test
    public void botCantSeeBotHiddenByWall() {
        Bot other = mockBot(pt(5, 0));
        bot.getGame().getMap().addEntity(new Wall(pt(3, 0), new Size(1, 1)));
        assertThat(bot.isInFov(other), is(false));
    }


    @Test
    public void botCantSeeBotToTheLeftAndBack() {
        Bot other = mockBot(pt(0, 2));
        assertThat(bot.isInFov(other), is(false));
    }


    @Test
    public void botCantSeeBotToTheRightAndBack() {
        bot.setLocation(pt(2, 2));
        Bot other = mockBot(pt(1, 0));
        assertThat(bot.isInFov(other), is(false));
    }


    @Test
    public void botCanSeeBotToTheLeftAndFront() {
        Bot other = mockBot(pt(0.5, 2));
        assertThat(bot.isInFov(other), is(true));
    }


    @Test
    public void botCanSeeBotToTheRightAndFront() {
        bot.setLocation(pt(0, 5));
        Bot other = mockBot(pt(0.5, 3));
        assertThat(bot.isInFov(other), is(true));
    }


    /*
     * List<Bot> getBotsInFov
     */

    @Test
    public void getBotsInFovReturnsEmptyWhenThereAreNoOtherBots() {
        when(bot.getGame().getBots()).thenReturn(singletonList(bot));
        assertThat(bot.getBotsInFov(), is(empty()));
    }


    @Test
    public void getBotsInFovReturnsOnlyBotsInFov() {
        Bot visible = mockBot(pt(5, 0));
        Bot invisible = mockBot(pt(-5, 0));

        when(bot.getGame().getBots()).thenReturn(asList(bot, visible, invisible));
        assertThat(bot.getBotsInFov(), contains(visible));
    }


    /*
     * boolean isShootable(Bot enemy)
     */

    @Test
    public void isShootableReturnsTrueIfProjectileCanTravelToEnemy() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        Game game = mockGame(map);

        Bot a = buildBot(game, pt(0.5, 0.5), mockTeam());
        Bot b = buildBot(game, pt(2.5, 0.5), mockTeam());

        assertThat(a.isShootable(b), is(true));
    }


    @Test
    public void isShootableReturnsFalseIfProjectileWouldHitWallDuringItsCourse() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        Game game = mockGame(map);

        Bot a = buildBot(game, pt(0.5, 0.5), mockTeam());
        Bot b = buildBot(game, pt(2.5, 1.5), mockTeam());

        assertThat(a.isShootable(b), is(false));
    }


    /*
     * boolean canMoveTo(Point target)
     */

        /*
     * boolean canMoveTo(Point source, Point target, double boundingRadius)
     */

    @Test
    public void canMoveToReturnsTrueWhenPathIsNotObstructed() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        Game game = mockGame(map);

        Bot bot = buildBot(game, pt(0.5, 0.5), mockTeam());

        assertThat(bot.canMoveTo(pt(1.5, 0.5)), is(true));
    }


    @Test
    public void canMoveToReturnsFalseWhenBotBumpsIntoWallDuringCourse() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        Game game = mockGame(map);

        Bot bot = buildBot(game, pt(0.5, 0.5), mockTeam());

        assertThat(bot.canMoveTo(pt(2.5, 1.5)), is(false));
    }


    @Test
    public void canMoveBetweenReturnsFalseWhenBotPenetratesWallAtTheEnd() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        Game game = mockGame(map);

        Bot bot = buildBot(game, pt(1.5, 0.5), mockTeam());

        assertThat(bot.canMoveTo(pt(1.5, 0.71)), is(false));
    }
    
    
    /*
     * acquireWeapon(Weapon weapon)
     */

    @Test
    public void acquireWeaponAcquiresNewWeapon() {
        bot.acquireWeapon(buildLaserGun(bot));
        assertThat(bot.getArtillery().getWeapons().keySet(), hasItem(Weapon.WeaponType.LASER_GUN));
    }


    @Test
    public void acquireWeaponTakesAmmoFromExistingWeapon() {
        int initialAmmo = bot.getArtillery().getWeapons().get(Weapon.WeaponType.HANDGUN).getRemainingAmmo();

        HandGun otherGun = buildHandGun(bot);
        bot.acquireWeapon(otherGun);

        assertThat(bot.getArtillery().getWeapons().get(Weapon.WeaponType.HANDGUN).getRemainingAmmo(), is(greaterThan(initialAmmo)));
    }


    /*
     * void seek(Point target)
     */

    @Test
    public void seekEnablesTheSeekBehavior() {
        bot.seek(pt(10, 10));
        assertThat(bot.getSteeringBehavior(), is(instanceOf(SeekBehavior.class)));
    }


    /*
     * void pursue(Bot target)
     */

    @Test
    public void pursueEnablesThePursuitBehavior() {
        Bot other = mockBot();
        bot.pursue(other);

        assertThat(bot.getSteeringBehavior(), is(instanceOf(PursuitBehavior.class)));
    }



    /*
     * void stop()
     */

    @Test
    public void stopStopsTheBot() {
        bot.seek(pt(10, 10));
        bot.stop();
        assertThat(bot.getSteeringBehavior(), is(nullValue()));
    }


    /*
     * Path<Point> findPathTo(Point target)
     */

    @Test
    public void findPathFindsPathToTarget() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        Game game = mockGame(map);

        Bot bot = buildBot(game, pt(0.4, 1.6), mockTeam());

        assertThat(bot.findPathTo(pt(2.4, 1.4)).getLocations(), contains(pt(0.4, 1.6), pt(0.5, 0.5),
                pt(2.5, 0.5), pt(2.4, 1.4)));
    }


    /*
     * void fireAt(Bot other)
     */

    @Test
    public void fireAtFiresAtEnemyBotsInFov() {
        // I'm at (0, 0) and looking towards (1, 0), so this enemy is right in front of me
        Bot enemy = mockBot(pt(1, 0), Vector.vec(0, 1), 0.3, 0);

        bot.fireAt(enemy, 1000);

        // Make sure the projectile is added to the game
        Mockito.verify(bot.getGame(), times(1)).addProjectile(any());
    }


    @Test
    public void fireAtIgnoresBotsOutsideFov() {
        // I'm at (0, 0) and looking towards (1, 0), so this enemy is outside my field of vision
        Bot enemy = mockBot(pt(-1, 1), Vector.vec(0, 1), 0.3, 0);

        bot.fireAt(enemy, 1000);

        // Make sure no projectile is added to the game
        Mockito.verify(bot.getGame(), never()).addProjectile(any());
    }


    /*
     * boolean rotateFacing(Vector direction)
     */

    @Test(expected=IllegalArgumentException.class)
    public void rotateFacingFailsForNullDirectionVector() {
        bot.rotateFacing(Vector.vec(0, 0), 1000);
    }


    @Test
    public void rotateFacingDoesNotRotateIfAlreadyFacingDirection() {
        assertThat(bot.rotateFacing(Vector.vec(1, 0), 1000), is(true));
        assertThat(bot.getRotation(), is(0.0));
    }


    @Test
    public void rotateFacingRotatesClockwise() {
        assertThat(bot.rotateFacing(Vector.vec(0, -1), 1000), is(false));
        assertThat(bot.getRotation(), Is.is(-bot.maxTurnRate));
    }


    @Test
    public void rotateFacingRotatesCounterClockwise() {
        assertThat(bot.rotateFacing(Vector.vec(0, 1), 1000), is(false));
        assertThat(bot.getRotation(), Is.is(bot.maxTurnRate));
    }


    @Test
    public void rotateFacingFinishesRotationInOneStep() {
        // Rotate by pi/24 degrees, which is less than maxTurnRate
        assertThat(bot.rotateFacing(Vector.vec(Math.cos(Math.PI / 24.0), Math.sin(Math.PI / 24.0)), 1000), is(true));
        assertThat(bot.getRotation(), is(Matchers.closeTo(Math.PI / 24)));
    }


    @Test
    public void rotateFacingRotatesAccordingToTime() {
        assertThat(bot.rotateFacing(Vector.vec(0, 1), 500), is(false));
        assertThat(bot.getRotation(), Is.is(bot.maxTurnRate / 2));
    }


    /*
     * boolean isSameTeam(Bot other)
     */

    @Test
    public void friendlyBotsAreOnTheSameTeam() {
        Bot friendly = buildBot(bot.game, pt(5, 5), bot.getTeam());
        assertThat(bot.isSameTeam(friendly), is(true));
    }


    @Test
    public void enemyBotsAreNotOnTheSameTeam() {
        Bot enemy = buildBot(bot.game, pt(5, 5), mockTeam());
        assertThat(bot.isSameTeam(enemy), is(false));
    }


    /*
     * void update(long dt)
     */

    @Test
    public void updateSetsNewVelocity() {
        bot.seek(pt(5, 5));
        assertThat(bot.getVelocity(), Is.is(Vector.vec(0, 0)));

        bot.update(1000);
        assertThat(bot.getVelocity().normalize(), Is.is(Vector.vec(Math.sqrt(2)/2, Math.sqrt(2)/2)));
    }


    @Test
    public void updateMovesBot() {
        bot.seek(pt(5, 5));
        assertThat(bot.getLocation(), is(pt(0, 0)));

        bot.update(1000);
        assertThat(bot.getLocation(), is(closeTo(pt(Math.sqrt(2), Math.sqrt(2)))));
    }


    @Test
    public void updateUpdatesMemory() {
        FirstAidItem item = buildFirstAidItem(pt(2.5, 2.5));
        bot.getGame().getMap().addItem(item);

        bot.getMemory().getItemMemory().getRecords().put(item, new ItemMemoryRecord(item.getActivationInterval(), ItemMemoryRecord.State.UNKNOWN));

        bot.update(1000);

        assertThat(bot.getMemory().getItemRecord(item).getState(), is(ItemMemoryRecord.State.ACTIVE));
    }
}
