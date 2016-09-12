package io.github.pureza.warbots.geometry;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import static java.util.Arrays.asList;


/**
 * A circle
 */
public class Circle {

    /** The center of the circle */
    private final Point center;

    /** The radius */
    private final double radius;


    /**
     * Creates a new circle
     */
    public static Circle circle(Point center, double radius) {
        return new Circle(center, radius);
    }


    public Circle(Point center, double radius) {
        this.center = center;
        this.radius = radius;
    }


    /**
     * Finds the center of a circle travelling along a line when it collides
     * with another vertical line
     *
     * In this scenario, the circle is initially far away from the line but
     * starts moving towards it along a constant direction. This method
     * calculates the center of the circle when it is touching the line.
     * This is useful for collision avoidance: if a bot penetrates a wall,
     * we may want to move it back to the point where it is barely touching
     * the wall.
     */
    public Point centerWhenTouchingVertLine(Vector direction, double x0) {

        /*
         * Let r be the line being followed by the circle, defined by
         *
         *   r(t) = c + t·d,
         *
         * where c is the initial center and d is the direction followed by
         * the circle.
         *
         * We want to find t₁ such that r(t₁) = p, the center of the circle
         * that is touching the line x=x₀.
         *
         * We know that p₁ = x₀ ± r, depending on whether the circle is
         * initially at the right (+) or left (-) of x=x₀.
         *
         * Thus,
         *
         *   | p₁ = c₁ + t·d₁ = x₀ ± r
         *   | p₂ = c₂ + t·d₂
         *
         * ⇔ | t = (x₀ ± r - c₁) ÷ d₁ [assuming d₁ ≠ 0]
         *   | p₂ = c₂ + ((x₀ ± r - c₁) ÷ d₁)·d₂
         *
         * Furthermore, the point of intersection is just (x₀, p₂).
         *
         * If d₁ = 0, the direction is vertical and the circle will not
         * intersect the line
         */

        // If the direction vector is also vertical, it will never touch the
        // line at a single point
        if (direction.x() == 0) {
            return null;
        }

        // Positive sign if the circle starts to the right of the line
        int sign = center.x() > x0 ? 1 : -1;

        double p1 = x0 + radius * sign;
        double t = (p1 - center.x()) / direction.x();
        double p2 = center.y() + t * direction.y();

        return Point.pt(p1, p2);
    }


    /**
     * Finds the center of a circle travelling along a line when it collides
     * with another horizontal line
     *
     * In this scenario, the circle is initially far away from the line but
     * starts moving towards it along a constant direction. This method
     * calculates the center of the circle when it is touching the line.
     * This is useful for collision avoidance: if a bot penetrates a wall,
     * we may want to move it back to the point where it is barely touching
     * the wall.
     */
    public Point centerWhenTouchingHorizLine(Vector direction, double y0) {

        /*
         * Let r be the line being followed by the circle, defined by
         *
         *   r(t) = c + t·d,
         *
         * where c is the initial center and d is the direction followed by
         * the circle.
         *
         * We want to find t₁ such that r(t₁) = p, the center of the circle
         * that is touching the line y=y₀.
         *
         * We know that p₂ = y₀ ± r, depending on whether the circle is
         * initially above (+) or under (-) of y=y₀.
         *
         * Thus,
         *
         *   | p₁ = c₁ + t·d₁
         *   | p₂ = c₂ + t·d₂ = y₀ ± r
         *
         * ⇔ | p₁ = c₁ + ((y₀ ± r - c₂) ÷ d₂)·d₁
         *   | t = (y₀ ± r - c₂) ÷ d₂ [assuming d₂ ≠ 0]
         *
         * Furthermore, the point of intersection is just (p₁, y₀).
         *
         * If d₂ = 0, the direction is horizontal and the circle will not
         * intersect the line
         */

        // If the direction vector is also horizontal, it will never touch the
        // line at a single point
        if (direction.y() == 0) {
            return null;
        }

        // Positive sign if the circle starts above the line
        int sign = center.y() > y0 ? 1 : -1;

        double p2 = y0 + radius * sign;
        double t = (p2 - center.y()) / direction.y();
        double p1 = center.x() + t * direction.x();

        return Point.pt(p1, p2);
    }


    /**
     * Find out at which point of the path does the circle centered at that
     * point touches another point
     */
    public Point centerWhenTouchingPoint(Vector direction, Point point) {
        // The path traveled by the circle
        Line path = new Line(center, direction);

        // Imagine a circle centered at the point. Where does it intersect
        // the path?
        Set<Point> intersectionPoints = path.intersectionWithCircle(circle(point, radius));

        if (intersectionPoints.size() == 0) {
            // It doesn't intersect the path
            return null;
        } else if (intersectionPoints.size() == 1) {
            // It intersects the path at one point
            return intersectionPoints.iterator().next();
        } else {
            // Two points of intersection: choose the one that will be reached
            // first
            Iterator<Point> it = intersectionPoints.iterator();
            Point a = it.next();
            Point b = it.next();
            return path.source().distanceTo(a) < path.source().distanceTo(b) ? a : b;
        }
    }


