package io.github.pureza.warbots.game;

import com.golden.gamedev.GameLoader;
import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.config.ConfigLoader;
import io.github.pureza.warbots.navigation.Map;

import java.awt.*;
import java.io.IOException;


/**
 * Where it all begins...
 */
public class App {

    public static void main(String[] args) throws IOException {

        // Validate command line arguments
        if (args.length != 1) {
            System.err.println("No map specified! Check some examples in the maps/ directory.");
            System.err.println("Usage: warbots <map file>");
            System.exit(1);
        }

        // The map to display
        String mapFile = args[0];

        // Initialize the game
        Config config = ConfigLoader.load();
        Game game = Game.load(config, mapFile);
        Map map = game.getMap();

        // Initialize the Golden T game engine.
        GameLoader loader = new GameLoader();
        loader.setup(game, new Dimension(map.width() * 32, map.height() * 32), false);
        loader.start();
    }
}
