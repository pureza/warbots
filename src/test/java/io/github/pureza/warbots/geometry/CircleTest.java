package io.github.pureza.warbots.geometry;

import org.junit.Test;

import static java.lang.Math.sqrt;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static io.github.pureza.warbots.Matchers.closeTo;
import static io.github.pureza.warbots.geometry.Circle.circle;
import static io.github.pureza.warbots.geometry.Point.pt;

public class CircleTest {


    /*
     * Point centerWhenTouchingVertLine(Vector direction, double x0)
     */

    @Test
    public void centerWhenTouchingVertLineFindsCenterOnLeftCircleAndRightDirection() {
        Circle circ = circle(pt(1, 0), 1);
        assertThat(circ.centerWhenTouchingVertLine(Direction.RIGHT.vector(), 5), is(closeTo(pt(4, 0))));
    }


    @Test
    public void centerWhenTouchingVertLineFindsCenterOnRightCircleAndRightDirection() {
        Circle circ = circle(pt(8, 0), 1);
        assertThat(circ.centerWhenTouchingVertLine(Direction.RIGHT.vector(), 5), is(closeTo(pt(6, 0))));
    }


    @Test
    public void centerWhenTouchingVertLineFindsCenterOnLeftCircleAndLeftDirection() {
        Circle circ = circle(pt(1, 0), 1);
        assertThat(circ.centerWhenTouchingVertLine(Direction.LEFT.vector(), 5), is(closeTo(pt(4, 0))));
    }


    @Test
    public void centerWhenTouchingVertLineFindsCenterOnRightCircleAndLeftDirection() {
        Circle circ = circle(pt(8, 0), 1);
        assertThat(circ.centerWhenTouchingVertLine(Direction.LEFT.vector(), 5), is(closeTo(pt(6, 0))));
    }


    @Test
    public void centerWhenTouchingVertLineFailsForVerticalLine() {
        Circle circ = circle(pt(8, 0), 1);
        assertThat(circ.centerWhenTouchingVertLine(Direction.TOP.vector(), 5), is(nullValue()));
    }


