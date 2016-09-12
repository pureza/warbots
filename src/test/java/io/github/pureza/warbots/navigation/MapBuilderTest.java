package io.github.pureza.warbots.navigation;

import io.github.pureza.warbots.TestConfig;
import io.github.pureza.warbots.entities.FirstAidItem;
import io.github.pureza.warbots.entities.Wall;
import io.github.pureza.warbots.entities.WeaponItem;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Size;
import io.github.pureza.warbots.search.Path;
import io.github.pureza.warbots.weaponry.Weapon;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class MapBuilderTest {

    /*
     * Map build()
     */

    @Test(expected=IllegalStateException.class)
    public void buildFailsIfDimensionsAreNotGiven() {
        MapBuilder builder = new MapBuilder(new TestConfig());
        builder.build();
    }


    @Test
    public void buildAddsWalls() {
        Wall wall1 = new Wall(Point.pt(1, 1), new Size(1, 1));
        Wall wall2 = new Wall(Point.pt(3, 1), new Size(2, 2));

        Map map = new MapBuilder(new TestConfig())
                .setDimension(5, 3)
                .addWall(wall1)
                .addWall(wall2)
                .build();

        assertThat(map.cellAt(wall1.getLocation()).getEntity(), is(wall1));
        assertThat(map.cellAt(wall2.getLocation()).getEntity(), is(wall2));
    }


    @Test
    public void buildAddsFirstAidItems() {
        Map map = new MapBuilder(new TestConfig())
                .setDimension(5, 3)
                .addFirstAidItem(Point.pt(1.5, 1.5))
                .addFirstAidItem(Point.pt(2.5, 2.5))
                .build();

        assertThat(map.cellAt(Point.pt(1.5, 1.5)).getItem(), is(instanceOf(FirstAidItem.class)));
        assertThat(map.cellAt(Point.pt(2.5, 2.5)).getItem(), is(instanceOf(FirstAidItem.class)));
    }


    @Test
    public void buildAddsWeaponItems() {
        Map map = new MapBuilder(new TestConfig())
                .setDimension(5, 3)
                .addWeaponItem(Point.pt(1.5, 1.5), Weapon.WeaponType.HANDGUN)
                .addWeaponItem(Point.pt(2.5, 2.5), Weapon.WeaponType.LASER_GUN)
                .build();

        assertThat(((WeaponItem) map.cellAt(Point.pt(1.5, 1.5)).getItem()).getWeaponType(), is(Weapon.WeaponType.HANDGUN));
        assertThat(((WeaponItem) map.cellAt(Point.pt(2.5, 2.5)).getItem()).getWeaponType(), is(Weapon.WeaponType.LASER_GUN));
    }


    @Test
    public void buildSetsUpNavGraph() {
        Map map = new MapBuilder(new TestConfig())
                .setDimension(5, 3)
                .build();

        assertThat(map.findPath(Point.pt(0.5,0.5), Point.pt(3.5, 2.5)), is(instanceOf(Path.class)));
    }
}
