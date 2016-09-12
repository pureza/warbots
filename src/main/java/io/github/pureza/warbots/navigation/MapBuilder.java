package io.github.pureza.warbots.navigation;

import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.entities.WeaponItemBuilder;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.util.Pair;
import io.github.pureza.warbots.weaponry.Weapon;
import io.github.pureza.warbots.entities.FirstAidItemBuilder;
import io.github.pureza.warbots.entities.Wall;

import java.util.ArrayList;
import java.util.List;

/**
 * The Map Builder
 *
 * Builds maps according to the global game configuration
 */
public class MapBuilder {

    /** The game configuration */
    private final Config config;

    /** Map width */
    private Integer width;

    /** Map height */
    private Integer height;

    /** Map walls */
    private List<Wall> walls = new ArrayList<>();

    /** Location of first aid items in the map */
    private List<Point> firstAidItems = new ArrayList<>();

    /** Location of weapon items in the map */
    private List<Pair<Point, Weapon.WeaponType>> weaponItems = new ArrayList<>();


    public MapBuilder(Config config) {
        this.config = config;
    }


    /**
     * Sets the map size
     */
    public MapBuilder setDimension(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }


    /**
     * Adds a wall to this map
     */
    public MapBuilder addWall(Wall wall) {
        this.walls.add(wall);
        return this;
    }


    /**
     * Adds a first aid item to the map
     */
    public MapBuilder addFirstAidItem(Point location) {
        this.firstAidItems.add(location);
        return this;
    }


    /**
     * Aids a weapon item to the map
     */
    public MapBuilder addWeaponItem(Point location, Weapon.WeaponType weaponType) {
        this.weaponItems.add(Pair.of(location, weaponType));
        return this;
    }


    /**
     * Builds the map according to the given configuration
     */
    public Map build() {
        if (width == null || height == null) {
            throw new IllegalStateException("Missing width and/or height in map definition");
        }

        Map map = new Map(width, height);

        // Setup all the walls
        walls.forEach(map::addEntity);

        // Setup all the first aid items
        FirstAidItemBuilder firstAidItemBuilder = new FirstAidItemBuilder(config);
        firstAidItems.forEach(location -> map.addItem(firstAidItemBuilder.build(location)));

        // Setup all the weapon items
        WeaponItemBuilder weaponItemBuilder = new WeaponItemBuilder(config);
        weaponItems.forEach(pair -> {
            Point location = pair.first();
            Weapon.WeaponType weaponType = pair.second();
            map.addItem(weaponItemBuilder.build(location, weaponType));
        });

        // Build the navigational graph
        map.buildNavGraph();
        return map;
    }


    public Integer width() {
        return width;
    }


    public Integer height() {
        return height;
    }
}