    @Test
    public void centerWhenTouchingVertLineFindsCenterOnLeftBottomCircleAndDiagonalDirection() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingVertLine(Direction.TOP_RIGHT.vector(), 5), is(closeTo(pt(4, 4))));
    }


    @Test
    public void centerWhenTouchingVertLineFindsCenterOnRightTopCircleAndDiagonalDirection() {
        Circle circ = circle(pt(10, 10), 1);
        assertThat(circ.centerWhenTouchingVertLine(Direction.TOP_RIGHT.vector(), 5), is(closeTo(pt(6, 6))));
    }


    @Test
    public void centerWhenTouchingVertLineFindsCenterOnLeftTopCircleAndDiagonalDirection() {
        Circle circ = circle(pt(0, 10), 1);
        assertThat(circ.centerWhenTouchingVertLine(Direction.BOTTOM_RIGHT.vector(), 5), is(closeTo(pt(4, 6))));
    }


    @Test
    public void centerWhenTouchingVertLineFindsCenterOnRightBottomCircleAndDiagonalDirection() {
        Circle circ = circle(pt(10, 0), 1);
        assertThat(circ.centerWhenTouchingVertLine(Direction.TOP_LEFT.vector(), 5), is(closeTo(pt(6, 4))));
    }


    /*
     * Point centerWhenTouchingHorizLine(Vector direction, double y0)
     */


    @Test
    public void centerWhenTouchingHorizLineFindsCenterOnTopCircleAndTopDirection() {
        Circle circ = circle(pt(10, 8), 1);
        assertThat(circ.centerWhenTouchingHorizLine(Direction.TOP.vector(), 5), is(closeTo(pt(10, 6))));
    }


    @Test
    public void centerWhenTouchingHorizLineFindsCenterOnBottomCircleAndTopDirection() {
        Circle circ = circle(pt(10, 1), 1);
        assertThat(circ.centerWhenTouchingHorizLine(Direction.TOP.vector(), 5), is(closeTo(pt(10, 4))));
    }


    @Test
    public void centerWhenTouchingHorizLineFindsCenterOnBottomCircleAndBottomDirection() {
        Circle circ = circle(pt(10, 1), 1);
        assertThat(circ.centerWhenTouchingHorizLine(Direction.BOTTOM.vector(), 5), is(closeTo(pt(10, 4))));
    }


    @Test
    public void centerWhenTouchingHorizLineFindsCenterOnTopCircleAndBottomDirection() {
        Circle circ = circle(pt(10, 8), 1);
        assertThat(circ.centerWhenTouchingHorizLine(Direction.BOTTOM.vector(), 5), is(closeTo(pt(10, 6))));
    }


    @Test
    public void centerWhenTouchingHorizLineFailsForHorizontalLine() {
        Circle circ = circle(pt(8, 0), 1);
        assertThat(circ.centerWhenTouchingHorizLine(Direction.RIGHT.vector(), 5), is(nullValue()));
    }


    @Test
    public void centerWhenTouchingHorizLineFindsCenterOnLeftBottomCircleAndDiagonalDirection() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingHorizLine(Direction.TOP_RIGHT.vector(), 5), is(closeTo(pt(4, 4))));
    }


    @Test
    public void centerWhenTouchingHorizLineFindsCenterOnRightTopCircleAndDiagonalDirection() {
        Circle circ = circle(pt(10, 10), 1);
        assertThat(circ.centerWhenTouchingHorizLine(Direction.TOP_RIGHT.vector(), 5), is(closeTo(pt(6, 6))));
    }


    @Test
    public void centerWhenTouchingHorizLineFindsCenterOnLeftTopCircleAndDiagonalDirection() {
        Circle circ = circle(pt(0, 10), 1);
        assertThat(circ.centerWhenTouchingHorizLine(Direction.BOTTOM_RIGHT.vector(), 5), is(closeTo(pt(4, 6))));
    }


    @Test
    public void centerWhenTouchingHorizLineFindsCenterOnRightBottomCircleAndDiagonalDirection() {
        Circle circ = circle(pt(10, 0), 1);
        assertThat(circ.centerWhenTouchingHorizLine(Direction.BOTTOM_LEFT.vector(), 5), is(closeTo(pt(14, 4))));
    }


    /*
     * Point centerWhenTouchingPoint(Vector direction, Point point)
     */

    @Test
    public void centerWhenTouchingPointFindsCenterWhenCirclePenetratesPointFromBelow() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingPoint(Direction.TOP.vector(), pt(0, 2)), is(closeTo(pt(0, 1))));
    }


    @Test
    public void centerWhenTouchingPointFindsCenterWhenCirclePenetratesPointFromRight() {
        Circle circ = circle(pt(5, 2), 1);
        assertThat(circ.centerWhenTouchingPoint(Direction.LEFT.vector(), pt(0, 2)), is(closeTo(pt(1, 2))));
    }


    @Test
    public void centerWhenTouchingPointFindsCenterWhenCircleIsTangentToPoint() {
        Circle circ = circle(pt(5, 3), 1);
        assertThat(circ.centerWhenTouchingPoint(Direction.LEFT.vector(), pt(0, 2)), is(closeTo(pt(0, 3))));
    }


    @Test
    public void centerWhenTouchingPointFindsCenterWhenCircleIsTangentToPointAndCircleMovesDiagonally() {
        Circle circ = circle(pt(sqrt(2) - 0.00000001, 0), 1);
        assertThat(circ.centerWhenTouchingPoint(Direction.TOP_LEFT.vector(), pt(0, 0)), is(closeTo(pt(sqrt(2)/2, sqrt(2)/2))));
    }


    @Test
    public void centerWhenTouchingPointDoesntFindCenterWhenCirclePassesFarAwayFromPoint() {
        Circle circ = circle(pt(5, 4), 1);
        assertThat(circ.centerWhenTouchingPoint(Direction.LEFT.vector(), pt(0, 2)), is(nullValue()));
    }


    @Test
    public void centerWhenTouchingPointFindsCenterWhenCircleMovesDiagonally() {
        Circle circ = circle(pt(1, 1), 1);
        assertThat(circ.centerWhenTouchingPoint(Direction.BOTTOM_LEFT.vector(), pt(0, 0)), is(closeTo(pt(sqrt(2)/2, sqrt(2)/2))));
    }


    /*
     * Point centerWhenTouchingLineSegment(Vector direction, Point a, Point b)
     */

    @Test
    public void centerWhenTouchingLineSegmentFindsCenterWhenTouchingHorizontalLine() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingLineSegment(Direction.TOP_RIGHT.vector(), pt(0, 5), pt(10, 5)), is(pt(4, 4)));
    }


    @Test
    public void centerWhenTouchingLineSegmentFindsCenterWhenTouchingHorizontalLineEndpointsSwapped() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingLineSegment(Direction.TOP_RIGHT.vector(), pt(10, 5), pt(0, 5)), is(pt(4, 4)));
    }


    @Test
    public void centerWhenTouchingLineSegmentFindsCenterWhenTouchingHorizontalLineEndpointA() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingLineSegment(Direction.TOP_RIGHT.vector(), pt(4, 5), pt(10, 5)), is(pt(4, 4)));
    }


    @Test
    public void centerWhenTouchingLineSegmentFindsCenterWhenTouchingHorizontalLineEndpointB() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingLineSegment(Direction.TOP_RIGHT.vector(), pt(0, 5), pt(4, 5)), is(pt(4, 4)));
    }


    @Test
    public void centerWhenTouchingLineSegmentDoesntFindCenterWhenCircleTooFarIntoTheLeftOfHorizontalLine() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingLineSegment(Direction.TOP_LEFT.vector(), pt(0, 5), pt(1, 5)), is(nullValue()));
    }


    @Test
    public void centerWhenTouchingLineSegmentDoesntFindCenterWhenCircleTooFarIntoTheRightOfHorizontalLine() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingLineSegment(Direction.TOP_RIGHT.vector(), pt(0, 5), pt(1, 5)), is(nullValue()));
    }


    @Test
    public void centerWhenTouchingLineSegmentFindsCenterWhenTouchingVerticalLine() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingLineSegment(Direction.TOP_RIGHT.vector(), pt(5, 0), pt(5, 10)), is(pt(4, 4)));
    }


    @Test
    public void centerWhenTouchingLineSegmentFindsCenterWhenTouchingVerticalLineEndpointsSwapped() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingLineSegment(Direction.TOP_RIGHT.vector(), pt(5, 10), pt(5, 0)), is(pt(4, 4)));
    }


    @Test
    public void centerWhenTouchingLineSegmentFindsCenterWhenTouchingVerticalLineEndpointA() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingLineSegment(Direction.TOP_RIGHT.vector(), pt(5, 4), pt(5, 10)), is(pt(4, 4)));
    }


    @Test
    public void centerWhenTouchingLineSegmentFindsCenterWhenTouchingVerticalLineEndpointB() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingLineSegment(Direction.TOP_RIGHT.vector(), pt(5, 0), pt(5, 4)), is(pt(4, 4)));
    }


    @Test
    public void centerWhenTouchingLineSegmentDoesntFindCenterWhenCircleTooFarIntoTheBottomOfVerticalLine() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingLineSegment(Direction.TOP_LEFT.vector(), pt(5, 0), pt(5, 1)), is(nullValue()));
    }


    @Test
    public void centerWhenTouchingLineSegmentDoesntFindCenterWhenCircleTooFarIntoTheTopOfVerticalLine() {
        Circle circ = circle(pt(0, 0), 1);
        assertThat(circ.centerWhenTouchingLineSegment(Direction.TOP_RIGHT.vector(), pt(5, 0), pt(5, 1)), is(nullValue()));
    }


    @Test(expected=IllegalArgumentException.class)
    public void centerWhenTouchingLineSegmentFailsIfLineSegmentIsNotHorizontalOrVertical() {
        Circle circ = circle(pt(0, 0), 1);
        circ.centerWhenTouchingLineSegment(Direction.TOP_RIGHT.vector(), pt(5, 0), pt(10, 1));
    }


    /*
     * Point centerWhenTouchingCell(Vector direction, Point cell)
     */

