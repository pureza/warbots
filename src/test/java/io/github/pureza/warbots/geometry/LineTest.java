package io.github.pureza.warbots.geometry;


import io.github.pureza.warbots.Matchers;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.Iterator;
import java.util.Set;

import static java.lang.Math.sqrt;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static io.github.pureza.warbots.geometry.Circle.circle;
import static io.github.pureza.warbots.Matchers.closeTo;
import static io.github.pureza.warbots.geometry.Point.pt;
import static io.github.pureza.warbots.geometry.Vector.vec;

public class LineTest {


    @Test(expected = IllegalArgumentException.class)
    public void directionMustNotBeZero() {
        new Line(pt(0, 0), vec(0, 0));
    }


    /*
     * intersectionWithLine(Line other)
     */

    @Test
    public void lineDoesntIntersectWithItself() {
        Line line = new Line(pt(0, 0), vec(1, 1));
        assertThat(line.intersectionWithLine(line), is(nullValue()));
    }


    @Test
    public void parallelLinesDontIntersect() {
        Line a = new Line(pt(0, 0), vec(1, 1));
        Line b = new Line(pt(0, 1), vec(1, 1));

        assertThat(a.intersectionWithLine(b), is(nullValue()));
        assertThat(b.intersectionWithLine(a), is(nullValue()));
    }


    @Test
    public void parallelLinesWithOppositeVectorsDontIntersect() {
        Line a = new Line(pt(0, 0), vec(1, 1));
        Line b = new Line(pt(0, 1), vec(-1, -1));

        assertThat(a.intersectionWithLine(b), is(nullValue()));
        assertThat(b.intersectionWithLine(a), is(nullValue()));
    }


    @Test
    public void verticalLinesDontIntersect() {
        Line a = new Line(pt(0, 0), vec(1, 0));
        Line b = new Line(pt(0, 1), vec(-1, 0));

        assertThat(a.intersectionWithLine(b), is(nullValue()));
        assertThat(b.intersectionWithLine(a), is(nullValue()));
    }


    @Test
    public void verticalLineIntersectsHorizontalLine() {
        Line a = new Line(pt(0, 0), vec(1, 0));
        Line b = new Line(pt(1, 1), vec(0, 1));

        assertThat(a.intersectionWithLine(b), is(pt(1, 0)));
        assertThat(b.intersectionWithLine(a), is(pt(1, 0)));
    }


    @Test
    public void twoNonParallelAndNonVerticalLinesIntersect() {
        Line a = new Line(pt(0, 0), vec(1, 1));
        Line b = new Line(pt(1, -1), vec(1, -1));

        assertThat(a.intersectionWithLine(b), is(pt(0, 0)));
        assertThat(b.intersectionWithLine(a), is(pt(0, 0)));
    }


    /*
     * intersectionWithCircle(Point center, double radius)
     */

    @Test(expected = IllegalArgumentException.class)
    public void failsIfRadiusIsZero() {
        Line a = new Line(pt(0, 0), vec(0, 1));
        a.intersectionWithCircle(circle(pt(0, 0), 0));
    }


    @Test(expected = IllegalArgumentException.class)
    public void failsIfRadiusIsNegative() {
        Line a = new Line(pt(0, 0), vec(0, 1));
        a.intersectionWithCircle(circle(pt(0, 0), -1));
    }


    @Test
    public void verticalDiameterIntersectsCircleInOppositeSides() {
        Line a = new Line(pt(0, 0), vec(0, 1));
        assertThat(a.intersectionWithCircle(circle(pt(0, 0), 1)), containsInAnyOrder(pt(0, 1), pt(0, -1)));
    }


    @Test
    public void verticalChordIntersectsCircleInTwoPoints() {
        Line a = new Line(pt(0.5, 0), vec(0, 1));
        assertThat(a.intersectionWithCircle(circle(pt(0, 0), 1)), containsInAnyOrder(pt(0.5, sqrt(3 / 4.0)), pt(0.5, -sqrt(3 / 4.0))));
    }


    @Test
    public void verticalLineIntersectsCircleInOnePoint() {
        Line a = new Line(pt(1, 0), vec(0, 1));

        Set<Point> points = a.intersectionWithCircle(circle(pt(0, 0), 1));
        assertThat(points.size(), is(1));

        Point p = points.iterator().next();
        assertThat(p, is(closeTo(pt(1, 0))));
    }


    @Test
    public void verticalLineDoesNotIntersectCircle() {
        Line a = new Line(pt(100, 0), vec(0, 1));
        assertThat(a.intersectionWithCircle(circle(pt(0, 0), 1)), is(empty()));
    }


    @Test
    public void horizontalDiameterIntersectsCircleInOppositeSides() {
        Line a = new Line(pt(0, 0), vec(1, 0));
        assertThat(a.intersectionWithCircle(circle(pt(0, 0), 1)), containsInAnyOrder(pt(1, 0), pt(-1, 0)));
    }


