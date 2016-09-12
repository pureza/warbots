package io.github.pureza.warbots.game;

import com.golden.gamedev.object.Background;
import com.golden.gamedev.object.background.TileBackground;
import com.golden.gamedev.util.ImageUtil;
import io.github.pureza.warbots.weaponry.Projectile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import io.github.pureza.warbots.collisions.CollisionHandler;
import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.entities.InventoryItem;
import io.github.pureza.warbots.entities.Team;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.geometry.Size;
import io.github.pureza.warbots.navigation.Map;
import io.github.pureza.warbots.navigation.Parser;
import io.github.pureza.warbots.resources.Sprites;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The main game class
 */
public class Game extends com.golden.gamedev.Game {

    /** The logger */
    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    /** Global game configuration */
    private final Config config;

    /** The game map */
    private final Map map;

    /** The game background */
    protected Background background;

    /** The collision handler */
    private final CollisionHandler collisionHandler = new CollisionHandler(this);

    /** The first team */
    private Team teamA;

    /** The second team */
    private Team teamB;

    /** The bots that are still alive */
    private final List<Bot> bots = new ArrayList<>();

    /** Projectiles currently travelling on the map */
    private final List<Projectile> projectiles = new ArrayList<>();

    /**
     * Projectiles that have hit a target and will be removed in the next
     * iteration.
     *
     * This is to avoid ConcurrentModificationExceptions
     */
    private final List<Projectile> lostProjectiles = new ArrayList<>();

    /**
     * Bots to remove in the next iteration
     * This is to avoid ConcurrentModificationExceptions
     */
    protected final Collection<Bot> zombies = new ArrayList<>();


    /**
     * Loads the game from the .map file definition
     *
     * Exits the application on failure.
     */
    public static Game load(Config config, String mapFile) {
        try {
            return new Parser(config).parse(mapFile);
        } catch (Exception e) {
            logger.error("An error occurred while parsing the .map file {}. Terminating...", mapFile, e);
            System.exit(1);
        }

        return null;
    }



    public Game(Config config, Map map, Team teamA, Team teamB) {
        super();
        this.config = config;
        this.map = map;
        this.teamA = teamA;
        this.teamB = teamB;
    }


    @Override
    public void initResources() {
        this.background = createBackground();

        // Initialize inventory item sprites
        for (InventoryItem item : map.getItems()) {
            item.loadSprite(this);
        }

        for (Bot bot : bots) {
            bot.initResources();
        }
    }


    @Override
    public void render(Graphics2D graphics) {
        // Draw the background
        background.render(graphics);

        // Draw the inventory items on top
        map.getItems().forEach(item -> item.render(graphics));

        // Draw the bots
        this.bots.forEach(bot -> bot.render(graphics));

        // Draw the projectiles
        this.projectiles.forEach(projectile -> projectile.render(graphics));
    }


    @Override
    public void update(long dt) {
        teamA.spawnBotIfNecessary(this);
        teamB.spawnBotIfNecessary(this);

        // Update the inventory first
        map.getItems().forEach(item -> item.update(dt));

        // Update the bots
        this.bots.forEach(bot -> bot.update(dt));

        // Update the projectiles
        this.projectiles.forEach(projectile -> projectile.update(dt));

        // Handle collisions
        this.collisionHandler.handle();;

        // Delete dead bots
        this.removeDeadBots();

        // Delete lost projectiles
        this.removeLostProjectiles();
    }


    /**
     * Returns the game map
     */
    public Map getMap() {
        return this.map;
    }


    /**
     * Returns the bots that are still alive
     *
     * It also returns zombie bots, that is, bots that died during the current
     * iteration which haven't been removed yet.
     */
    public List<Bot> getBots() {
        return Collections.unmodifiableList(bots);
    }


    /**
     * Adds a bot to the game
     */
    public void addBot(Bot bot) {
        this.bots.add(bot);
    }


    /**
     * Removes a bot from the world
     *
     * Doesn't actually remove the bot, just marks it as zombie.
     * The bot will be removed at the end of the current iteration, to avoid
     * ConcurrentModificationExceptions.
     */
    public void removeBot(Bot bot) {
        zombies.add(bot);
    }


    /**
     * Returns the bots inside the given circle
     */
    public List<Bot> getBotsInRange(Point center, double radius) {
        return this.bots.stream()
                .filter(bot -> center.distanceTo(bot.getLocation()) <= radius)
                .collect(Collectors.toList());
    }


    /**
     * Returns the projectiles currently travelling on the map
     */
    public List<Projectile> getProjectiles() {
        return this.projectiles;
    }


    /**
     * Adds a new projectile to the map
     */
    public void addProjectile(Projectile projectile) {
        this.projectiles.add(projectile);
    }


    /**
     * Removes a projectile from the world
     *
     * Doesn't actually remove the projectile, just marks it for deletion.
     * The projectile will be removed at the end of the current iteration, to
     * avoid ConcurrentModificationExceptions.
     */
    public void removeProjectile(Projectile projectile) {
        lostProjectiles.add(projectile);
    }


    /**
     * Returns the list of lost projectiles, i.e., the projectiles that have
     * hit an entity and must be removed from the game
     */
    public List<Projectile> getLostProjectiles() {
        return this.lostProjectiles;
    }


    /**
     * Returns the game global configuration
     */
    public Config getConfig() {
        return config;
    }


    public Team getTeamA() {
        return teamA;
    }


    public Team getTeamB() {
        return teamB;
    }


    /**
     * Loads a PNG with translucency support
     */
    public BufferedImage getImage(String filename) {
        BufferedImage result;
        if ((result = bsLoader.getStoredImage(filename)) == null) {
            URL framesUrl = bsIO.getURL(filename);
            result = ImageUtil.getImage(framesUrl, Transparency.TRANSLUCENT);
            bsLoader.storeImage(filename, result);
        }

        return result;
    }


    /**
     * Create the background with the walls and buildings, etc
     */
    private Background createBackground() {
        // Create and fill the image array
        BufferedImage[] tileImages = new BufferedImage[2];
        tileImages[0] = createFloorImage();
        tileImages[1] = super.getImage(Sprites.SPRITE_WALL_PATH);

        int[][] tiles = new int[map.width()][map.height()];

        // Set the tiles corresponding to occupied cells to 1
        for (int i = 0; i < map.height(); i++) {
            for (int j = 0; j < map.width(); j++) {
                if (!map.cellAt(j, i).isFree())
                    tiles[j][map.height() - i - 1] = 1;
            }
        }

        // Create the background
        return new TileBackground(tileImages, tiles);
    }


    /**
     * Remove the zombie bots
     *
     * Unlike removeBot(), this really removes them!
     */
    void removeDeadBots() {
        this.bots.removeAll(zombies);
        zombies.clear();
    }


    /**
     * Remove lost projectiles
     *
     * Unlike removeProjectile(), this really removes them!
     */
    void removeLostProjectiles() {
        this.projectiles.removeAll(lostProjectiles);
        lostProjectiles.clear();
    }


    /**
     * Creates a simple cell-width x cell-height gray filled image, to
     * be used as the floor
     */
    private BufferedImage createFloorImage() {
        Size cellSize = Sprites.CELL_SIZE;
        BufferedImage image = new BufferedImage(cellSize.width(), cellSize.height(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Draw the image
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fill(new Rectangle(cellSize.width(), cellSize.height()));
        g2d.dispose();
        return image;
    }
}
