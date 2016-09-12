package io.github.pureza.warbots.game;

import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.entities.Team;
import io.github.pureza.warbots.navigation.Map;
import io.github.pureza.warbots.navigation.MapBuilder;

/**
 * Builder for the game entities
 */
public class GameBuilder {

    /** The game configuration */
    private final Config config;

    public GameBuilder(Config config) {
        this.config = config;
    }


    /**
     * Builds a new game instance with the given map
     */
    public Game build(Map map, Team teamA, Team teamB) {
        return new Game(config, map, teamA, teamB);
    }


    /**
     * Returns a MapBuilder to build maps according to the game configuration
     */
    public MapBuilder mapBuilder() {
        return new MapBuilder(config);
    }
}