    @Test
    public void horizontalChordIntersectsCircleInTwoPoints() {
        Line a = new Line(pt(0, 0.5), vec(1, 0));
        assertThat(a.intersectionWithCircle(circle(pt(0, 0), 1)), containsInAnyOrder(pt(sqrt(3 / 4.0), 0.5), pt(-sqrt(3 / 4.0), 0.5)));
    }


    @Test
    public void horizontalLineIntersectsCircleInOnePoint() {
        Line a = new Line(pt(0, 1), vec(1, 0));
        assertThat(a.intersectionWithCircle(circle(pt(0, 0), 1)), contains(closeTo(pt(0, 1))));
    }


    @Test
    public void horizontalLineDoesNotIntersectCircle() {
        Line a = new Line(pt(0, 100), vec(1, 0));
        assertThat(a.intersectionWithCircle(circle(pt(0, 0), 1)), is(empty()));
    }


    @Test
    public void lineIntersectsCircleOnTwoHemispheres() {
        Line a = new Line(pt(0, 0), vec(1, 1));

        Matcher<Point> closeToFirst = closeTo(pt(sqrt(2) / 2, sqrt(2) / 2));
        Matcher<Point> closeToSecond = closeTo(pt(-sqrt(2) / 2, -sqrt(2) / 2));

        Set<Point> points = a.intersectionWithCircle(circle(pt(0, 0), 1));
        assertThat(points.size(), is(2));

        Iterator<Point> it = points.iterator();
        Point p = it.next();
        Point q = it.next();
        assertThat(p, anyOf(closeToFirst, closeToSecond));
        assertThat(p, anyOf(closeToFirst, closeToSecond));
    }


    @Test
    public void lineIntersectsCircleTwiceInTheUpperHemisphere() {
        Line a = new Line(pt(0, 2), vec(1, 1));

        Matcher<Point> closeToFirst = closeTo(pt(-1 - sqrt(2) / 2, 1 - sqrt(2) / 2));
        Matcher<Point> closeToSecond = closeTo(pt(sqrt(2) / 2 - 1, 1 + sqrt(2) / 2));

        Set<Point> points = a.intersectionWithCircle(circle(pt(0, 0), sqrt(3)));
        assertThat(points.size(), is(2));

        Iterator<Point> it = points.iterator();
        Point p = it.next();
        Point q = it.next();
        assertThat(p, anyOf(closeToFirst, closeToSecond));
        assertThat(p, anyOf(closeToFirst, closeToSecond));
    }


    @Test
    public void lineIntersectsCircleTwiceInTheLowerHemisphere() {
        Line a = new Line(pt(0, -2), vec(1, 1));

        Matcher<Point> closeToFirst = closeTo(pt(1 - sqrt(2) / 2, -1 - sqrt(2) / 2));
        Matcher<Point> closeToSecond = closeTo(pt(1 + sqrt(2) / 2, sqrt(2) / 2 - 1));

        Set<Point> points = a.intersectionWithCircle(circle(pt(0, 0), sqrt(3)));
        assertThat(points.size(), is(2));

        Iterator<Point> it = points.iterator();
        Point p = it.next();
        Point q = it.next();
        assertThat(p, anyOf(closeToFirst, closeToSecond));
        assertThat(p, anyOf(closeToFirst, closeToSecond));
    }


    @Test
    public void lineDoesntIntersectCircle() {
        Line a = new Line(pt(0, 100), vec(1, 1));
        assertThat(a.intersectionWithCircle(circle(pt(0, 0), 1)), is(empty()));
    }
    
    
    /*
     * orthoProject(Point point)
     */

    @Test
    public void orthoProjectProjectsPointOutsideLine() {
        Line a = new Line(pt(0, 0), vec(1, 1));
        assertThat(a.orthoProject(pt(0, 2)), is(pt(1, 1)));
    }


    @Test
    public void orthoProjectProjectsPointInsideLine() {
        Line a = new Line(pt(0, 0), vec(1, 1));
        assertThat(a.orthoProject(pt(1, 1)), is(pt(1, 1)));
    }

    
    /*
     * quadraticSolve(double a, double b, double c)
     */

    @Test
    public void quadraticEquationHasNoSolutions() {
        // x² + 1 = 0
        assertThat(Line.quadraticSolve(1, 0, 1), is(new double[0]));
    }


    @Test
    public void quadraticEquationHasOneSolution() {
        // x² = 0
        double[] solutions = Line.quadraticSolve(1, 0, 0);
        assertThat(solutions.length, is(1));
        assertThat(solutions[0], is(Matchers.closeTo(0.0)));
    }


    @Test
    public void quadraticEquationHasTwoSolutions() {
        // x² = 1
        double[] solutions = Line.quadraticSolve(1, 0, -1);
        assertThat(solutions.length, is(2));
        assertThat(solutions[0], anyOf(Matchers.closeTo(1.0), Matchers.closeTo(-1.0)));
        assertThat(solutions[1], anyOf(Matchers.closeTo(1.0), Matchers.closeTo(-1.0)));
    }
}
