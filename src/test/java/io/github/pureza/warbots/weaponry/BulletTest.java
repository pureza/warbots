package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.Matchers;
import io.github.pureza.warbots.geometry.Vector;
import org.junit.Test;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static io.github.pureza.warbots.Tests.buildBullet;
import static io.github.pureza.warbots.Tests.mockBot;
import static io.github.pureza.warbots.Matchers.closeTo;

public class BulletTest {

    @Test
    public void bulletVelocityPointsCorrectly() {
        Bot bot = mockBot();

        Bullet bullet = buildBullet(bot.getGame(), Point.pt(0, 0), Math.PI);
        assertThat(bullet.getVelocity().normalize(), is(Matchers.closeTo(Vector.vec(-1, 0))));
    }


    @Test
    public void bulletSpeedIsCorrect() {
        Bot bot = mockBot();

        Bullet bullet = buildBullet(bot.getGame(), Point.pt(0, 0), Math.PI);
        assertThat(bullet.getVelocity().norm(), is(25.0));
    }
}
