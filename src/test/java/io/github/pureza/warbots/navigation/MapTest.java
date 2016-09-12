package io.github.pureza.warbots.navigation;

import io.github.pureza.warbots.collection.Graph;
import io.github.pureza.warbots.entities.FirstAidItem;
import io.github.pureza.warbots.entities.InventoryItem;
import io.github.pureza.warbots.entities.Wall;
import io.github.pureza.warbots.entities.WeaponItem;
import io.github.pureza.warbots.geometry.Direction;
import org.hamcrest.Matchers;
import org.junit.Test;
import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Size;
import io.github.pureza.warbots.weaponry.Weapon;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static io.github.pureza.warbots.Matchers.closeTo;
import static io.github.pureza.warbots.Tests.*;
import static io.github.pureza.warbots.geometry.Point.pt;


public class MapTest {

    private TestConfig config = new TestConfig();

    /*
     * Map(int width, int height)
     */

    @Test(expected=IllegalArgumentException.class)
    public void constructorFailsIfWidthIsTooLow() {
        new Map(0, 100);
    }


    @Test(expected=IllegalArgumentException.class)
    public void constructorFailsIfHeightIsTooLow() {
        new Map(100, 0);
    }


    @Test
    public void constructorCreatesMapWithTheRightSize() {
        Map map = new Map(10, 5);
        assertThat(map.width(), is(10));
        assertThat(map.height(), is(5));
    }


    @Test
    public void constructorFillsMapWithCells() {
        Map map = new Map(10, 5);
        for (int i = 0; i < map.width(); i++) {
            for (int j = 0; j < map.height(); j++) {
                assertThat(map.cellAt(i, j), is(not(nullValue())));
            }
        }
    }


    @Test
    public void constructorSurroundsMapWithWalls() {
        Map map = new Map(10, 5);
        for (int i = 0; i < map.width(); i++) {
            assertThat(map.cellAt(i, -1).getEntity(), is(instanceOf(Wall.class)));
            assertThat(map.cellAt(i, map.height()).getEntity(), is(instanceOf(Wall.class)));
        }

        for (int j = 0; j < map.height(); j++) {
            assertThat(map.cellAt(-1, j).getEntity(), is(instanceOf(Wall.class)));
            assertThat(map.cellAt(map.width(), j).getEntity(), is(instanceOf(Wall.class)));
        }
    }


    /*
     * boolean addEntity(StaticEntity entity)
     */

    @Test
    public void addEntityToFreeAreaSucceeds() {
        Map map = new Map(10, 5);
        Wall wall = new Wall(pt(2, 2), new Size(1, 1));
        assertThat(map.addEntity(wall), is(true));
        assertThat(map.cellAt(wall.getLocation()).getEntity(), is(wall));
    }


    @Test
    public void addEntityUpdatesAllAffectedCells() {
        Map map = new Map(10, 5);
        Wall wall = new Wall(pt(2, 2), new Size(3, 2));
        assertThat(map.addEntity(wall), is(true));

        // Make sure the wall occupies the whole area
        assertThat(map.cellAt(wall.getLocation().displace(0, 0)).getEntity(), is(wall));
        assertThat(map.cellAt(wall.getLocation().displace(1, 0)).getEntity(), is(wall));
        assertThat(map.cellAt(wall.getLocation().displace(2, 0)).getEntity(), is(wall));
        assertThat(map.cellAt(wall.getLocation().displace(0, 1)).getEntity(), is(wall));
        assertThat(map.cellAt(wall.getLocation().displace(1, 1)).getEntity(), is(wall));
        assertThat(map.cellAt(wall.getLocation().displace(2, 1)).getEntity(), is(wall));
    }


    @Test
    public void addEntityFailsOnOccupiedArea() {
        Map map = new Map(10, 5);
        Wall wall = new Wall(pt(2, 2), new Size(5, 1));
        assertThat(map.addEntity(wall), is(true));

        Wall otherWall = new Wall(pt(4, 0), new Size(1, 3));
        assertThat(map.addEntity(otherWall), is(false));
    }


    @Test
    public void addEntityFailsForMapOuterWall() {
        Map map = new Map(10, 5);
        Wall wall = new Wall(pt(-1, -1), new Size(1, 1));
        assertThat(map.addEntity(wall), is(false));
    }


    @Test
    public void addEntityFailsForWallCompletelyOutsideTheMap() {
        Map map = new Map(10, 5);
        Wall wall = new Wall(pt(100, 100), new Size(1, 1));
        assertThat(map.addEntity(wall), is(false));
    }


    @Test
    public void addEntityFailsForWallPartiallyOutsideTheMap() {
        Map map = new Map(10, 5);
        Wall wall = new Wall(pt(5, 5), new Size(100, 1));
        assertThat(map.addEntity(wall), is(false));
    }


    /*
     * boolean addItem(InventoryItem item)
     */

    @Test
    public void addItemToFreeCellSucceeds() {
        Map map = new Map(10, 5);
        InventoryItem item = buildFirstAidItem(pt(2.5, 2.5));
        assertThat(map.addItem(item), is(true));
    }


    @Test
    public void addItemUpdatesCell() {
        Map map = new Map(10, 5);
        InventoryItem item = buildFirstAidItem(pt(2.5, 2.5));
        assertThat(map.addItem(item), is(true));
        assertThat(map.cellAt(item.getLocation()).getItem(), is(item));
    }


    @Test
    public void addItemFailsIfCellAlreadyHasAnotherItem() {
        Map map = new Map(10, 5);
        InventoryItem originalItem = buildFirstAidItem(pt(2.5, 2.5));
        map.addItem(originalItem);

        InventoryItem newItem = buildWeaponItem(pt(2.5, 2.5), Weapon.WeaponType.HANDGUN);
        assertThat(map.addItem(newItem), is(false));
    }


