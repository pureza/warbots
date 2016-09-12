package io.github.pureza.warbots.navigation;

import io.github.pureza.warbots.collection.Graph;
import io.github.pureza.warbots.entities.*;
import io.github.pureza.warbots.geometry.*;
import io.github.pureza.warbots.search.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.floor;
import static java.util.Arrays.asList;
import static io.github.pureza.warbots.geometry.Circle.circle;
import static io.github.pureza.warbots.geometry.Point.pt;

/**
 * The game map
 *
 * WarBots is a grid-based game and, to reflect that, every
 * map is composed by cell's of class Map.Cell
 */
public class Map {

    /** The map grid - a 2D array of Map.Cells */
    private final Cell[][] grid;

    /** The navigational graph of the map */
    private final Graph<Point, Double> navGraph = new Graph<>();

    /** The items lying around in the floor waiting for someone to pick them up */
    private final List<InventoryItem> items = new ArrayList<>();

    /** Random number generator */
    private final Random random = new Random(2);


    /**
     * Creates a new map of containing width x height cells
     */
    public Map(int width, int height) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException();
        }

        this.grid = new Cell[height + 2][width + 2];

        for (int i = 0; i < height + 2; i++) {
            for (int j = 0; j < width + 2; j++) {
                this.grid[i][j] = new Cell(new Point(j - 1, i - 1));
            }
        }

        // Surround the arena with walls
        this.addEntity(new Wall(new Point(-1, -1), new Size(width + 2, 1)));
        this.addEntity(new Wall(new Point(-1, height), new Size(width + 2, 1)));
        this.addEntity(new Wall(new Point(-1, 0), new Size(1, height)));
        this.addEntity(new Wall(new Point(width, 0), new Size(1, height)));
    }


    /**
     * Adds a static entity to the map, but only if the area occupied by the
     * entity is free
     *
     * Returns true if the entity was added and false otherwise.
     */
    public boolean addEntity(StaticEntity entity) {
        try {
            if (isAreaClear(entity.getLocation(), entity.getSize())) {
                for (int i = 0; i < entity.getSize().height(); i++) {
                    for (int j = 0; j < entity.getSize().width(); j++) {
                        Cell cell = cellAt((int) floor(entity.getLocation().x()) + j, (int) floor(entity.getLocation().y()) + i);
                        cell.setEntity(entity);
                    }
                }

                return true;
            }

            return false;
        } catch (IndexOutOfBoundsException ex) {
            // Return false, to be consistent with the case when the wall is
            // only partially outside the map
            return false;
        }
    }


    /**
     * Adds an item to a specific cell in the map, but only if it's free
     *
     * Returns true if the item was added and false otherwise
     */
    public boolean addItem(InventoryItem item) {
        // Only add to this.items if able to add to the cell
        if (this.cellAt(item.getLocation()).setItem(item)) {
            this.items.add(item);
            return true;
        }

        return false;
    }


    /**
     * Removes an inventory item from the map
     *
     * Returns true if the item was removed and false otherwise.
     */
    public boolean removeItem(InventoryItem item) {
        cellAt(item.getLocation()).removeItem();
        return this.items.remove(item);
    }


    /**
     * The list of items lying on the map
     */
    public List<InventoryItem> getItems() {
        return this.items;
    }


    /**
     * Returns the active first aid kits on the map
     */
    public List<FirstAidItem> getFirstAidItems() {
        return getItems().stream()
                .filter(item -> item instanceof FirstAidItem && item.isActive())
                .map(item -> (FirstAidItem) item)
                .collect(Collectors.toList());
    }


    /**
     * Returns the active weapons on the map
     */
    public List<WeaponItem> getWeaponItems() {
        return getItems().stream()
                .filter(item -> item instanceof WeaponItem && item.isActive())
                .map(item -> (WeaponItem) item)
                .collect(Collectors.toList());
    }


    /**
     * Returns the item at the given location, or null if none exists
     */
    public InventoryItem getItemAt(Point location) {
        if (!isInside(location)) {
            throw new IndexOutOfBoundsException();
        }

        return cellAt(location).getItem();
    }


    /**
     * Returns a list of cells around a given point
     */
    public List<Point> getNeighbours(Point seed) {
        if (!isInside(seed)) {
            throw new IndexOutOfBoundsException(String.valueOf(seed));
        }

        // No need to floor the coordinates because we know the seed is inside!
        int x = (int) seed.x();
        int y = (int) seed.y();

        return asList(
                new Point(x - 1, y),
                new Point(x + 1, y),
                new Point(x, y - 1),
                new Point(x, y + 1),
                new Point(x - 1, y - 1),
                new Point(x + 1, y - 1),
                new Point(x - 1, y + 1),
                new Point(x + 1, y + 1));
    }


    /**
     * Returns the free cells around a given point
     *
     * A diagonal is only returned if both sides are also free, because the
     * moving entity needs to pass through them if it goes on a straight path.
     */
    public List<Point> getFreeNeighbours(Point seed) {
        List<Point> neighbours = getNeighbours(seed);

        return neighbours.stream()
                .filter(neighbour -> cellAt(neighbour).isFree())
                .filter(neighbour -> {
                    // If this is a corner, then both surrounding cells must also be free
                    if (seed.x() != neighbour.x() && seed.y() != neighbour.y()) {
                        // It is a corner
                        Point surroundingSameX = new Point(neighbour.x(), seed.y());
                        Point surroundingSameY = new Point(seed.x(), neighbour.y());
                        return cellAt(surroundingSameX).isFree() && cellAt(surroundingSameY).isFree();
                    } else {
                        // No corner
                        return true;
                    }
                })
                .collect(Collectors.toList());
    }


    /**
     * Returns all the cells traversed by a path between two points
     */
    public Set<Cell> pathTransversedCells(Point source, Point target) {
        if (!this.isInside(source) || !this.isInside(target)) {
            throw new IndexOutOfBoundsException(source + " or " + target);
        }

        Set<Cell> cells = new HashSet<>();

        Cell sourceCell = cellAt(source);
        Cell targetCell = cellAt(target);

        // Add the first cell to the list
        cells.add(cellAt(source));

        // Both points lie in the same cell. Nothing to do!
        if (sourceCell.equals(targetCell)) {
            return cells;
        }

        // Path's direction vector
        io.github.pureza.warbots.geometry.Vector direction = target.minus(source).normalize();

        Cell currentCell = sourceCell;
        Point previous = source;
        while (currentCell != null && !currentCell.equals(targetCell)) {
            // Move the current point forward along the direction vector
            Point current = previous.plus(direction);
            currentCell = isInside(current) ? cellAt(current) : null;

            if (currentCell != null) {
                // We might be adding the same cell twice, but that's ok
                cells.add(currentCell);
            }

            // If the current and previous cells are diagonal to each other, we
            // have to find out if the path crossed another cell in between
            // Note that we don't need to floor the coordinates because we know
            // the points are inside the map!
            if ((int) previous.x() != (int) current.x() && (int) previous.y() != (int) current.y()) {
                // First, let's discover which corner they have in common
                Point corner = new Point((int) Math.max(current.x(), previous.x()), (int) Math.max(current.y(), previous.y()));

                // Project the corner orthogonally onto the line. This point is
                // inside the cell we are looking for
                Line line = new Line(previous, direction);
                Point nearest = line.orthoProject(corner);
                Cell nearestCell = cellAt(nearest);
                cells.add(nearestCell);
            }

            previous = current;
        }

        return cells;
    }


    /**
     * Checks if a path between two points is obstructed by a static entity
     */
    public boolean isPathObstructed(Point source, Point target) {
        if (!this.isInside(source) || !this.isInside(target)) {
            return true;
        }

        return !pathTransversedCells(source, target).stream()
                .allMatch(Cell::isFree);
    }


    /**
     * Finds a path from one location to another
     */
    public Path<Point> findPath(Point source, Point target) {
        try {
            return new AStarSearch<>(this.navGraph, source, new FindTargetCondition<>(this.navGraph, target),
                    new ManhattanHeuristic()).search();
        } catch (NoPathFoundException e) {
            // Can't happen, unless to navigational graph is disconnected!
            throw new RuntimeException(e);
        }
    }


    /**
     * Returns the set of cells a circle is occupying
     *
     * This is useful to find out which cells an entity at a given location and
     * with a certain bounding radius is occupying
     */
    public Set<Point> getOccupyingCells(Point center, double boundingRadius) {
        Set<Point> cells = new HashSet<>();

        // Clearly, the entity occupies the cell it is centered at
        cells.add(cellAt(center).getLocation());

        // If the bounding circle touches any of the four borders around the
        // center cell, add the cells beyond those borders
        cells.addAll(getOccupyingCellsBeyondBorder(Direction.LEFT, center, boundingRadius));
        cells.addAll(getOccupyingCellsBeyondBorder(Direction.RIGHT, center, boundingRadius));
        cells.addAll(getOccupyingCellsBeyondBorder(Direction.TOP, center, boundingRadius));
        cells.addAll(getOccupyingCellsBeyondBorder(Direction.BOTTOM, center, boundingRadius));
        return cells;
    }


    /**
     * Check if the bounding circle intersects a border
     * - If it doesn't intersect, there is nothing to do
     * - If the intersection occurs at one point, there is nothing to do
     * - If it intersects at two points, then we need to add the cell or
     *   cells on the other side of the border
     * - However, if one of these two points is a corner, we don't add it!
     */
    private Set<Point> getOccupyingCellsBeyondBorder(Direction border, Point center, double boundingRadius) {
        Set<Point> cells = new HashSet<>();

        // Avoid computing the intersections when it is clear that the circle
        // is too far away from the border
        boolean mayIntersect = false;

        // A point on the border
        Point borderPoint = null;

        switch (border) {
            case LEFT:
                mayIntersect = center.x() - (int) center.x() < boundingRadius;
                borderPoint = pt((int) center.x(), (int) center.y());
                break;
            case RIGHT:
                mayIntersect = ((int) center.x()) + 1 - center.x() < boundingRadius;
                borderPoint = pt((int) center.x() + 1, (int) center.y() + 1);
                break;
            case TOP:
                mayIntersect = (int) center.y() + 1 - center.y() < boundingRadius;
                borderPoint = pt((int) center.x() + 1, (int) center.y() + 1);
                break;
            case BOTTOM:
                mayIntersect = center.y() - (int) center.y() < boundingRadius;
                borderPoint = pt((int) center.x(), (int) center.y());
                break;
        }

        // How much to displace the intersection point to make sure we end up
        // inside the neighbour cell
        io.github.pureza.warbots.geometry.Vector displacement = border.vector().scalarMul(0.5);

        // The direction of the border
        io.github.pureza.warbots.geometry.Vector borderDirection = displacement.perp();

        if (mayIntersect) {
            // This is the border line, given by a point and a direction
            Line borderLine = new Line(borderPoint, borderDirection);
            Set<Point> intersection = borderLine.intersectionWithCircle(circle(center, boundingRadius));
            if (intersection.size() == 2) {
                cells.addAll(intersection.stream()
                        .filter(point -> !isCorner(point))
                        .map(point -> cellAt(point.plus(displacement)).getLocation())
                        .collect(Collectors.toList()));
            }
        }

        return cells;
    }


    /**
     * Checks if an entity can move source one point to another, in a straight
     * line, ignoring any collisions that occur only at the end point
     *
     * An entity can move if its bounding circle will never collide with any
     * wall.
     */
    public boolean canMoveBetween(Point source, Point target, double boundingRadius) {
        // We clearly can move source one point to itself!
        if (source.equals(target)) {
            return true;
        }

        io.github.pureza.warbots.geometry.Vector direction = target.minus(source).normalize();

        // Check if the left and right side points of the bounding circle bump
        // into any wall during the movement
        Set<Point> referencePoints = getBoundingCircleSides(source, direction, boundingRadius);

        double distX = target.x() - source.x();
        double distY = target.y() - source.y();

        for (Point point : referencePoints) {
            Point displaced = point.displace(distX, distY);
            if (isPathObstructed(point, displaced)) {
                return false;
            }
        }

        return true;
    }


    /**
     * Checks if an entity can stay at a certain location without bumping into
     * any wall
     */
    public boolean canStayAt(Point location, double boundingRadius) {
        // Check if the circle is penetrating a wall at the end
        return getOccupyingCells(location, boundingRadius).stream().allMatch(point -> cellAt(point).isFree());
    }


    /**
     * Checks if an entity can move from one point to another, in a straight
     * line
     *
     * An entity can move if its bounding circle will never collide with any
     * wall. To figure this out, we do two checks:
     * - First, we check whether the diameter of the bounding circle bumps into
     *   walls along the course
     * - Second, we check whether part of the circle is penetrating a wall
     *   at the target location
     *
     * The first check is enough to figure out if part of the circle collides
     * with a wall during the trajectory, provided the bounding radius is small
     * enough (certainly, smaller than 1).
     *
     * The second check is necessary because it is possible that only a tiny
     * bit of the circle penetrates the wall at the end, and the first check
     * is unable to detect that.
     */
    public boolean canMoveTo(Point source, Point target, double boundingRadius) {
        return canMoveBetween(source, target, boundingRadius) && canStayAt(target, boundingRadius);
    }


    /**
     * Calculates the left and right points of the bounding circle,
     * assuming the entity will move alongside the direction vector
     *
     * The direction vector must be normalized.
     */
    public Set<Point> getBoundingCircleSides(Point center, io.github.pureza.warbots.geometry.Vector direction, double boundingRadius) {
        assert (Math.abs(direction.norm() - 1.0) < 0.0001);

        // The left and right sides of the bounding circle are the points
        // of intersection of the bounding circle with the diameter
        // perpendicular to the direction of movement
        Line diameter = new Line(center, direction.perp());
        return diameter.intersectionWithCircle(circle(center, boundingRadius));
    }


    /**
     * Randomly chooses a free cell on the map and returns its center
     */
    public Point chooseRandomLocation() {
        Point point;
        do {
            double x = random.nextInt(width());
            double y = random.nextInt(height());

            point = new Point(x, y);
        } while (!cellAt(point).isFree());

        return cellAt(point).getCenter();
    }


    /**
     * Creates the navigation graph for this map
     */
    public void buildNavGraph() {
        // Select the starting point...
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                if (cellAt(x, y).isFree()) {
                    buildNavGraphHelper(new Point(x, y), this.navGraph);
                }
            }
        }
    }


    /**
     * Creates the navigation graph recursively from a given starting point
     *
     * @param seed The point where the graph creation begins.
     * @param graph The mutable graph where vertices and edges will be added to
     */
    void buildNavGraphHelper(Point seed, Graph<Point, Double> graph) {
        Point seedCenter = seed.displace(0.5, 0.5);
        if (!cellAt(seedCenter).isFree()) {
            throw new IllegalArgumentException("The seed cell is occupied!");
        }

        if (!graph.contains(seedCenter)) {
            graph.add(seedCenter);
        }

        // For each free neighbour...
        List<Point> neighbours = getFreeNeighbours(seed);
        for (Point neighbour : neighbours) {
            Point cellCenter = neighbour.displace(0.5, 0.5);

            // If the neighbour is not in the graph...
            if (!graph.contains(cellCenter)) {
                // Add the neighbour and the edge connecting to it
                graph.add(cellCenter);
                graph.addEdge(seedCenter, cellCenter, seedCenter.distanceTo(cellCenter));

                // Recursive call on the neighbour
                buildNavGraphHelper(neighbour, graph);
            } else if (!graph.containsEdge(cellCenter, seedCenter)) {
                // Even if the neighbor is already on the graph, we might still
                // have to create the edge
                graph.addEdge(seedCenter, cellCenter, seedCenter.distanceTo(cellCenter));
            }
        }
    }


    /**
     * Returns the cell at position (x, y)
     */
    public Cell cellAt(int x, int y) {
        return this.grid[y + 1][x + 1];
    }


    /**
     * Returns the cell at the position given by the point
     */
    public Cell cellAt(Point point) {
        return this.grid[(int) floor(point.y()) + 1][(int) floor(point.x()) + 1];
    }


    /**
     * The number of cells along the horizontal axis
     */
    public int width() {
        return this.grid[0].length - 2;
    }


    /**
     * The number of cells along the vertical axis.
     */
    public int height() {
        return this.grid.length - 2;
    }


    /**
     * Checks if the point is a corner
     */
    public boolean isCorner(Point point) {
        // Note that it is not necessary to floor the coordinates because
        // flooring and casting is the same when the numbers are integers
        return point.x() == (int) point.x() && point.y() == (int) point.y();
    }


    /**
     * Checks if a point lies inside the map
     */
    public boolean isInside(Point point) {
        return point.x() >= 0 && point.x() < this.width() && point.y() >= 0 && point.y() < this.height();
    }


    /**
     * A simple textual representation of the map
     */
    @Override
    public String toString() {
        String result = "";
        for (int i = this.height() - 1; i >= 0; i--) {
            for (int j = 0; j < this.width(); j++) {
                if (!this.cellAt(j, i).isFree()) {
                    result += "X ";
                } else {
                    result += ". ";
                }
            }
            result += "\n";
        }
        return result;
    }


    /**
     * Checks if an entire rectangle-shaped area is free of walls, buildings and
     * other static entities in general
     *
     * @param location The bottom-left corner of the area.
     * @param size     The size of the area.
     */
    boolean isAreaClear(Point location, Size size) {
        for (int i = 0; i < size.height(); i++) {
            for (int j = 0; j < size.width(); j++) {
                Cell cell = cellAt((int) floor(location.x()) + j, (int) floor(location.y()) + i);
                if (!cell.isFree()) {
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * A Map.Cell represents a cell of the grid map. A cell can be in two
     * states: free or occupied by a static entity (wall, tree, building, etc).
     * A cell is considered free even if a moving agent is currently positioned
     * inside it - for all we care, cells are occupied by static entities only.
     *
     * A cell can also have exactly one inventory item (weapon or first-aid
     * kit).
     */
    public static class Cell {

        /** This cell's location */
        private final Point location;

        /** This cell's entity. If null, the cell is free */
        private StaticEntity entity;

        /** The inventory item occupying this cell, if any */
        private InventoryItem item;


        public Cell(Point location) {
            this.location = location;
        }


        /**
         * Returns the entity occupying this cell, or null if the cell is free
         */
        public StaticEntity getEntity() {
            return this.entity;
        }


        /**
         * Sets the entity located at this cell
         */
        public void setEntity(StaticEntity entity) {
            if (this.isFree() && this.item == null) {
                this.entity = entity;
            } else {
                throw new IllegalStateException("Cell already contains something");
            }
        }


        /**
         * Returns the inventory item in this cell, or null if there is none
         */
        public InventoryItem getItem() {
            return this.item;
        }


        /**
         * Sets the inventory item in this cell, but only if:
         *
         * - The cell is empty
         * - The cell doesn't already contain an item.
         *
         * Returns true if the item was added and false otherwise
         */
        public boolean setItem(InventoryItem item) {
            if (this.isFree() && this.item == null) {
                this.item = item;
                return true;
            }

            return false;
        }


        /**
         * Removes the inventory item from this cell
         *
         * Returns the item removed.
         */
        public InventoryItem removeItem() {
            InventoryItem previous = this.item;
            this.item = null;
            return previous;
        }


        /**
         * Checks if this cell is free
         *
         * A cell is considered to be free if it is not occupied by a
         * StaticEntity. It doesn't matter if there is a bot or an inventory
         * item.
         */
        public boolean isFree() {
            return entity == null;
        }


        /**
         * The cell's index, from (0, 0) to (width - 1, height - 1)
         */
        public Point getLocation() {
            return this.location;
        }


        /**
         * The cell's center
         */
        public Point getCenter() {
            return this.location.displace(0.5, 0.5);
        }
    }
}
