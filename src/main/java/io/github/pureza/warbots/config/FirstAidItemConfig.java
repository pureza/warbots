package io.github.pureza.warbots.config;

public interface FirstAidItemConfig {

    /**
     * First aid item's bounding radius
     */
    double boundingRadius();


    /**
     * First aid item's activation interval
     */
    long activationInterval();


    /**
     * The amount of health contained in a first aid item
     */
    int healthAmount();
}