    @Test(expected=IndexOutOfBoundsException.class)
    public void addItemFailsIfLocationIsOutsideTheMap() {
        Map map = new Map(10, 5);
        InventoryItem item = buildFirstAidItem(pt(200.5, 200.5));
        map.addItem(item);
    }


    /*
     * void removeItem(FirstAidItem item)
     */

    @Test
    public void removeItemReturnsFalseIfItemDoesNotExist() {
        Map map = new Map(10, 5);
        FirstAidItem item = buildFirstAidItem(pt(2.5, 2.5));
        assertThat(map.removeItem(item), is(false));
    }


    @Test
    public void removeItemRemovesExistingItem() {
        Map map = new Map(10, 5);
        FirstAidItem item = buildFirstAidItem(pt(2.5, 2.5));
        map.addItem(item);
        assertThat(map.cellAt(item.getLocation()).getItem(), is(item));
        assertThat(map.removeItem(item), is(true));
        assertThat(map.cellAt(item.getLocation()).getItem(), is(nullValue()));
    }


    /*
     * List<InventoryItem> getItems()
     */

    @Test
    public void getItemsReturnsEmptyWhenThereAreNoItems() {
        Map map = new Map(10, 5);
        assertThat(map.getItems(), is(empty()));
    }


    @Test
    public void getItemsReturnsMapItems() {
        Map map = new Map(10, 5);

        FirstAidItem firstAidItem = buildFirstAidItem(pt(2.5, 2.5));
        map.addItem(firstAidItem);

        WeaponItem weaponItem = buildWeaponItem(pt(3.5, 3.5), Weapon.WeaponType.HANDGUN);
        map.addItem(weaponItem);

        assertThat(map.getItems(), containsInAnyOrder(firstAidItem, weaponItem));
    }


    @Test
    public void getItemsDoesNotReturnRemovedItems() {
        Map map = new Map(10, 5);

        FirstAidItem firstAidItem = buildFirstAidItem(pt(2.5, 2.5));
        map.addItem(firstAidItem);

        WeaponItem weaponItem = buildWeaponItem(pt(3.5, 3.5), Weapon.WeaponType.HANDGUN);
        map.addItem(weaponItem);

        map.removeItem(firstAidItem);

        assertThat(map.getItems(), contains(weaponItem));
    }


    /*
     * List<FirstAidItem> getFirstAidItems()
     */

    @Test
    public void getFirstAidItemsReturnsEmptyWhenThereAreNoFirstAidItems() {
        Map map = new Map(10, 5);
        assertThat(map.getFirstAidItems(), is(empty()));
    }


    @Test
    public void getFirstAidItemsReturnsActiveFirstAidItems() {
        Map map = new Map(10, 5);

        FirstAidItem firstAidItemA = buildFirstAidItem(pt(2.5, 2.5));
        map.addItem(firstAidItemA);

        FirstAidItem firstAidItemB = buildFirstAidItem(pt(3.5, 3.5));
        map.addItem(firstAidItemB);

        assertThat(map.getFirstAidItems(), contains(firstAidItemA, firstAidItemB));
    }


    @Test
    public void getFirstAidItemsIgnoresInactiveFirstAidItems() {
        Map map = new Map(10, 5);

        FirstAidItem firstAidItemA = buildFirstAidItem(pt(2.5, 2.5));
        map.addItem(firstAidItemA);

        FirstAidItem firstAidItemB = buildFirstAidItem(pt(3.5, 3.5));
        map.addItem(firstAidItemB);

        firstAidItemB.deactivate();

        assertThat(map.getFirstAidItems(), contains(firstAidItemA));
    }
    
    
    /*
     * List<WeaponItem> getWeaponItems()
     */

    @Test
    public void getWeaponItemsReturnsEmptyWhenThereAreNoWeapons() {
        Map map = new Map(10, 5);
        assertThat(map.getWeaponItems(), is(empty()));
    }


    @Test
    public void getWeaponItemsReturnsActiveWeapons() {
        Map map = new Map(10, 5);

        WeaponItem weaponItemA = buildWeaponItem(pt(2.5, 2.5), Weapon.WeaponType.HANDGUN);
        map.addItem(weaponItemA);

        WeaponItem weaponItemB = buildWeaponItem(pt(3.5, 3.5), Weapon.WeaponType.LASER_GUN);
        map.addItem(weaponItemB);

        assertThat(map.getWeaponItems(), contains(weaponItemA, weaponItemB));
    }


    @Test
    public void getWeaponItemsIgnoresInactiveWeapons() {
        Map map = new Map(10, 5);

        WeaponItem weaponItemA = buildWeaponItem(pt(2.5, 2.5), Weapon.WeaponType.HANDGUN);
        map.addItem(weaponItemA);

        WeaponItem weaponItemB = buildWeaponItem(pt(3.5, 3.5), Weapon.WeaponType.LASER_GUN);
        map.addItem(weaponItemB);

        weaponItemB.deactivate();

        assertThat(map.getWeaponItems(), contains(weaponItemA));
    }


    /*
     * InventoryItem getItemAt(Point location)
     */

    @Test
    public void getItemAtReturnsNullIfThereIsNoItem() {
        Map map = new Map(10, 5);
        assertThat(map.getItemAt(pt(2, 2)), is(nullValue()));
    }


