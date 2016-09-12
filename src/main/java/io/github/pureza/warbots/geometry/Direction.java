package io.github.pureza.warbots.geometry;


/**
 * Common directions
 */
public enum Direction {

    /** Top direction */
    TOP(Vector.vec(0, 1)),

    /** Left direction */
    LEFT(Vector.vec(-1, 0)),

    /** Bottom direction */
    BOTTOM(Vector.vec(0, -1)),

    /** Right direction */
    RIGHT(Vector.vec(1, 0)),

    /** Top-right direction */
    TOP_RIGHT(Vector.vec(1, 1).normalize()),

    /** Top-left direction */
    TOP_LEFT(Vector.vec(-1, 1).normalize()),

    /** Bottom-right direction */
    BOTTOM_RIGHT(Vector.vec(1, -1).normalize()),

    /** Bottom-left direction */
    BOTTOM_LEFT(Vector.vec(-1, -1).normalize());


    /** The direction vector */
    private final Vector vector;

    Direction(Vector vector) {
        this.vector = vector;
    }


    public Vector vector() {
        return vector;
    }
}
