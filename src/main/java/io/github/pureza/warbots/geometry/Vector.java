package io.github.pureza.warbots.geometry;

import java.util.Objects;


/**
 * A vector
 */
public class Vector {

    /**
     * Creates a new vector with the given components
     */
    public static Vector vec(double x, double y) {
        return new Vector(x, y);
    }


    /** The x component */
    private final double x;

    /** The y component */
    private final double y;


    /**
     * Creates a new vector with the given components
     */
    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }


    /**
     * Adds this vector to another one
     */
    public Vector plus(Vector other) {
        return new Vector(this.x + other.x, this.y + other.y);
    }


    /**
     * Subtracts a vector from this one
     */
    public Vector minus(Vector other) {
        return new Vector(this.x - other.x, this.y - other.y);
    }


    /**
     * Multiplies this vector by a scalar, component-wise
     */
    public Vector scalarMul(double scalar) {
        return new Vector(x * scalar, y * scalar);
    }


    /**
     * Divides this vector by a scalar, component-wise
     */
    public Vector scalarDiv(double scalar) {
        return new Vector(x / scalar, y / scalar);
    }


    /**
     * Calculates the dot product between this vector and another
     */
    public double dot(Vector other) {
        return this.x * other.x() + this.y * other.y();
    }


    /**
     * Calculates the z component of the cross product between this vector and
     * another, with the z component set to 0, as if they were 3D vectors
     */
    public double cross(Vector other) {
        return this.x * other.y - this.y * other.x;
    }


    /**
     * Calculates the norm of this vector
     */
    public double norm() {
        return Math.sqrt(this.x * this.x + this.y * this.y);
    }


    /**
     * Normalizes this vector
     */
    public Vector normalize() {
        double norm = this.norm();
//        assert (norm > 0.00000001);
        return this.scalarMul(1 / norm);
    }


    /**
     * Finds the angle between two vectors
     *
     * The angle will be between 0 and π
     */
    public double angleWith(Vector other) {
        double cos = this.dot(other) / (this.norm() * other.norm());

        // Bound cos so that -1 ≤ cos ≤ 1
        cos = Math.min(Math.max(cos, -1), 1);

        return Math.acos(cos);
    }


    /**
     * Reverses this vector
     */
    public Vector reverse() {
        return new Vector(-this.x, -this.y);
    }


    /**
     * Returns a vector perpendicular to this one
     */
    @SuppressWarnings("SuspiciousNameCombination")
    public Vector perp() {
        return new Vector(-this.y, this.x);
    }


    /**
     * Truncates this vector to a certain maximum norm
     */
    public Vector truncate(double maxNorm) {
        double mag = this.norm();
        if (mag > maxNorm) {
            return this.scalarMul(maxNorm / mag);
        }

        return this;
    }


    /**
     * Checks if this is the null vector
     */
    public boolean isNull() {
        return x == 0 && y == 0;
    }


    /**
     * Converts the vector to a point
     */
    public Point toPoint() {
        return new Point(this.x, this.y);
    }


    public double x() {
        return this.x;
    }


    public double y() {
        return this.y;
    }


    public String toString() {
        return String.format("vec(%.2f, %.2f)", x, y);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector vector = (Vector) o;
        return Double.compare(vector.x, x) == 0 &&
                Double.compare(vector.y, y) == 0;
    }


    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