    @Test
    public void getItemAtReturnsItemAtLocation() {
        Map map = new Map(10, 5);

        WeaponItem weaponItem = buildWeaponItem(pt(2.5, 2.5), Weapon.WeaponType.HANDGUN);
        map.addItem(weaponItem);

        assertThat(map.getItemAt(pt(2, 2)), is(weaponItem));
    }


    @Test(expected=IndexOutOfBoundsException.class)
    public void getItemAtFailsIfLocationIsOutOfBounds() {
        Map map = new Map(10, 5);
        map.getItemAt(pt(-1, 0));
    }


    /*
     * List<Point> getNeighbours(Point seed)
     */

    @Test
    public void getNeighboursReturnsAllNeighboursAroundPoint() {
        Map map = new Map(10, 5);
        assertThat(map.getNeighbours(pt(2, 2)), containsInAnyOrder(
                pt(1, 1), pt(2, 1), pt(3, 1),
                pt(1, 2), pt(3, 2),
                pt(1, 3), pt(2, 3), pt(3, 3)));
    }


    @Test
    public void getNeighboursReturnsMapEdges() {
        Map map = new Map(10, 5);
        assertThat(map.getNeighbours(pt(0, 0)), containsInAnyOrder(
                pt(-1, -1), pt(-1, 0), pt(-1, 1),
                pt(0, -1), pt(0, 1),
                pt(1, -1), pt(1, 0), pt(1, 1)));
    }


    @Test(expected=IndexOutOfBoundsException.class)
    public void getNeighboursFailsIfPointIsOutsideMap() {
        Map map = new Map(10, 5);
        map.getNeighbours(pt(-1, 0));
    }


    /*
     * List<Point> getFreeNeighbours(Point seed)
     */

    @Test(expected=IndexOutOfBoundsException.class)
    public void getFreeNeighboursFailsIfPointIsOutsideMap() {
        Map map = new Map(10, 5);
        map.getFreeNeighbours(pt(-1, 0));
    }

    @Test
    public void getFreeNeighboursReturnsFreeNeighbour() {
        Map map = new Map(10, 5);
        assertThat(map.getFreeNeighbours(pt(0, 0)), containsInAnyOrder(pt(1, 0), pt(0, 1), pt(1, 1)));
    }


    @Test
    public void getFreeNeighboursDoesNotReturnOccupiedNeighbour() {
        Map map = new Map(10, 5);
        map.addEntity(new Wall(pt(1, 1), new Size(1, 1)));
        assertThat(map.getFreeNeighbours(pt(0, 0)), containsInAnyOrder(pt(1, 0), pt(0, 1)));
    }


    @Test
    public void getFreeNeighboursDoesNotReturnFreeDiagonalWithOccupiedSides() {
        Map map = new Map(10, 5);
        map.addEntity(new Wall(pt(1, 0), new Size(1, 1)));
        map.addEntity(new Wall(pt(0, 1), new Size(1, 1)));
        assertThat(map.getFreeNeighbours(pt(0, 0)), is(empty()));
    }


    @Test
    public void getFreeNeighboursDoesNotReturnFreeDiagonalWithFreeSideVertical() {
        Map map = new Map(10, 5);
        map.addEntity(new Wall(pt(0, 1), new Size(1, 1)));
        assertThat(map.getFreeNeighbours(pt(0, 0)), containsInAnyOrder(pt(1, 0)));
    }


    @Test
    public void getFreeNeighboursDoesNotReturnFreeDiagonalWithFreeSideHorizontal() {
        Map map = new Map(10, 5);
        map.addEntity(new Wall(pt(1, 0), new Size(1, 1)));
        assertThat(map.getFreeNeighbours(pt(0, 0)), containsInAnyOrder(pt(0, 1)));
    }


    /*
     * Set<Cell> pathTransversedCells(Point source, Point target)
     */


    @Test
    public void pathTransversedCellsReturnsSingleCellWhenSourceIsEqualToTarget() {
        Map map = new Map(10, 10);
        assertThat(map.pathTransversedCells(pt(1, 1), pt(1, 1)), contains(map.cellAt(1, 1)));
    }


    @Test
    public void pathTransversedCellsReturnsSingleCellForVerySmallPath() {
        Map map = new Map(10, 5);
        assertThat(map.pathTransversedCells(pt(0.5, 0.5), pt(0.6, 0.6)), contains(map.cellAt(0, 0)));
    }


    @Test
    public void pathTransversedCellsReturnsRightCell() {
        Map map = new Map(10, 5);
        assertThat(map.pathTransversedCells(pt(0.5, 0.5), pt(1.6, 0.6)), Matchers.containsInAnyOrder(map.cellAt(0, 0), map.cellAt(1, 0)));
    }


    @Test
    public void pathTransversedCellsReturnsTopCell() {
        Map map = new Map(10, 5);
        assertThat(map.pathTransversedCells(pt(0.5, 0.5), pt(0.6, 1.6)), Matchers.containsInAnyOrder(map.cellAt(0, 0), map.cellAt(0, 1)));
    }


    @Test
    public void pathTransversedCellsReturnsLeftCell() {
        Map map = new Map(10, 5);
        assertThat(map.pathTransversedCells(pt(1.6, 0.6), pt(0.5, 0.5)), Matchers.containsInAnyOrder(map.cellAt(0, 0), map.cellAt(1, 0)));
    }


    @Test
    public void pathTransversedCellsReturnsBottomCell() {
        Map map = new Map(10, 5);
        assertThat(map.pathTransversedCells(pt(0.6, 1.6), pt(0.5, 0.5)), Matchers.containsInAnyOrder(map.cellAt(0, 0), map.cellAt(0, 1)));
    }


