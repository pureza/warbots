package io.github.pureza.warbots.geometry;

import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.sqrt;


/**
 * A line
 *
 * Lines are characterized by a point and a direction vector.
 */
public class Line {

    /** One point where the line passes through */
    private final Point source;

    /** Line direction */
    private final Vector direction;


    /**
     * Creates a new line from a point where it passes through and a direction
     * vector
     */
    public Line(Point source, Vector direction) {
        if (direction.norm() == 0) {
            throw new IllegalArgumentException("Direction vector must not be zero!");
        }

        this.source = source;
        this.direction = direction;
    }


    /**
     * Calculates the point of intersection between two lines
     *
     * Returns null if the lines are parallel and don't intersect.
     */
    public Point intersectionWithLine(Line other) {
        
        /*
         * Let r and s be two lines defined by
         * 
         *   r(x) = p + v·x
         *   s(y) = q + u·y
         *
         * At the point the lines intersect, we have
         *
         *   r(x) = s(y)
         * ⇔ p + v·x = q + u·y
         * ⇔ | p₁ + v₁·x = q₁ + u₁·y  (1)
         *   | p₂ + v₂·x = q₂ + u₂·y  (2)
         *
         * Now,
         *
         *  - if u₂ = 0 and v₂ = 0, the lines are both horizontal and don't intersect
         *
         *  - if u₂ = 0 and v₂ ≠ 0, only the second line is horizontal and from (1)
         *    we get x = (q₂ - p₂)/v₂
         *
         *  - if v₁ - u₁·v₂/u₂ = 0, then u⨯v = 0 and the lines are parallel
         *
         *  - otherwise, x = (q₁ + u₁·p₂/u₂ - u₁·q₂/u₂ - p₁)/(v₁ - u₁·v₂/u₂)
         *
         * From x, we can easily find the point of intersection
         */

        double p1 = this.source().x();
        double p2 = this.source().y();
        double v1 = this.direction().x();
        double v2 = this.direction().y();

        double q1 = other.source().x();
        double q2 = other.source().y();
        double u1 = other.direction().x();
        double u2 = other.direction().y();

        if (this.direction().cross(other.direction()) == 0) {
            // Lines are parallel and don't intersect
            return null;
        }

        double x;
        if (u2 == 0) {
            // The other line is horizontal but this one isn't
            x = (q2 - p2) / v2;
        } else {
            x = (q1 + (u1 * (p2 - q2)) / u2 - p1) / (v1 - u1 * v2 / u2);
        }

        return this.source.plus(this.direction.scalarMul(x));
    }


    /**
     * Calculates the intersection points between this line and a circle
     *
     * May return 0, 1 or 2 points.
     */
    public Set<Point> intersectionWithCircle(Circle circle) {
        
        /*
         * Let r(t) be a non-vertical line defined by
         * 
         * y = m·x + h                 (1)
         * 
         * and o(a, b, r) a circle with center (a, b) and radius r:
         * 
         *   (x-a)² + (y-b)² = r²
         * ⇔ y = ±sqrt(r² - (x-a)²) + b  (2)
         * 
         * Equating (1) and (2) one gets
         * 
         *     m·x + h = +sqrt(r² - (x-a)²) + b
         * or  m·x + h = -sqrt(r² - (x-a)²) + b
         * 
         * Solving the positive equation first, we get
         * 
         *   (m·x + (h - b))² = r² - (x-a)²
         * ⇔ (m·x)² + 2·m·x(h - b) + (h - b)² = r² - (x²-2·a·x+a²)
         * ⇔ (m·x)² + x² + 2·m·x(h - b) -2·a·x + (h - b)² - r² + a² = 0
         * ⇔ (m² + 1)·x² + (2·m·(h-b) - 2·a)x + [(h - b)² - r² + a²] = 0
         * 
         * This last equation can be solved for x with the quadratic formula.
         * The negative equation (2) is handled in a similar manner.
         * 
         * If the line is vertical x = k, then it is easy to see that the point of
         * intersection is given by
         * 
         * y = ±sqrt(r² - (k-a)²) + b
         * 
         * as long as r² - (k-a)² ≥ 0.
         */

        Point center = circle.center();
        double radius = circle.radius();

        if (radius <= 0) {
            throw new IllegalArgumentException("radius must be > 0, but was " + radius);
        }

        Set<Point> points = new HashSet<>();

        double a = center.x();
        double b = center.y();
        double r2 = radius * radius;

        if (this.direction().x() == 0) {
            // The line is vertical
            double k = this.source().x();
            double delta = r2 - (k - a) * (k - a);

            if (delta >= 0) {
                // Two points of intersection
                double sqDelta = sqrt(delta);
                points.add(Point.pt(k, sqDelta + b));
                points.add(Point.pt(k, -sqDelta + b));
            } else if (delta == 0) {
                // One point of intersection
                points.add(Point.pt(k, b));
            } else {
                // No intersection
            }
        } else {
            // m = dy/dx
            double m = this.direction().y() / this.direction().x();

            // h = y - x·m
            double h = this.source().y() - this.source().x() * m;
            double[] solutions = quadraticSolve(m * m + 1, 2 * m * (h - b) - 2 * a, (h - b) * (h - b) - r2 + a * a);

            switch (solutions.length) {
                case 2:
                    double x2 = solutions[1];
                    double y2 = m * x2 + h;
                    points.add(Point.pt(x2, y2));
                    // fall-through
                case 1:
                    double x1 = solutions[0];
                    double y1 = m * x1 + h;
                    points.add(Point.pt(x1, y1));
                    // fall-through
                case 0:
            }
        }

        return points;
    }


    /**
     * Projects a point p orthogonally over the line, thus returning the point
     * p' which belongs to the line and is closest to p.
     */
    public Point orthoProject(Point point) {
        return this.intersectionWithLine(new Line(point, direction.perp()));
    }


    public Vector direction() {
        return direction;
    }


    public Point source() {
        return source;
    }


    /**
     * Solves the quadratic equation a·x² + b·x + c = 0
     *
     * May return an array of length 0, 1 or 2.
     */
    static double[] quadraticSolve(double a, double b, double c) {
        double[] solutions;

        double delta = b * b - 4 * a * c;
        if (delta > 0) {
            double sqDelta = sqrt(delta);

            // Two solutions
            solutions = new double[2];
            solutions[0] = (-b + sqDelta) / (2 * a);
            solutions[1] = (-b - sqDelta) / (2 * a);
        } else if (delta == 0) {
            // One solution
            solutions = new double[1];
            solutions[0] = -b / (2 * a);
        } else {
            solutions = new double[0];
        }

        return solutions;
    }
}