    /**
     * Finds out the center of a circle that moves along a constant path, when
     * the circle touches a line segment
     *
     * The line segment must either be horizontal or vertical.
     *
     * This is useful for collision handling. For example, a bot centered at
     * (2.5, 2.5) with bounding radius 0.1 that is moving along the x axis will
     * touch the cell (3, 2) at point (3, 2.5) and, at that moment, will be
     * bot itself will be located at (2.9, 2.5).
     *
     * Some care must be taken to handle the cases where the circle penetrates
     * the cell from the corner. For example, the same bot centered at
     * (2.5, 1.9) will not touch the cell (3, 2) at (3, 1.9), because this
     * point is under the cell. On the contrary, the bot will move a bit more
     * before hitting the corner (3, 2).
     *
     * If the circle doesn't touch the line segment, returns null.
     */
    public Point centerWhenTouchingLineSegment(Vector direction, Point a, Point b) {
        if (a.y() == b.y()) {
            // Horizontal line

            // Left and right endpoints
            Point left, right;
            if (a.x() < b.x()) {
                left = a;
                right = b;
            } else {
                left = b;
                right = a;
            }

            // Find the center of the circle when it touches the infinite line
            Point touchingCenter = this.centerWhenTouchingHorizLine(direction, a.y());

            // Does the touching point belong to the line segment?
            if (touchingCenter.x() < left.x()) {
                // The touching point is to the left of the segment... Check
                // the left endpoint
                return centerWhenTouchingPoint(direction, left);
            } else if (touchingCenter.x() > right.x()) {
                // The touching point is to the right of the segment... Check
                // the right endpoint
                return centerWhenTouchingPoint(direction, right);
            } else {
                // Yes, the touching point touches the line segment
                return touchingCenter;
            }
        } else if (a.x() == b.x()) {
            // Vertical line

            // Top and bottom endpoints
            Point top, bottom;
            if (a.y() < b.y()) {
                bottom = a;
                top = b;
            } else {
                bottom = b;
                top = a;
            }

            // Find the center of the circle when it touches the infinite line
            Point touchingCenter = this.centerWhenTouchingVertLine(direction, a.x());

            // Does the touching point belong to the line segment?
            if (touchingCenter.y() < bottom.y()) {
                // The touching point is under the segment... Check
                // the bottom endpoint
                return centerWhenTouchingPoint(direction, bottom);
            } else if (touchingCenter.y() > top.y()) {
                // The touching point is above the segment... Check
                // the top endpoint
                return centerWhenTouchingPoint(direction, top);
            } else {
                // Yes, the touching point touches the line segment
                return touchingCenter;
            }
        } else {
            throw new IllegalArgumentException("Line segment must be horizontal or vertical");
        }
    }


    /**
     * Finds out the center of a circle that moves along a constant path when
     * it hits a cell
     *
     * The circle may hit the cell at any of its four borders or even its four
     * corners. We have to check all these cases.
     */
    public Point centerWhenTouchingCell(Vector direction, Point cell) {
        // The four borders we have to check
        Line top = new Line(cell.displace(0, 1), Direction.RIGHT.vector());
        Line bottom = new Line(cell.displace(1, 0), Direction.LEFT.vector());
        Line right = new Line(cell.displace(1, 1), Direction.BOTTOM.vector());
        Line left = new Line(cell, Direction.TOP.vector());

        // Check each border (and their corners) and get all the candidate
        // touching centers
        return asList(top, bottom, right, left).stream()
                .filter(border -> {
                    // Are we going to hit this border from inside the cell or from the
                    // outside? We don't care we are hitting from the inside!
                    // (for example, if the direction is (0, 1) we don't want to check the
                    // top border!)
                    return direction.cross(border.direction()) > 0;
                })
                .map(border -> {
                    // The border endpoints
                    Point a = border.source();
                    Point b = border.source().plus(border.direction());

                    // Get the candidate point for each border
                    return centerWhenTouchingLineSegment(direction, a, b);
                })
                .filter(Objects::nonNull)
                .map(pt -> new PointDistance(pt, center.distanceTo(pt)))
                .min((pairA, pairB) -> {
                    // If there are multiple candidates, select the one that will
                    // be reached first (i.e., the one closest to the circle's
                    // initial position)
                    return Double.compare(pairA.distance(), pairB.distance());
                })
                .map(PointDistance::point)
                .orElse(null);
    }


    public Point center() {
        return center;
    }


    public double radius() {
        return radius;
    }
}



/**
 * Helper class that keeps a point and its distance to another point
 */
class PointDistance {

    /** The point */
    private final Point point;

    /** The distance */
    private final double distance;


    public PointDistance(Point point, double distance) {
        this.point = point;
        this.distance = distance;
    }


    public Point point() {
        return point;
    }

    public double distance() {
        return distance;
    }
}