    @Test
    public void pathTransversedCellsReturnsTopRightCellAndTopNeighbour() {
        Map map = new Map(10, 5);
        assertThat(map.pathTransversedCells(pt(0.5, 0.5), pt(1.5, 1.6)), Matchers.containsInAnyOrder(map.cellAt(0, 0), map.cellAt(0, 1), map.cellAt(1, 1)));
    }


    @Test
    public void pathTransversedCellsReturnsTopRightCellAndRightNeighbour() {
        Map map = new Map(10, 5);
        assertThat(map.pathTransversedCells(pt(0.5, 0.5), pt(1.6, 1.5)), Matchers.containsInAnyOrder(map.cellAt(0, 0), map.cellAt(1, 0), map.cellAt(1, 1)));
    }


    @Test
    public void pathTransversedCellsReturnsTopLeftCellAndTopNeighbour() {
        Map map = new Map(10, 5);
        assertThat(map.pathTransversedCells(pt(1.5, 0.5), pt(0.5, 1.6)), Matchers.containsInAnyOrder(map.cellAt(1, 0), map.cellAt(0, 1), map.cellAt(1, 1)));
    }


    @Test
    public void pathTransversedCellsReturnsTopLeftCellAndLeftNeighbour() {
        Map map = new Map(10, 5);
        assertThat(map.pathTransversedCells(pt(1.5, 0.5), pt(0.4, 1.5)), Matchers.containsInAnyOrder(map.cellAt(0, 0), map.cellAt(1, 0), map.cellAt(0, 1)));
    }


    @Test
    public void pathTransversedCellsReturnsBottomLeftCellAndLeftNeighbour() {
        Map map = new Map(10, 5);
        assertThat(map.pathTransversedCells(pt(1.5, 1.5), pt(0.4, 0.5)), Matchers.containsInAnyOrder(map.cellAt(0, 1), map.cellAt(0, 0), map.cellAt(1, 1)));
    }


    @Test
    public void pathTransversedCellsReturnsBottomLeftCellAndBottomNeighbour() {
        Map map = new Map(10, 5);
        assertThat(map.pathTransversedCells(pt(1.5, 1.5), pt(0.6, 0.5)), Matchers.containsInAnyOrder(map.cellAt(1, 0), map.cellAt(0, 0), map.cellAt(1, 1)));
    }


    @Test
    public void pathTransversedCellsReturnsBottomRightCellAndRightNeighbour() {
        Map map = new Map(10, 5);
        assertThat(map.pathTransversedCells(pt(1.5, 1.5), pt(2.6, 0.5)), Matchers.containsInAnyOrder(map.cellAt(2, 0), map.cellAt(2, 1), map.cellAt(1, 1)));
    }


    @Test
    public void pathTransversedCellsReturnsBottomRightCellAndBottomNeighbour() {
        Map map = new Map(10, 5);
        assertThat(map.pathTransversedCells(pt(1.5, 1.5), pt(2.4, 0.5)), Matchers.containsInAnyOrder(map.cellAt(2, 0), map.cellAt(1, 1), map.cellAt(1, 0)));
    }


    @Test
    public void pathTransversedCellsDoesntReturnNeighbourCellIfDiagonalPathGoesThroughCorner() {
        Map map = new Map(10, 5);
        assertThat(map.pathTransversedCells(pt(1.5, 1.5), pt(0.5, 0.5)), Matchers.containsInAnyOrder(map.cellAt(1, 1), map.cellAt(0, 0)));
    }


    @Test
    public void pathTransversedCellsDoesntFallOutsideMap() {
        Map map = new Map(1, 2);

        // The first cell is (0, 0), but then the algorithm jumps to (1, 1), which doesn't exist
        // Test that the algorithm is smart enough to backtrack to (0, 1) and ignore (1, 1)
        assertThat(map.pathTransversedCells(pt(0.7, 0.99), pt(0.99, 1.1)), Matchers.containsInAnyOrder(map.cellAt(0, 0), map.cellAt(0, 1)));
    }


    @Test(expected=IndexOutOfBoundsException.class)
    public void pathTransversedCellsFailsWhenSourceIsOutsideMap() {
        Map map = new Map(10, 10);
        map.pathTransversedCells(new Point(100, 100), new Point(1, 1));
    }


    @Test(expected=IndexOutOfBoundsException.class)
    public void pathTransversedCellsFailsWhenTargetIsOutsideMap() {
        Map map = new Map(10, 10);
        map.pathTransversedCells(new Point(1, 1), new Point(100, 100));
    }


    /*
     * boolean isPathObstructed(Point source, Point target)
     */

    @Test
    public void isPathObstructedIsTrueIfSourceIsOutsideMap() {
        Map map = new Map(10, 10);
        assertThat(map.isPathObstructed(pt(-1, -1), pt(1, 1)), is(true));
    }


    @Test
    public void isPathObstructedIsTrueIfTargetIsOutsideMap() {
        Map map = new Map(10, 10);
        assertThat(map.isPathObstructed(pt(1, 1), pt(100, 100)), is(true));
    }


    @Test
    public void isPathObstructedIsTrueIfPathIsObstructedByAWall() {
        Map map = new Map(10, 10);
        map.addEntity(new Wall(new Point(5, 1), new Size(1, 8)));
        assertThat(map.isPathObstructed(pt(1, 1), pt(9, 9)), is(true));
    }


    @Test
    public void isPathObstructedIsFalseIsPathIsClear() {
        Map map = new Map(10, 10);
        assertThat(map.isPathObstructed(pt(1, 1), pt(9, 9)), is(false));
    }


    /*
     * Path<Point> findPath(Point source, Point target)
     */

