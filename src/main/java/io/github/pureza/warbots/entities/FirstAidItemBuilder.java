package io.github.pureza.warbots.entities;

import io.github.pureza.warbots.config.Config;
import io.github.pureza.warbots.config.FirstAidItemConfig;
import io.github.pureza.warbots.geometry.Point;

/**
 * Builder for first aid items
 *
 * Builds first aid items according to the global game configuration.
 */
public class FirstAidItemBuilder {

    /** The game configuration */
    private final Config config;


    public FirstAidItemBuilder(Config config) {
        this.config = config;
    }


    /**
     * Builds a first aid item at the given location
     */
    public FirstAidItem build(Point location) {
        FirstAidItemConfig cfg = config.firstAidItemConfig();
        return new FirstAidItem(location, cfg.boundingRadius(), cfg.activationInterval(), cfg.healthAmount());
    }
}
