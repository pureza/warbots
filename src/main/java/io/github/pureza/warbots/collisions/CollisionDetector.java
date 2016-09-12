package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.InventoryItem;
import io.github.pureza.warbots.game.Game;
import io.github.pureza.warbots.geometry.Circle;
import io.github.pureza.warbots.geometry.Line;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Vector;
import io.github.pureza.warbots.navigation.Map;
import io.github.pureza.warbots.weaponry.Projectile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Detects all kinds of collisions
 */
public class CollisionDetector {

    /** The game */
    private Game game;


    public CollisionDetector(Game game) {
        this.game = game;
    }



    /**
     * Detect collisions between bots
     *
     * Two bots collide if their bounding circles intersect.
     */
    List<Collision> detectBotBotCollisions() {
        List<Collision> collisions = new ArrayList<>();
        List<Bot> bots = this.game.getBots();
        for (int i = 0; i < bots.size() - 1; i++) {
            for (int j = i + 1; j < bots.size(); j++) {
                Bot bot = bots.get(i);
                Bot other = bots.get(j);

                Vector toOther = other.getLocation().minus(bot.getLocation());
                double distance = toOther.norm();

                // If there is an overlap, there is a collision
                double overlapAmount = bot.getBoundingRadius() + other.getBoundingRadius() - distance;
                if (overlapAmount > 0) {
                    collisions.add(new BotBotCollision(bot, other));
                }
            }
        }

        return collisions;
    }


    /**
     * Detect collisions between bots and inventory items
     *
     * Since inventory items are always placed at the center of their cell, we
     * just need to check the cell the bot is currently at.
     */
    List<Collision> detectBotItemCollisions() {
        List<Collision> collisions = new ArrayList<>();

        for (Bot bot : this.game.getBots()) {
            Map.Cell cell = game.getMap().cellAt(bot.getLocation());
            InventoryItem item = cell.getItem();
            if (item != null && item.isActive()) {
                double distance = bot.getLocation().distanceTo(cell.getCenter());
                double overlapAmount = bot.getBoundingRadius() + item.getBoundingRadius() - distance;
                if (overlapAmount > 0) {
                    collisions.add(new BotItemCollision(bot, item));
                }
            }
        }

        return collisions;
    }


    /**
     * Detect collisions between bots and walls
     */
    List<Collision> detectBotWallCollisions() {
        List<Collision> collisions = new ArrayList<>();

        for (Bot bot : game.getBots()) {
            Set<Point> occupied = bot.getOccupyingCells();
            collisions.addAll(occupied.stream()
                    .map(center -> game.getMap().cellAt(center))
                    .filter(cell -> !cell.isFree())
                    .map(cell -> new BotWallCollision(bot, cell.getEntity(), bot.getLocation(), cell.getLocation()))
                    .collect(Collectors.toList()));
        }

        return collisions;
    }


    /**
     * Detect collisions between projectiles and walls
     *
     * It only checks if cell where the projectile is currently at is occupied
     * by a wall. If the projectile travels fast enough, it may cross the wall
     * unscathed. Increasing the accuracy would decrease performance, so I
     * think this is an acceptable compromise.
     *
     * TODO We only care about the first collision for each projectile...
     */
    List<Collision> detectProjectileWallCollisions() {
        List<Collision> collisions = new ArrayList<>();

        for (Projectile projectile : this.game.getProjectiles()) {
            if (!game.getMap().isInside(projectile.getLocation())) {
                // The projectile has left the map
                // Just remove it from the world
                game.removeProjectile(projectile);
            } else {
                // The projectile is still inside the map
                Set<Point> occupied = projectile.getOccupyingCells();
                collisions.addAll(occupied.stream()
                        .map(center -> game.getMap().cellAt(center))
                        .filter(cell -> !cell.isFree())
                        .map(cell -> new ProjectileWallCollision(projectile, cell.getEntity()))
                        .collect(Collectors.toList()));
            }
        }

        return collisions;
    }


    /**
     * Detect collisions between bots and projectiles
     *
     * A projectile collides with a bot when the path segment it travelled
     * during the last update intersects with the bot's bounding circle.
     * It is possible that the path intersects with multiple bots, in which
     * case we care only about the first (i.e., the one closest to the
     * projectile's source position).
     */
    List<Collision> detectBotProjectileCollisions() {
        List<Collision> collisions = new ArrayList<>();

        for (Projectile projectile : game.getProjectiles()) {
            Point source = projectile.getPreviousLocation();

            // The projectile's trajectory
            Line trajectory = new Line(source, projectile.getHeadingVector());

            Bot hitBot = game.getBots().stream()
                    .filter(bot -> {
                        // The bot must be in front of the projectile
                        Vector toBot = bot.getLocation().minus(source);
                        return projectile.getHeadingVector().dot(toBot) > 0;
                    })
                    .flatMap(bot -> {
                        // Calculate the points of collision between the projectile's trajectory
                        // and the bot's bounding circle
                        Set<Point> collisionPoints = trajectory.intersectionWithCircle(Circle.circle(bot.getLocation(), bot.getBoundingRadius()));

                        // Save this collision point as a candidate for further analysis
                        return collisionPoints.stream()
                                .map(pt -> new BotProjectileCollisionCandidate(bot, projectile, pt));
                    })
                    .min((a, b) -> {
                        // Keep only the bot that is closest to the projectile
                        return Double.compare(a.distance(), b.distance());
                    })
                    .filter(botDistance -> {
                        // The distance covered by the projectile during the last update()
                        double coveredDistance = source.distanceTo(projectile.getLocation());

                        // Make sure the bot was really hit
                        return botDistance.distance() < coveredDistance;
                    })
                    .filter(botDistance -> {
                        // Make sure there was no obstacle between the projectile and the bot
                        return !game.getMap().isPathObstructed(source, botDistance.intersectionPoint());
                    })
                    .map(BotProjectileCollisionCandidate::bot)
                    .orElse(null);

            if (hitBot != null) {
                collisions.add(new BotProjectileCollision(hitBot, projectile));
            }
        }

        return collisions;
    }
}


/**
 * Helper class that some basic details of a potential collision between
 * a projectile and a bot
 */
class BotProjectileCollisionCandidate {

    /** The bot */
    private final Bot bot;

    /** The point of intersection between the projectile and the bot */
    private final Point intersectionPoint;

    /** Distance between the bot and the projectile */
    private final double distance;


    public BotProjectileCollisionCandidate(Bot bot, Projectile projectile, Point intersectionPoint) {
        this.bot = bot;
        this.intersectionPoint = intersectionPoint;
        this.distance = projectile.getPreviousLocation().distanceTo(intersectionPoint);
    }


    public Bot bot() {
        return bot;
    }


    public Point intersectionPoint() {
        return intersectionPoint;
    }


    public double distance() {
        return distance;
    }
}