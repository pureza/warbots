package io.github.pureza.warbots.geometry;

import io.github.pureza.warbots.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;

import static java.lang.Math.PI;
import static java.lang.Math.sqrt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static io.github.pureza.warbots.Matchers.closeTo;

public class PointTest {

    @Test
    public void midpointFindsMiddlePoint() {
        assertThat(Point.midpoint(Point.pt(1, 1), Point.pt(-1, -1)), Is.is(Point.pt(0, 0)));
    }


    @Test
    public void displaceMovesPoint() {
        assertThat(Point.pt(1, 1).displace(1, 1), Is.is(Point.pt(2, 2)));
    }


    @Test
    public void plusAddsTwoPoints() {
        assertThat(Point.pt(1, 1).plus(Point.pt(1, 1)), Is.is(Point.pt(2, 2)));
    }


    @Test
    public void plusAddsPointWithVector() {
        assertThat(Point.pt(1, 1).plus(Vector.vec(1, 1)), Is.is(Point.pt(2, 2)));
    }


    @Test
    public void minusSubstractsTwoPoints() {
        assertThat(Point.pt(1, 1).minus(Point.pt(3, 3)), Is.is(Vector.vec(-2, -2)));
    }


    @Test
    public void distanceFromPointToItselfIsZero() {
        assertThat(Point.pt(0, 0).distanceTo(Point.pt(0, 0)), is(Matchers.closeTo(0.0)));
    }


    @Test
    public void distanceBetweenTwoPointsIsCorrect() {
        assertThat(Point.pt(0, 0).distanceTo(Point.pt(1, 1)), is(sqrt(2)));
        assertThat(Point.pt(1, 1).distanceTo(Point.pt(0, 0)), is(sqrt(2)));
    }


    @Test
    public void sqDistanceFromPointToItselfIsZero() {
        assertThat(Point.pt(0, 0).sqDistanceTo(Point.pt(0, 0)), is(Matchers.closeTo(0.0)));
    }


    @Test
    public void sqDistanceBetweenTwoPointsIsCorrect() {
        assertThat(Point.pt(0, 0).sqDistanceTo(Point.pt(1, 1)), is(2.0));
        assertThat(Point.pt(1, 1).sqDistanceTo(Point.pt(0, 0)), is(2.0));
    }


    @Test
    public void rotateRotatesPointCounterClockwise() {
        assertThat(Point.pt(1, 1).rotate(PI / 4), is(Matchers.closeTo(Point.pt(0, sqrt(2)))));
    }
}
