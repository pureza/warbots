package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.geometry.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.StaticEntity;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.navigation.Map;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static io.github.pureza.warbots.geometry.Circle.circle;

/**
 * A collision between a bot and a wall
 *
 * Bot/Wall collisions are handled by backing up the bot a little bit until it
 * is just a tiny distance away from touching the wall.
 *
 * The resolution of a Bot/Wall collision may generate new collisions simply
 * because the bot is moved.
 */
public class BotWallCollision extends Collision {

    /** The location of the bot */
    private final Point location;

    /** The cell it collided with */
    private final Point cell;

    /** The logger */
    private final Logger logger = LoggerFactory.getLogger(getClass());


    public BotWallCollision(Bot bot, StaticEntity wall, Point location, Point cell) {
        super(bot, wall);
        this.location = location;
        this.cell = cell;
    }


    @Override
    public void handle() {
        Bot bot = (Bot) first;
        StaticEntity staticEntity = (StaticEntity) second();

        // The bot went from A to B. At A everything was fine, but at B it is
        // penetrating a wall. Find out the point between A and B where the
        // bot is touching the wall but not penetrating it, and move it there!

        // Skip if the bot has already been moved by another collision handler
        // that fixed this collision
        if (!location.equals(bot.getLocation()) && !bot.getOccupyingCells().contains(cell)) {
            return;
        }

        // The bot was not penetrating any wall before moving
        assert (botWasntPenetratingAnyWallBefore(bot));

        Point center = bot.getPreviousLocation();
        double boundingRadius = bot.getBoundingRadius();
        Vector heading = bot.getLocation().minus(bot.getPreviousLocation());

        // The bot must have moved!
        assert (!heading.isNull()) : "The bot didn't move!";

        // Find the point of the path where the bounding circle touches the wall
        Point touchingCenter = circle(center, boundingRadius).centerWhenTouchingCell(heading, cell);

        // There must be a touching point!
        assert (touchingCenter != null) : "The bot is colliding with a wall but I can't find the touching point";

        // Move the touching center just a tiny wee bit to make sure it
        // doesn't penetrate the wall
        touchingCenter = touchingCenter.plus(heading.normalize().scalarMul(-0.00001));

        logger.debug("Moved {} from {} to {}", bot, bot.getLocation(), touchingCenter);

        // Restore the previous location, because we might need if there are
        // multiple collisions to handle
        bot.restoreLocation();

        // XXX This may generate new collisions. Most of them will be ignored,
        // XXX but new Bot/Wall collisions must be detected and handled!
        bot.setLocation(touchingCenter);

        // Fire the corresponding event
        bot.onCollisionWithStaticEntity().fire(staticEntity);

        // At this point, the bot must not be penetrating this cell anymore
        // (although it could still be penetrating others)
        assert (botIsNotPenetratingThisWallAnymore(bot))
                : "After being moved, the bot is still colliding with the cell " + cell + " location: " + bot.getLocation();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BotWallCollision collision = (BotWallCollision) o;
        return Objects.equals(first, collision.first) &&
                Objects.equals(second, collision.second) &&
                Objects.equals(cell, collision.cell);
    }


    @Override
    public int hashCode() {
        return Objects.hash(first, second, cell);
    }


    @Override
    public String toString() {
        return "BotWallCollision{" +
                "bot=" + first +
                "location=" + location +
                ", cell=" + cell +
                "}";
    }


    /**
     * Check if the bot was already penetrating a wall before
     *
     * This can only happen due to a bug.
     */
    private boolean botWasntPenetratingAnyWallBefore(Bot bot) {
        Point center = bot.getPreviousLocation();
        double boundingRadius = bot.getBoundingRadius();

        Map map = bot.getGame().getMap();

        Set<Point> before = map.getOccupyingCells(center, boundingRadius).stream()
                .filter(cell -> !map.cellAt(cell).isFree())
                .collect(Collectors.toSet());

        if (before.isEmpty()) {
            return true;
        } else {
            logger.error("Bot {} was already penetrating cell(s) {} at its previous location {}", bot,
                    before, center);
            return false;
        }
    }


    /**
     * Checks if the bot is not penetrating the wall anymore
     */
    private boolean botIsNotPenetratingThisWallAnymore(Bot bot) {
        if (bot.getOccupyingCells().contains(cell)) {
            return false;
        } else {
            return true;
        }
    }
}