//    @Test
//    public void centerWhenTouchingCellFindsBottomLeftCornerWhenJustTouching() {
//        Circle circ = circle(pt(1, 0), sqrt(2)/2);
//        assertThat(circ.centerWhenTouchingCell(vec(-1, 1), pt(1, 1)), is(closeTo(pt(0.5, 0.5))));
//    }
//
//
//    @Test
//    public void centerWhenTouchingCellFindsBottomLeftCornerWhenPenetrating() {
//        Circle circ = circle(pt(0, 0), sqrt(2)/2);
//        assertThat(circ.centerWhenTouchingCell(vec(1, 1), pt(1, 1)), is(closeTo(pt(0.5, 0.5))));
//    }
//
//
//    @Test
//    public void centerWhenTouchingCellFindsBottomBorder() {
//        Circle circ = circle(pt(1.5, 0), 0.5);
//        assertThat(circ.centerWhenTouchingCell(vec(0, 1), pt(1, 1)), is(closeTo(pt(1.5, 0.5))));
//    }
//
//
//    @Test
//    public void centerWhenTouchingCellDoesntFindCenterWhenCirclePassesFarAwayComingFromTheLeft() {
//        Circle circ = circle(pt(0, 0), 0.5);
//        assertThat(circ.centerWhenTouchingCell(vec(1, 0), pt(1, 1)), is(nullValue()));
//    }
//
//
//    @Test
//    public void centerWhenTouchingCellDoesntFindCenterWhenCirclePassesFarAwayComingFromTheTop() {
//        Circle circ = circle(pt(0, 10), 0.5);
//        assertThat(circ.centerWhenTouchingCell(vec(0, -1), pt(1, 1)), is(nullValue()));
//    }
//
//
//    @Test
//    public void centerWhenTouchingCellDoesntFindCenterWhenCirclePassesFarAwayComingFromTheBottom() {
//        Circle circ = circle(pt(0, -10), 0.5);
//        assertThat(circ.centerWhenTouchingCell(vec(0, 1), pt(1, 1)), is(nullValue()));
//    }
//
//
//    @Test
//    public void centerWhenTouchingCellDoesntFindCenterWhenCirclePassesFarAwayComingFromTheRight() {
//        Circle circ = circle(pt(10, 0), 0.5);
//        assertThat(circ.centerWhenTouchingCell(vec(-1, 0), pt(1, 1)), is(nullValue()));
//    }


    @Test
    public void centerWhenTouchingCellFindsBottomRightCornerWhenJustTouching() {
        Circle circ = circle(pt(2, 0), sqrt(2)/2);
        assertThat(circ.centerWhenTouchingCell(Vector.vec(1, 1), pt(1, 1)), is(closeTo(pt(2.5, 0.5))));
    }


    @Test
    public void centerWhenTouchingCellFindsBottomRightCornerWhenPenetrating() {
        Circle circ = circle(pt(3, 0), sqrt(2)/2);
        assertThat(circ.centerWhenTouchingCell(Vector.vec(-1, 1), pt(1, 1)), is(closeTo(pt(2.5, 0.5))));
    }


    @Test
    public void centerWhenTouchingCellFindsRightBorder() {
        Circle circ = circle(pt(3.5, 1.5), 0.5);
        assertThat(circ.centerWhenTouchingCell(Vector.vec(-1, 0), pt(1, 1)), is(closeTo(pt(2.5, 1.5))));
    }


    @Test
    public void centerWhenTouchingCellFindsTopRightCornerWhenJustTouching() {
        Circle circ = circle(pt(5, 0), sqrt(2)/2);
        assertThat(circ.centerWhenTouchingCell(Vector.vec(-1, 1), pt(1, 1)), is(closeTo(pt(2.5, 2.5))));
    }


    @Test
    public void centerWhenTouchingCellFindsTopRightCornerWhenPenetrating() {
        Circle circ = circle(pt(3, 3), sqrt(2)/2);
        assertThat(circ.centerWhenTouchingCell(Vector.vec(-1, -1), pt(1, 1)), is(closeTo(pt(2.5, 2.5))));
    }


    @Test
    public void centerWhenTouchingCellFindsTopBorder() {
        Circle circ = circle(pt(1.5, 4.5), 0.5);
        assertThat(circ.centerWhenTouchingCell(Vector.vec(0, -1), pt(1, 1)), is(closeTo(pt(1.5, 2.5))));
    }


    @Test
    public void centerWhenTouchingCellFindsTopLeftCornerWhenJustTouching() {
        Circle circ = circle(pt(1, 3), sqrt(2)/2);
        assertThat(circ.centerWhenTouchingCell(Vector.vec(-1, -1), pt(1, 1)), is(closeTo(pt(0.5, 2.5))));
    }


    @Test
    public void centerWhenTouchingCellFindsTopLeftCornerWhenPenetrating() {
        Circle circ = circle(pt(0, 3), sqrt(2)/2);
        assertThat(circ.centerWhenTouchingCell(Vector.vec(1, -1), pt(1, 1)), is(closeTo(pt(0.5, 2.5))));
    }


    @Test
    public void centerWhenTouchingCellFindsLeftBorder() {
        Circle circ = circle(pt(0, 1.5), 0.5);
        assertThat(circ.centerWhenTouchingCell(Vector.vec(1, 0), pt(1, 1)), is(closeTo(pt(0.5, 1.5))));
    }


    @Test
    public void centerWhenTouchingCellReturnsNullWhenPassingFarFromCell() {
        Circle circ = circle(pt(10, 20), 0.5);
        assertThat(circ.centerWhenTouchingCell(Vector.vec(1, 1), pt(1, 1)), is(nullValue()));
    }


    @Test
    public void centerWhenTouchingCellSelectsFirstCollisionPoint() {
        // This circle will penetrate the right wall first and, after a bit,
        // will also touch the top right corner.
        // The centerWhenTouchingCell() finds both collision points but
        // correctly chooses the first one to be hit
        Circle circ = circle(pt(5.30400636949258, 0.966913964271631), 0.3);
        assertThat(circ.centerWhenTouchingCell(Vector.vec(-0.006748505038239649,-0.021464102118394335), pt(4, 0)), is(closeTo(pt(5.3, 0.9541714200450968))));
    }
}
