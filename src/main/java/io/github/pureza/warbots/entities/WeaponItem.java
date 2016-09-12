package io.github.pureza.warbots.entities;

import com.golden.gamedev.Game;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.resources.Sprites;
import io.github.pureza.warbots.weaponry.Weapon;
import io.github.pureza.warbots.weaponry.WeaponBuilder;

import java.awt.image.BufferedImage;

/**
 * A weapon lying on the map
 */
public class WeaponItem extends InventoryItem {

    /** The weapon's type */
    private final Weapon.WeaponType weaponType;

    /** The weapon builder for this weapon */
    private final WeaponBuilder weaponBuilder;


    public WeaponItem(Point location, final Weapon.WeaponType weaponType, double boundingRadius, long activationInterval,
                      WeaponBuilder weaponBuilder) {
        super(location, boundingRadius, activationInterval);
        this.weaponType = weaponType;
        this.weaponBuilder = weaponBuilder;
    }


    @Override
    public void applyTo(Bot bot) {
        bot.acquireWeapon(weaponBuilder.build(bot));
    }


    @Override
    protected BufferedImage loadImage(Game game) {
        switch (this.weaponType) {
            case HANDGUN:
                return game.getImage(Sprites.SPRITE_HANDGUN_PATH);
            case LASER_GUN:
                return game.getImage(Sprites.SPRITE_LASER_GUN_PATH);
            case ROCKET_LAUNCHER:
                return game.getImage(Sprites.SPRITE_ROCKET_LAUNCHER_PATH);
            default:
                throw new IllegalArgumentException("Unexpected weapon: " + this.weaponType);
        }
    }


    public Weapon.WeaponType getWeaponType() {
        return weaponType;
    }


    @Override
    public String toString() {
        return "weapon";
    }
}
