package io.github.pureza.warbots.steering;

import io.github.pureza.warbots.Tests;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Vector;
import org.hamcrest.Matchers;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static io.github.pureza.warbots.Tests.mockBot;
import static io.github.pureza.warbots.geometry.Point.pt;

public class SeekBehaviorTest {

    @Test
    public void seekMovesTowardsPointUsingTheMaximumSpeed() {
        Bot bot = Tests.mockBot();
        SeekBehavior seek = new SeekBehavior(bot, pt(20, 0));

        assertThat(seek.calculateVelocity(1000), Matchers.is(Vector.vec(2, 0)));
    }


    @Test
    public void seekJumpsStraightToTargetIfPossible() {
        Bot bot = Tests.mockBot();
        SeekBehavior seek = new SeekBehavior(bot, pt(1.3, 0));

        assertThat(seek.calculateVelocity(1000), Matchers.is(Vector.vec(1.3, 0)));
    }
}
