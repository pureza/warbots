package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.entities.Bot;

/**
 * Instantiates and sets up weapons according to the global game configuration
 */
public interface WeaponBuilder {

    /**
     * Builds a weapon for the given bot
     */
    Weapon build(Bot owner);
}
