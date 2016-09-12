package io.github.pureza.warbots.steering;

import org.junit.Test;
import io.github.pureza.warbots.entities.Bot;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static io.github.pureza.warbots.Tests.mockBot;
import static io.github.pureza.warbots.geometry.Point.pt;
import static io.github.pureza.warbots.geometry.Vector.vec;

public class PursuitBehaviorTest {

    @Test
    public void pursuitBehaviorCalculatesVelocityToMoveBotTowardsTargetsFuturePosition() {
        Bot bot = mockBot();

        Bot target = mockBot();
        when(target.getLocation()).thenReturn(pt(10, 0));
        when(target.getVelocity()).thenReturn(vec(0, -3));

        PursuitBehavior pursuit = new PursuitBehavior(bot, target);

        // The predicted position of the target will be at (10, -6)
        assertThat(pursuit.calculateVelocity(1000), is(vec(10, -6).normalize().scalarMul(bot.getMaxSpeed())));
    }
}
