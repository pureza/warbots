package io.github.pureza.warbots;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.number.IsCloseTo;
import io.github.pureza.warbots.collisions.BotBotCollision;
import io.github.pureza.warbots.entities.Entity;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Vector;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;


public class Matchers {

    /** The tolerance for isClose Matchers */
    private static final double TOLERANCE = 0.001;


    /**
     * Private constructor, so this class can't be instantiated
     */
    private Matchers() {
    }


    /**
     * Checks if two doubles are close enough
     */
    public static Matcher<Double> closeTo(Double value) {

        return new TypeSafeMatcher<Double>() {

            @Override
            protected boolean matchesSafely(Double other) {
                return IsCloseTo.closeTo(value, TOLERANCE).matches(other);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a number close to ")
                        .appendValue(value);
            }
        };
    }


    /**
     * Checks if two points are close enough
     */
    public static Matcher<Point> closeTo(Point point) {

        return new TypeSafeMatcher<Point>() {

            @Override
            protected boolean matchesSafely(Point other) {
                return point.distanceTo(other) <= TOLERANCE;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a point close to ")
                        .appendValue(point);
            }
        };
    }


    /**
     * Checks if two vectors are close enough
     */
    public static Matcher<Vector> closeTo(Vector vector) {

        return new TypeSafeMatcher<Vector>() {

            @Override
            protected boolean matchesSafely(Vector other) {
                return vector.toPoint().distanceTo(other.toPoint()) <= TOLERANCE;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a vector close to ")
                        .appendValue(vector);
            }
        };
    }


    /**
     * Checks if two bot/bot collisions are close enough
     */
    public static Matcher<BotBotCollision> closeTo(BotBotCollision collision) {

        return new TypeSafeMatcher<BotBotCollision>() {

            @Override
            protected boolean matchesSafely(BotBotCollision other) {
                Set<Entity> collisionEntities = new HashSet<>(asList(collision.first(), collision.second()));
                Set<Entity> otherEntities = new HashSet<>(asList(other.first(), other.second()));

                return collisionEntities.equals(otherEntities);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a bot/bot collision close to ")
                        .appendValue(collision);
            }
        };
    }
}
