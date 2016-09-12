package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.Matchers;
import io.github.pureza.warbots.geometry.Vector;
import org.hamcrest.core.Is;
import org.junit.Test;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.navigation.Map;
import io.github.pureza.warbots.weaponry.Bullet;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static io.github.pureza.warbots.Matchers.closeTo;
import static io.github.pureza.warbots.Tests.*;
import static io.github.pureza.warbots.geometry.Point.pt;

public class MovingEntityTest {

    /*
     * void update(long dt)
     */

    @Test
    public void updateMovesEntity() {
        Bot bot = mockBot();
        Bullet bullet = buildBullet(bot.getGame(), pt(0, 0), Math.PI);

        assertThat(bullet.getLocation(), is(closeTo(pt(0, 0))));
        bullet.update(1000);

        // A bullet moves at 25 units per second
        assertThat(bullet.getLocation(), is(closeTo(pt(-25, 0))));
    }


    /*
     * Point toWorldCoordinates(Point point)
     */

    @Test
    public void toWorldCoordinatesConvertsOrigin() {
        Bot bot = mockBot();
        Bullet bullet = buildBullet(bot.getGame(), pt(5.0, 7.0));
        assertThat(bullet.toWorldCoordinates(pt(0, 0)), is(closeTo(pt(5.0, 7.0))));
    }


    @Test
    public void toWorldCoordinatesConvertsPointInFrontOfUnrotatedEntity() {
        Bot bot = mockBot();
        Bullet bullet = buildBullet(bot.getGame(), pt(5.0, 7.0));
        assertThat(bullet.toWorldCoordinates(pt(1.0, 2.0)), is(closeTo(pt(6.0, 9.0))));
    }


    @Test
    public void toWorldCoordinatesConvertsPointInFrontOfRotatedEntity() {
        Bot bot = mockBot();
        Bullet bullet = buildBullet(bot.getGame(), pt(5.0, 7.0), Math.PI);
        assertThat(bullet.toWorldCoordinates(pt(1.0, 2.0)), is(closeTo(pt(4.0, 5.0))));
    }


    /*
     * Set<Point> getOccupyingCells()
     */

    @Test
    public void entityOccupiesSingleCell() {
        Map map = new Map(10, 10);
        Bot bot = buildBot(mockGame(map), pt(5.5, 5.5), mockTeam());

        assertThat(bot.getOccupyingCells(), containsInAnyOrder(pt(5, 5)));
    }


    @Test
    public void entityOccupiesNorth() {
        Map map = new Map(10, 10);
        Bot bot = buildBot(mockGame(map), pt(5.5, 6.0), mockTeam());

        assertThat(bot.getOccupyingCells(), containsInAnyOrder(pt(5, 5), pt(5, 6)));
    }


    /*
     * Vector getHeadingVector()
     */


    @Test
    public void getHeadingVectorReturnsRightVectorWhenNoRotation() {
        Map map = new Map(10, 10);
        Bot bot = buildBot(mockGame(map), pt(0, 0), mockTeam());
        assertThat(bot.getHeadingVector(), Is.is(Vector.vec(1, 0)));
    }


    @Test
    public void getHeadingVectorReturnsUnitVectorInFirstQuadrant() {
        Map map = new Map(10, 10);
        Bot bot = buildBot(mockGame(map), pt(0, 0), mockTeam());
        bot.setRotation(Math.PI / 4);
        assertThat(bot.getHeadingVector(), is(Matchers.closeTo(Vector.vec(1, 1).normalize())));
    }


    @Test
    public void getHeadingVectorReturnsUnitVectorInFourthQuadrant() {
        Map map = new Map(10, 10);
        Bot bot = buildBot(mockGame(map), pt(0, 0), mockTeam());
        bot.setRotation(Math.PI / 4 + Math.PI);
        assertThat(bot.getHeadingVector(), is(Matchers.closeTo(Vector.vec(-1, -1).normalize())));
    }


    /*
     * void setLocation(Point location)
     */

    @Test
    public void setLocationSetsTheNewLocation() {
        Map map = new Map(10, 10);
        Bot bot = buildBot(mockGame(map), pt(0, 0), mockTeam());
        bot.setLocation(pt(1, 0));
        assertThat(bot.getLocation(), is(pt(1, 0)));
    }


    @Test
    public void setLocationSavesThePreviousLocation() {
        Map map = new Map(10, 10);
        Bot bot = buildBot(mockGame(map), pt(0, 0), mockTeam());
        bot.setLocation(pt(1, 0));
        bot.setLocation(pt(1, 2));
        assertThat(bot.getPreviousLocation(), is(pt(1, 0)));
    }


    /*
     * void render(Graphics2D graphics)
     */
    @Test
    public void renderPositionsTheSprite() {
        BufferedImage image = mock(BufferedImage.class);
        when(image.getWidth()).thenReturn(16);
        when(image.getHeight()).thenReturn(16);

        Map map = new Map(10, 10);
        Game game = mockGame(map);
        when(game.getImage(any())).thenReturn(image);

        Bot bot = buildBot(game, pt(1, 8), mockTeam());
        bot.initResources();
        bot.render(mock(Graphics2D.class));

        assertThat(bot.sprite.getX(), is(24.0));
        assertThat(bot.sprite.getY(), is(56.0));
    }


    /*
     * void restoreLocation()
     */

    @Test
    public void restoreLocationRestoresPreviousLocation() {
        Map map = new Map(10, 10);
        Bot bot = buildBot(mockGame(map), pt(0, 0), mockTeam());
        bot.setLocation(pt(1, 0));

        bot.restoreLocation();

        assertThat(bot.getLocation(), is(pt(0, 0)));
    }


    @Test
    public void restoreLocationDoesntBackupOverwrittenLocation() {
        Map map = new Map(10, 10);
        Bot bot = buildBot(mockGame(map), pt(0, 0), mockTeam());
        bot.setLocation(pt(1, 0));

        bot.restoreLocation();

        // Not (1, 0)
        assertThat(bot.getPreviousLocation(), is(pt(0, 0)));
    }

    // Test that it doesnt set the overwritten location as the previous location
}
