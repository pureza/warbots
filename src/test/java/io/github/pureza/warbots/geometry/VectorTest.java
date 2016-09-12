package io.github.pureza.warbots.geometry;

import org.junit.Test;

import static java.lang.Math.PI;
import static java.lang.Math.sqrt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static io.github.pureza.warbots.Matchers.closeTo;
import static io.github.pureza.warbots.geometry.Point.pt;
import static io.github.pureza.warbots.geometry.Vector.vec;

public class VectorTest {

    @Test
    public void plusAddsTwoVectors() {
        assertThat(vec(1, 1).plus(vec(2, 2)), is(vec(3, 3)));
    }


    @Test
    public void minusSubtractsTwoVectors() {
        assertThat(vec(1, 1).minus(vec(0, 2)), is(vec(1, -1)));
    }


    @Test
    public void scalarMulMultipliesVectorByScalar() {
        assertThat(vec(1, 1).scalarMul(2), is(vec(2, 2)));
    }


    @Test
    public void scalarDivMultipliesVectorByScalar() {
        assertThat(vec(2, 2).scalarDiv(2), is(vec(1, 1)));
    }


    @Test
    public void dotCalculatesTheDotProduct() {
        assertThat(vec(1, 1).dot(vec(2, 2)), is(4.0));
    }


    @Test
    public void crossCalculatesTheCrossProduct() {
        assertThat(vec(1, 1).cross(vec(2, 2)), is(0.0));
        assertThat(vec(1, 1).cross(vec(2, 0)), is(-2.0));
    }


    @Test
    public void normCalculatesTheVectorNorm() {
        assertThat(vec(1, 1).norm(), is(sqrt(2)));
    }


    @Test
    public void normalizeNormalizesVector() {
        assertThat(vec(1, 1).normalize().toPoint(), is(closeTo(pt(1 / sqrt(2), 1 / sqrt(2)))));
        assertThat(vec(0.5, 0.5).normalize().toPoint(), is(closeTo(pt(0.5 / sqrt(0.5), 0.5 / sqrt(0.5)))));
        assertThat(vec(0.5, sqrt(3) / 2).normalize().toPoint(), is(closeTo(pt(0.5, sqrt(3) / 2))));
    }


    @Test
    public void angleWithFindsAngleBetweenParallelVectors() {
        assertThat(vec(1, 1).angleWith(vec(2, 2)), is(closeTo(0.0)));
    }


    @Test
    public void angleWithFindsAngleBetweenOppositeVectors() {
        assertThat(vec(1, 1).angleWith(vec(-2, -2)), is(closeTo(PI)));
    }


    @Test
    public void angleWithFindsAngleBetweenPerpendicularVectors() {
        assertThat(vec(1, 1).angleWith(vec(-1, 1)), is(closeTo(PI / 2)));
    }


    @Test
    public void angleWithFindsAngleBetweenCloseVectors() {
        // 30 degrees
        assertThat(vec(sqrt(3) / 2, 0.5).angleWith(vec(0.5, sqrt(3) / 2)), is(closeTo(30 / 180.0 * PI)));
    }


    @Test
    public void reverseReversesVector() {
        assertThat(vec(1, 1).reverse(), is(vec(-1, -1)));
    }


    @Test
    public void perpReturnsPerpendicularVector() {
        assertThat(vec(1, 2).perp().dot(vec(1, 2)), is(closeTo(0.0)));
    }


    @Test
    public void truncateDoesntChangeSmallVector() {
        assertThat(vec(1, 1).truncate(100), is(vec(1, 1)));
    }


    @Test
    public void truncateTruncatesBigVector() {
        assertThat(vec(1, 1).truncate(1).toPoint(), is(closeTo(pt(sqrt(2) / 2, sqrt(2) / 2))));
    }


    @Test
    public void toPointReturnsPointWithVectorComponentsAsCoordinates() {
        assertThat(vec(1, 2).toPoint(), is(pt(1, 2)));
    }


    /*
     * boolean isNull()
     */

    @Test
    public void isNullReturnsTrueForNullVector() {
        assertThat(vec(0, 0).isNull(), is(true));
    }


    @Test
    public void isNullReturnsFalseForHorizontalVector() {
        assertThat(vec(1, 0).isNull(), is(false));
    }


    @Test
    public void isNullReturnsFalseForVerticalVector() {
        assertThat(vec(0, 1).isNull(), is(false));
    }
}
