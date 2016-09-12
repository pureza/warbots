package io.github.pureza.warbots.resources;

import io.github.pureza.warbots.geometry.Size;

/**
 * Sprites used in this game
 */
public class Sprites {

    /**
     * This class cannot be instantiated
     */
    private Sprites() { }

    public static final Size CELL_SIZE = new Size(32, 32);

    public static final String SPRITE_WALL_PATH = "sprites/wall.gif";
    public static final String SPRITE_TEAM_A_PATH = "sprites/anakin.png";
    public static final String SPRITE_TEAM_B_PATH = "sprites/obiwan.png";
    public static final String SPRITE_LASER_GUN_PATH = "sprites/lasergun.png";
    public static final String SPRITE_LASER_RAY_PATH = "sprites/laser_ray.png";
    public static final String SPRITE_HANDGUN_PATH = "sprites/handgun.png";
    public static final String SPRITE_BULLET_PATH = "sprites/bullet.png";
    public static final String SPRITE_ROCKET_LAUNCHER_PATH = "sprites/rocket.png";
    public static final String SPRITE_HEALTH_PATH = "sprites/health.png";
}
