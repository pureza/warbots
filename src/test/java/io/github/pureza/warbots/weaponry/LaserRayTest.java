package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.Matchers;
import io.github.pureza.warbots.geometry.Vector;
import org.junit.Test;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Point;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static io.github.pureza.warbots.Tests.buildLaserRay;
import static io.github.pureza.warbots.Tests.mockBot;
import static io.github.pureza.warbots.Matchers.closeTo;

public class LaserRayTest {

    @Test
    public void laserRayVelocityPointsCorrectly() {
        Bot bot = mockBot();

        LaserRay laserRay = buildLaserRay(bot.getGame(), Point.pt(0, 0), Math.PI);
        assertThat(laserRay.getVelocity().normalize(), is(Matchers.closeTo(Vector.vec(-1, 0))));
    }


    @Test
    public void laserRaySpeedIsCorrect() {
        Bot bot = mockBot();

        LaserRay laserRay = buildLaserRay(bot.getGame(), Point.pt(0, 0), Math.PI);
        assertThat(laserRay.getVelocity().norm(), is(10.0));
    }
}
