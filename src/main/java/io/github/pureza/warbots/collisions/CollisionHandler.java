package io.github.pureza.warbots.collisions;

import io.github.pureza.warbots.game.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Resolves collisions between Bots, Walls, Projectiles and Inventory Items
 *
 * Detecting and resolving collisions is a very visual matter: it's all about
 * the final effect it provokes under the scrutiny of the human eyes. It's
 * perfectly acceptable to allow some amount of overlap if it remains (mostly)
 * undetected by the user. However, we must have some guarantees that the
 * system works well enough. In this game, we took the following approach:
 *
 * - Collisions between bots and walls are completely forbidden. The system
 *   tries as hard as it can to resolve them all and, when the bots are
 *   drawn on the screen, there can't be any penetrating walls
 *
 * - Collisions between bots and other bots are handled, but it is possible
 *   that, at the end of a cycle, there could still be some collisions.
 *   This is essentially unavoidable because by handling a collision we have
 *   to move a bot, and this movement could create a new collision with
 *   another bot or wall
 *
 * - Other types of collisions are handled normally
 */
public class CollisionHandler {

    /** The collision detector */
    private final CollisionDetector detector;

    /** The logger */
    private final Logger logger = LoggerFactory.getLogger(getClass());


    public CollisionHandler(Game game) {
        this.detector = new CollisionDetector(game);
    }


    public void handle() {
        // First, take care of bot/bot collisions because they may generate
        // bot/wall collisions, and we must get ride of those
        handleBotBotCollisions();

        // XXX At this point there may still be some bot/bot collisions,
        // XXX but this is acceptable

        // Now, handle bot/wall collisions by moving the bots a little. This
        // may generate new bot/bot collisions, but this is also acceptable
        handleBotWallCollisions(2);

        // Let the bots acquire the items
        handleBotItemCollisions();

        // Remove projectiles that have hit walls
        handleProjectileWallCollisions();

        // Handle bots that have been hit by projectiles
        // This is done last because the other handlers may move the bots and
        // we want to make sure the projectile really hits them
        // Also, we let a bot catch a first aid kit before being hit, otherwise
        // it could die with positive health!
        handleBotProjectileCollisions();

        // At this point, there must not be any bot/wall collisions
        assert noBotWallCollisions();
    }


    /**
     * Handles all bot/bot collisions
     */
    void handleBotBotCollisions() {
        List<Collision> collisions = detector.detectBotBotCollisions();

        if (!collisions.isEmpty()) {
            logger.debug("Found {} Bot/Bot collisions", collisions.size());

            for (Collision collision : collisions) {
                logger.debug("Handling {}...", collision);
                collision.handle();
            }
        }
    }


    /**
     * Handles all bot/wall collisions
     *
     * In some very rare cases, fixing a bot/wall collision may generate a
     * new collision with a different wall. This new collision must be
     * taken care of as well, and that's why this method works iteratively,
     * until there are no more collisions or a maximum number of iterations
     * has been reached.
     */
    void handleBotWallCollisions(int maxTries) {
        for (int i = 0; i < maxTries; i++) {
            List<Collision> collisions = detector.detectBotWallCollisions();

            if (collisions.isEmpty()) {
                break;
            } else {
                logger.debug("Found {} Bot/Wall collisions", collisions.size());

                for (Collision collision : collisions) {
                    logger.debug("Handling {}...", collision);
                    collision.handle();
                }
            }
        }
    }


    /**
     * Handle bot/item collisions
     */
    private void handleBotItemCollisions() {
        List<Collision> collisions = detector.detectBotItemCollisions();
        for (Collision collision : collisions) {
            logger.debug("Handling {}...", collision);
            collision.handle();
        }
    }


    /**
     * Handle projectile/wall collisions
     */
    private void handleProjectileWallCollisions() {
        List<Collision> collisions = detector.detectProjectileWallCollisions();
        for (Collision collision : collisions) {
            logger.debug("Handling {}...", collision);
            collision.handle();
        }
    }


    /**
     * Handle bot/projectile collisions
     */
    private void handleBotProjectileCollisions() {
        List<Collision> collisions = detector.detectBotProjectileCollisions();
        for (Collision collision : collisions) {
            logger.debug("Handling {}...", collision);
            collision.handle();
        }
    }


    /**
     * Checks if there are still any bot/wall collisions
     */
    private boolean noBotWallCollisions() {
        List<Collision> botWallCollisions = detector.detectBotWallCollisions();

        if (botWallCollisions.isEmpty()) {
            return true;
        } else {
            logger.error("There can't be any Bot/Wall collisions at this point, but I found {}", botWallCollisions);
            return false;
        }
    }
}
