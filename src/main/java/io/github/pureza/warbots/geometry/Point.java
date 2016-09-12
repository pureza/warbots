package io.github.pureza.warbots.geometry;

import java.util.Objects;


/**
 * The point class represents a location in our 2D grid-based environment.
 */
public class Point {


    /**
     * Calculates the midpoint between two points
     */
    public static Point midpoint(Point a, Point b) {
        return new Point((a.x + b.x) / 2.0, (a.y + b.y) / 2.0);
    }


    /**
     * Creates a new point with the given coordinates
     */
    public static Point pt(double x, double y) {
        return new Point(x, y);
    }


    /** x coordinate */
    private final double x;

    /** y coordinate */
    private final double y;


    /**
     * Creates a new point with the given coordinates
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Displaces the current point by the given amount
     */
    public Point displace(double dx, double dy) {
        return new Point(x + dx, y + dy);
    }


    /**
     * Add this point to another point
     */
    public Point plus(Point other) {
        return new Point(x + other.x, y + other.y);
    }


    /**
     * Adds this point to a vector
     */
    public Point plus(Vector other) {
        return new Point(x + other.x(), y + other.y());
    }


    /**
     * Subtracts another point from this one and returns the result as a vector
     */
    public Vector minus(Point other) {
        return new Vector(x - other.x, y - other.y);
    }


    /**
     * Calculates the distance from this point to another one
     */
    public double distanceTo(Point other) {
        return Math.sqrt(sqDistanceTo(other));
    }


    /**
     * Calculates the squared distance from this point to another one
     */
    public double sqDistanceTo(Point other) {
        return (this.x - other.x) * (this.x - other.x) + (this.y - other.y) * (this.y - other.y);
    }


    /**
     * Rotates this point by the given angle around the origin
     *
     * The angle is in radians.
     */
    public Point rotate(double angle) {
        double newX = x * Math.cos(angle) - y * Math.sin(angle);
        double newY = y * Math.cos(angle) + x * Math.sin(angle);

        return new Point(newX, newY);
    }


    public double x() {
        return x;
    }


    public double y() {
        return y;
    }


    public String toString() {
        return String.format("pt(%f, %f)", x, y);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.x, x) == 0 &&
                Double.compare(point.y, y) == 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