    @Test
    public void findPathFindsAndReturnsPath() {
        Map map = new Map(10, 10);
        map.addEntity(new Wall(new Point(5, 1), new Size(1, 9)));
        map.buildNavGraph();
        assertThat(map.findPath(pt(1.5, 1.5), pt(9.5, 9.5)).getLocations(),
                containsInAnyOrder(pt(1.5, 1.5), pt(2.5, 1.5), pt(3.5, 1.5), pt(4.5, 0.5),
                        pt(5.5, 0.5), pt(6.5, 0.5), pt(7.5, 1.5), pt(8.5, 2.5), pt(9.5, 3.5),
                        pt(9.5, 4.5), pt(9.5, 5.5), pt(9.5, 6.5), pt(9.5, 7.5), pt(9.5, 8.5), pt(9.5, 9.5)));
    }


    /*
     * void buildNavGraph(Point seed)
     */

    @Test
    public void buildNavGraphBuildsNavGraph() {
        // ┌─────┐
        // │• X •│
        // │• • •│
        // └─────┘
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        Graph<Point, Double> expected = new Graph<>();
        expected.add(pt(0.5, 0.5));
        expected.add(pt(1.5, 0.5));
        expected.add(pt(2.5, 0.5));
        expected.add(pt(0.5, 1.5));
        expected.add(pt(2.5, 1.5));
        expected.addEdge(pt(0.5, 1.5), pt(1.5, 0.5), Math.sqrt(2));
        expected.addEdge(pt(0.5, 0.5), pt(0.5, 1.5), 1.0);
        expected.addEdge(pt(0.5, 0.5), pt(1.5, 0.5), 1.0);
        expected.addEdge(pt(1.5, 0.5), pt(2.5, 0.5), 1.0);
        expected.addEdge(pt(1.5, 0.5), pt(2.5, 1.5), Math.sqrt(2));
        expected.addEdge(pt(2.5, 0.5), pt(2.5, 1.5), 1.0);

        assertThat(expected, is(expected));
    }


    /*
     * Cell cellAt(int x, int y)
     */

    @Test(expected=IndexOutOfBoundsException.class)
    public void cellAtFailsIfXIsOutOfBounds() {
        Map map = new Map(3, 2);
        map.cellAt(4, 0);
    }


    @Test(expected=IndexOutOfBoundsException.class)
    public void cellAtFailsIfYIsOutOfBounds() {
        Map map = new Map(3, 2);
        map.cellAt(0, 3);
    }


    @Test
    public void cellAtReturnsCellAtLocation() {
        Map map = new Map(3, 2);
        assertThat(map.cellAt(0, 0).getCenter(), is(pt(0.5, 0.5)));
    }


    @Test
    public void cellAtReturnsCellAtMapBottomBorder() {
        Map map = new Map(3, 2);
        assertThat(map.cellAt(1, -1).getCenter(), is(pt(1.5, -0.5)));
    }


    @Test
    public void cellAtReturnsCellAtMapTopBorder() {
        Map map = new Map(3, 2);
        assertThat(map.cellAt(1, 2).getCenter(), is(pt(1.5, 2.5)));
    }


    @Test
    public void cellAtReturnsCellAtMapRightBorder() {
        Map map = new Map(3, 2);
        assertThat(map.cellAt(3, 1).getCenter(), is(pt(3.5, 1.5)));
    }


    @Test
    public void cellAtReturnsCellAtMapLeftBorder() {
        Map map = new Map(3, 2);
        assertThat(map.cellAt(-1, 2).getCenter(), is(pt(-0.5, 2.5)));
    }


    /*
     * Cell cellAt(Point point)
     */

    @Test(expected=IndexOutOfBoundsException.class)
    public void cellAtPointFailsIfXIsOutOfBounds() {
        Map map = new Map(3, 2);
        map.cellAt(pt(4, 0));
    }


    @Test(expected=IndexOutOfBoundsException.class)
    public void cellAtPointFailsIfYIsOutOfBounds() {
        Map map = new Map(3, 2);
        map.cellAt(pt(0, 3));
    }


    @Test
    public void cellAtPointReturnsCellAtPoint() {
        Map map = new Map(3, 2);
        assertThat(map.cellAt(pt(0, 0)).getCenter(), is(pt(0.5, 0.5)));
    }


    @Test
    public void cellAtPointReturnsCellUnderPoint() {
        Map map = new Map(3, 2);
        assertThat(map.cellAt(pt(0.5, 0.5)).getCenter(), is(pt(0.5, 0.5)));
    }


    @Test
    public void cellAtPointReturnsCellAtMapBottomBorder() {
        Map map = new Map(3, 2);
        assertThat(map.cellAt(pt(1.2, -0.8)).getCenter(), is(pt(1.5, -0.5)));
    }


    @Test
    public void cellAtPointReturnsCellAtMapTopBorder() {
        Map map = new Map(3, 2);
        assertThat(map.cellAt(pt(1.2, 2.3)).getCenter(), is(pt(1.5, 2.5)));
    }


    @Test
    public void cellAtPointReturnsCellAtMapRightBorder() {
        Map map = new Map(3, 2);
        assertThat(map.cellAt(pt(3.1, 1.7)).getCenter(), is(pt(3.5, 1.5)));
    }


    @Test
    public void cellAtPointReturnsCellAtMapLeftBorder() {
        Map map = new Map(3, 2);
        assertThat(map.cellAt(pt(-0.3, 2.1)).getCenter(), is(pt(-0.5, 2.5)));
    }


    /*
     * int width()
     */

    @Test
    public void widthReturnsTheMapWidth() {
        Map map = new Map(3, 2);
        assertThat(map.width(), is(3));
    }


    /*
     * int height()
     */

