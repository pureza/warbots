package io.github.pureza.warbots.navigation;

import io.github.pureza.warbots.game.Game;
import org.junit.Before;
import org.junit.Test;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.Wall;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Size;
import io.github.pureza.warbots.search.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.hamcrest.core.Is.is;
import static io.github.pureza.warbots.Tests.buildBot;
import static io.github.pureza.warbots.Tests.mockGame;
import static io.github.pureza.warbots.Tests.mockTeam;
import static io.github.pureza.warbots.Matchers.closeTo;
import static io.github.pureza.warbots.geometry.Point.pt;

public class PathPlannerTest {

    private PathPlanner pathPlanner;

    private Map map;


    @Before
    public void setUp() {
        // ┌─────┐
        // │↓ X •│
        // │• • •│
        // └─────┘
        map = new Map(3, 2);
        map.addEntity(new Wall(new Point(1, 1), new Size(1, 1)));
        map.buildNavGraph();

        Game game = mockGame(map);
        Bot bot = buildBot(game, pt(0.4, 1.6), mockTeam());

        this.pathPlanner = new PathPlanner(bot);
    }

    /*
     * Path<Point> findPathTo(Point target)
     */

    @Test
    public void findPathToReturnsStraightLineIfPossible() {
        assertThat(pathPlanner.findPathTo(pt(0.4, 0.6)), is(new Path<>(pt(0.4, 1.6), pt(0.4, 0.6))));
    }


    @Test
    public void findPathToReturnsPathStartingAtSourceLocation() {
        assertThat(pathPlanner.findPathTo(pt(2.4, 1.4)).getSource(), is(pt(0.4, 1.6)));
    }


    @Test
    public void findPathToReturnsPathEndingAtTargetLocation() {
        assertThat(pathPlanner.findPathTo(pt(2.4, 1.4)).getTarget(), is(pt(2.4, 1.4)));
    }


    @Test
    public void findPathToSmoothsResultingPath() {
        assertThat(pathPlanner.findPathTo(pt(2.4, 1.4)).getLocations(), contains(pt(0.4, 1.6), pt(0.5, 0.5),
                pt(2.5, 0.5), pt(2.4, 1.4)));
    }


    /*
     * Path<Point> smoothPath(Path<Point> path)
     */

    @Test
    public void smoothPathDoesntModifyPathWithTwoPoints() {
        Path<Point> path = new Path<>(pt(0.4, 1.6), pt(0.6, 0.4));
        assertThat(pathPlanner.smoothPath(path), is(path));
    }


    @Test
    public void smoothPathRemovesUnneededIntermediatePoints() {
        Path<Point> originalPath = new Path<>(pt(0.4, 1.6), pt(0.5, 1.5), pt(0.5, 0.5), pt(1.5, 0.5), pt(2.5, 0.5),
                pt(2.5, 1.5), pt(2.4, 1.4));
        Path<Point> smoothedPath = new Path<>(pt(0.4, 1.6), pt(0.5, 0.5), pt(2.5, 0.5),
                pt(2.4, 1.4));
        assertThat(pathPlanner.smoothPath(originalPath), is(smoothedPath));
    }


    @Test
    public void smoothPathDoesntChangeArgument() {
        Path<Point> originalPath = new Path<>(pt(0.4, 1.6), pt(0.5, 1.5), pt(0.5, 0.5), pt(1.5, 0.5), pt(2.5, 0.5),
                pt(2.5, 1.5), pt(2.4, 1.4));
        pathPlanner.smoothPath(originalPath);
        assertThat(originalPath.size(), is(7));
    }
}
