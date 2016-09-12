package io.github.pureza.warbots.geometry;


/**
 * Just a simple size - something with a width and a height.
 */
public class Size {

    /** The width */
    private final int width;

    /** The height */
    private final int height;


    /**
     * Creates a new Size of size width x height
     */
    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }


    public int width() {
        return width;
    }


    public int height() {
        return height;
    }
}