    @Test
    public void heightReturnsTheMapWidth() {
        Map map = new Map(3, 2);
        assertThat(map.height(), is(2));
    }


    /*
     * boolean isAreaClear(Point location, Size size)
     */

    @Test(expected=IndexOutOfBoundsException.class)
    public void isAreaClearFailsIfLocationIsOutsideMap() {
        Map map = new Map(3, 2);
        map.isAreaClear(pt(100, 100), new Size(1, 1));
    }


    @Test
    public void isAreaClearReturnsFalseIfRectangleGoesBeyondMap() {
        Map map = new Map(3, 2);

        // This doesn't fail because it hits the invisible wall around the map
        assertThat(map.isAreaClear(pt(1, 1), new Size(100, 1)), is(false));
    }


    @Test
    public void isAreaClearReturnsTrueIfAreaIsClear() {
        Map map = new Map(3, 2);
        assertThat(map.isAreaClear(pt(0, 0), new Size(3, 2)), is(true));
    }


    @Test
    public void isAreaClearReturnsFalseIfAreaIsOccupied() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(pt(1, 1), new Size(1, 1)));
        assertThat(map.isAreaClear(pt(0, 0), new Size(3, 2)), is(false));
    }


    @Test
    public void isAreaClearReturnsFalseIfAreaIncludesTheOuterBorder() {
        Map map = new Map(3, 2);
        assertThat(map.isAreaClear(pt(-1, -1), new Size(1, 1)), is(false));
        assertThat(map.isAreaClear(pt(3, 2), new Size(1, 1)), is(false));

    }


    /*
     * boolean isInside(Point point)
     */

    @Test
    public void isInsideReturnsFalseForPointToTheLeftOfTheMap() {
        Map map = new Map(3, 2);
        assertThat(map.isInside(pt(-1, -1)), is(false));
        assertThat(map.isInside(pt(-1, 0)), is(false));
        assertThat(map.isInside(pt(-1, 1)), is(false));
        assertThat(map.isInside(pt(-1, 2)), is(false));
        assertThat(map.isInside(pt(-100, 1)), is(false));
    }


    @Test
    public void isInsideReturnsFalseForPointToTheRightOfTheMap() {
        Map map = new Map(3, 2);
        assertThat(map.isInside(pt(3, -1)), is(false));
        assertThat(map.isInside(pt(3, 0)), is(false));
        assertThat(map.isInside(pt(3, 1)), is(false));
        assertThat(map.isInside(pt(3, 2)), is(false));
        assertThat(map.isInside(pt(100, 1)), is(false));
    }


    @Test
    public void isInsideReturnsFalseForPointAboveTheMap() {
        Map map = new Map(3, 2);
        assertThat(map.isInside(pt(-1, 2)), is(false));
        assertThat(map.isInside(pt(0, 2)), is(false));
        assertThat(map.isInside(pt(1, 2)), is(false));
        assertThat(map.isInside(pt(2, 2)), is(false));
        assertThat(map.isInside(pt(1, 100)), is(false));
    }


    @Test
    public void isInsideReturnsFalseForPointUnderTheMap() {
        Map map = new Map(3, 2);
        assertThat(map.isInside(pt(-1, -1)), is(false));
        assertThat(map.isInside(pt(0, -1)), is(false));
        assertThat(map.isInside(pt(1, -1)), is(false));
        assertThat(map.isInside(pt(2, -1)), is(false));
        assertThat(map.isInside(pt(1, -100)), is(false));
    }


    @Test
    public void isInsideReturnsTrueForPointInsideTheMap() {
        Map map = new Map(3, 2);
        assertThat(map.isInside(pt(0, 0)), is(true));
        assertThat(map.isInside(pt(1, 0)), is(true));
        assertThat(map.isInside(pt(2, 0)), is(true));
        assertThat(map.isInside(pt(0, 1)), is(true));
        assertThat(map.isInside(pt(1, 1)), is(true));
        assertThat(map.isInside(pt(2, 1)), is(true));
    }


    /*
     * boolean isCorner(Point point)
     */

    @Test
    public void aCornerIsACorner() {
        Map map = new Map(3, 2);
        assertThat(map.isCorner(pt(1.0, 3.0)), is(true));
    }


    @Test
    public void aPointStrictlyInsideTheCellIsNotACorner() {
        Map map = new Map(3, 2);
        assertThat(map.isCorner(pt(1.5, 3.2)), is(false));
    }


    @Test
    public void aPointStrictlyOnTheVerticalSideIsNotACorner() {
        Map map = new Map(3, 2);
        assertThat(map.isCorner(pt(1.0, 3.5)), is(false));
    }


    @Test
    public void aPointStrictlyOnTheHorizontalSideIsNotACorner() {
        Map map = new Map(3, 2);
        assertThat(map.isCorner(pt(1.5, 3.0)), is(false));
    }


    /*
     *
     * Map.Cell tests
     *
     */


    /*
     * void setEntity(StaticEntity entity)
     */

    @Test
    public void setEntitySetsEntity() {
        Map.Cell cell = new Map.Cell(pt(0, 0));
        Wall wall = new Wall(cell.getLocation(), new Size(1, 1));
        cell.setEntity(wall);
        assertThat(cell.getEntity(), is(wall));
    }


    @Test(expected=IllegalStateException.class)
    public void setEntityFailsIfCellAlreadyContainsAnEntity() {
        Map.Cell cell = new Map.Cell(pt(0, 0));
        cell.setEntity(new Wall(cell.getLocation(), new Size(1, 1)));
        cell.setEntity(new Wall(cell.getLocation(), new Size(1, 1)));
    }


    @Test(expected=IllegalStateException.class)
    public void setEntityFailsIfCellAlreadyContainsAnItem() {
        Map.Cell cell = new Map.Cell(pt(0, 0));
        cell.setItem(buildFirstAidItem(cell.getCenter()));
        cell.setEntity(new Wall(cell.getLocation(), new Size(1, 1)));
    }


    /*
     * boolean setItem(InventoryItem item)
     */

    @Test
    public void setItemSetsItem() {
        Map.Cell cell = new Map.Cell(pt(0, 0));
        FirstAidItem item = buildFirstAidItem(cell.getCenter());
        assertThat(cell.setItem(item), is(true));
        assertThat(cell.getItem(), is(item));
    }


    @Test
    public void setItemFailsIfCellAlreadyContainsAnEntity() {
        Map.Cell cell = new Map.Cell(pt(0, 0));
        cell.setEntity(new Wall(cell.getLocation(), new Size(1, 1)));
        assertThat(cell.setItem(buildWeaponItem(cell.getCenter(), Weapon.WeaponType.HANDGUN)), is(false));
        assertThat(cell.getItem(), is(nullValue()));
    }


    @Test
    public void setItemFailsIfCellAlreadyContainsAnItem() {
        Map.Cell cell = new Map.Cell(pt(0, 0));
        InventoryItem originalItem = buildFirstAidItem(cell.getCenter());
        cell.setItem(originalItem);
        assertThat(cell.setItem(buildWeaponItem(cell.getCenter(), Weapon.WeaponType.HANDGUN)), is(false));
        assertThat(cell.getItem(), is(originalItem));
    }


    /*
     * InventoryItem removeItem
     */

    @Test
    public void removeItemReturnsNullWhenThereIsNoItem() {
        Map.Cell cell = new Map.Cell(pt(0, 0));
        assertThat(cell.removeItem(), is(nullValue()));
    }


    @Test
    public void removeItemRemovesItem() {
        Map.Cell cell = new Map.Cell(pt(0, 0));
        InventoryItem item = buildFirstAidItem(cell.getCenter());
        cell.setItem(item);
        assertThat(cell.removeItem(), is(item));
    }


    /*
     * boolean isFree()
     */

    @Test
    public void isFreeReturnsTrueIfCellIsEmpty() {
        Map.Cell cell = new Map.Cell(pt(0, 0));
        assertThat(cell.isFree(), is(true));
    }


    @Test
    public void isFreeReturnsTrueIfCellContainsAnItem() {
        Map.Cell cell = new Map.Cell(pt(0, 0));
        InventoryItem item = buildFirstAidItem(cell.getCenter());
        cell.setItem(item);
        assertThat(cell.isFree(), is(true));
    }


    @Test
    public void isFreeReturnsFalseIfCellContainsAnEntity() {
        Map.Cell cell = new Map.Cell(pt(0, 0));
        cell.setEntity(new Wall(pt(0, 0), new Size(1, 1)));
        assertThat(cell.isFree(), is(false));
    }


    /*
     * Point getCenter()
     */

    @Test
    public void getCenterReturnsTheCellCenter() {
        Map.Cell cell = new Map.Cell(pt(0, 0));
        assertThat(cell.getCenter(), is(pt(0.5, 0.5)));
    }


        /*
     * Set<Point> getOccupyingCells()
     */

    @Test
    public void entityOccupiesSingleCell() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(5.5, 5.5), 0.3), containsInAnyOrder(pt(5, 5)));
    }


    @Test
    public void entityOccupiesNorth() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(5.5, 6.0), 0.3), containsInAnyOrder(pt(5, 5), pt(5, 6)));
    }


    @Test
    public void entityOccupiesSouthCell() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(5.5, 5.0), 0.3), containsInAnyOrder(pt(5, 5), pt(5, 4)));
    }


    @Test
    public void entityOccupiesWestCell() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(5.0, 5.5), 0.3), containsInAnyOrder(pt(5, 5), pt(4, 5)));
    }


    @Test
    public void entityOccupiesEastCell() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(6.0, 5.5), 0.3), containsInAnyOrder(pt(5, 5), pt(6, 5)));
    }


    @Test
    public void entityOccupiesNorthEastCell() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(6.0, 6.0), 0.3), containsInAnyOrder(pt(5, 5), pt(5, 6), pt(6, 5), pt(6, 6)));
    }


    @Test
    public void entityOccupiesNorthWestCell() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(5.0, 6.0), 0.3), containsInAnyOrder(pt(5, 5), pt(5, 6), pt(4, 5), pt(4, 6)));
    }


    @Test
    public void entityOccupiesSouthEastCell() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(5.0, 5.0), 0.3), containsInAnyOrder(pt(5, 5), pt(4, 5), pt(4, 4), pt(5, 4)));
    }


    @Test
    public void entityOccupiesSouthWestCell() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(6.0, 5.0), 0.3), containsInAnyOrder(pt(5, 5), pt(6, 4), pt(5, 4), pt(6, 5)));
    }


    @Test
    public void entityAlmostTouchesNorthBorder() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(5.5, 6.0 - 0.3 - 0.001), 0.3), containsInAnyOrder(pt(5, 5)));
    }


    @Test
    public void entityAlmostTouchesSouthBorder() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(5.5, 5.0 + 0.3 + 0.001), 0.3), containsInAnyOrder(pt(5, 5)));
    }


    @Test
    public void entityAlmostTouchesWestBorder() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(5.0 + 0.3 + 0.001, 5.5), 0.3), containsInAnyOrder(pt(5, 5)));
    }


    @Test
    public void entityAlmostTouchesEastBorder() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(6.0 - 0.3 - 0.001, 5.5), 0.3), containsInAnyOrder(pt(5, 5)));
    }


    @Test
    public void entityAlmostTouchesNorthEastCorner() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(6.0 - 0.3/Math.sqrt(2) - 0.001, 6.0 - 0.3/Math.sqrt(2) - 0.001), 0.3), containsInAnyOrder(pt(5, 5), pt(5, 6), pt(6, 5)));
    }


    @Test
    public void entityAlmostTouchesNorthWestCorner() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(5.0 + 0.3/Math.sqrt(2) + 0.001, 6.0 - 0.3/Math.sqrt(2) - 0.001), 0.3), containsInAnyOrder(pt(5, 5), pt(5, 6), pt(4, 5)));
    }


    @Test
    public void entityAlmostTouchesSouthEastCorner() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(6.0 - 0.3/Math.sqrt(2) - 0.001, 5.0 + 0.3/Math.sqrt(2) + 0.001), 0.3), containsInAnyOrder(pt(5, 5), pt(6, 5), pt(5, 4)));
    }


    @Test
    public void entityAlmostTouchesSouthWestCorner() {
        Map map = new Map(10, 10);
        assertThat(map.getOccupyingCells(pt(5.0 + 0.3/Math.sqrt(2) + 0.001, 5.0 + 0.3/Math.sqrt(2) + 0.001), 0.3), containsInAnyOrder(pt(5, 5), pt(5, 4), pt(4, 5)));
    }


    /*
     * Set<Point> getBoundingCircleSides(Point center, Vector direction, double boundingRadius)
     */

    @Test
    public void getBoundingCircleFrontAndSidesReturnsSidesWhenNotRotated() {
        Map map = new Map(10, 10);
        assertThat(map.getBoundingCircleSides(pt(0, 0), Direction.RIGHT.vector(), 0.3),
                containsInAnyOrder(pt(0, 0.3), pt(0, -0.3)));
    }


    @Test
    public void getBoundingCircleFrontAndSidesReturnsSidesWhenRotated() {
        Map map = new Map(10, 10);

        double dx = 0.3/Math.sqrt(2);
        assertThat(map.getBoundingCircleSides(pt(0, 0), Direction.BOTTOM_LEFT.vector(), 0.3),
                containsInAnyOrder(closeTo(pt(-dx, dx)),
                        closeTo(pt(dx, -dx))));
    }


    /*
     * boolean canMoveBetween(Point source, Point target)
     */

    @Test
    public void canMoveBetweenTheSamePoint() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        assertThat(map.canMoveBetween(pt(0.4, 0.6), pt(0.4, 0.6), config.botConfig().boundingRadius()), is(true));
    }

    @Test
    public void canMoveBetweenReturnsTrueWhenPathIsNotObstructed() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        assertThat(map.canMoveBetween(pt(0.4, 0.6), pt(2.6, 0.4), config.botConfig().boundingRadius()), is(true));
    }


    @Test
    public void canMoveBetweenReturnsFalseWhenLeftSideBumpsIntoWallDuringCourse() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        assertThat(map.canMoveBetween(pt(0.5, 0.8), pt(2, 0.8), config.botConfig().boundingRadius()), is(false));
    }


    @Test
    public void canMoveBetweenReturnsFalseWhenRightSideBumpsIntoWallDuringCourse() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        assertThat(map.canMoveBetween(pt(2, 0.8), pt(0.5, 0.8), config.botConfig().boundingRadius()), is(false));
    }


    @Test
    public void canMoveBetweenIgnoresCollisionsAtTheEnd() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        assertThat(map.canMoveBetween(pt(0.5, 0.9), pt(0.8, 0.9), config.botConfig().boundingRadius()), is(true));
    }


    /*
     * boolean canMoveTo(Point source, Point target, double boundingRadius)
     */

    @Test
    public void canMoveToReturnsTrueWhenPathIsNotObstructed() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        assertThat(map.canMoveTo(pt(0.4, 0.6), pt(2.6, 0.4), config.botConfig().boundingRadius()), is(true));
    }


    @Test
    public void canMoveToReturnsFalseWhenRightSideBumpsIntoWallDuringCourse() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        assertThat(map.canMoveTo(pt(2, 0.8), pt(0.5, 0.8), config.botConfig().boundingRadius()), is(false));
    }


    @Test
    public void canMoveBetweenReturnsFalseWhenBoundingCirclePenetratesWallAtTheEnd() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        assertThat(map.canMoveTo(pt(0.5, 0.9), pt(0.8, 0.9), config.botConfig().boundingRadius()), is(false));
    }
    
    
    /*
     * boolean canStayAt(Point location, double boundingRadius)
     */
    
    @Test
    public void canStayAtReturnsTrueWhenNotPenetratingAnyWall() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        assertThat(map.canStayAt(pt(0.5, 0.5), config.botConfig().boundingRadius()), is(true));
    }
    
    
    @Test
    public void canStayAtReturnsFalseInTheVicinityOfAWall() {
        Map map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        assertThat(map.canStayAt(pt(0.8, 1.5), config.botConfig().boundingRadius()), is(false));
    }


    /*
     * Point chooseRandomLocation()
     */

    @Test public void chooseRandomLocationChoosesARandomFreeLocation() {
        Map map = new Map(5, 5);

        // Only the last column is free
        map.addEntity(new Wall(pt(0, 0), new Size(4, 5)));

        Point random = map.chooseRandomLocation();
        assertThat(map.cellAt(random).isFree(), is(true));
        assertThat(random.x(), is(4.5));
        assertThat(random.y() - (int) random.y(), is(0.5));
    }
}
