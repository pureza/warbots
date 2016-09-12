package io.github.pureza.warbots.entities;

import com.golden.gamedev.Game;
import io.github.pureza.warbots.geometry.Point;
import io.github.pureza.warbots.resources.Sprites;

import java.awt.image.BufferedImage;

/**
 * A first aid kit
 */
public class FirstAidItem extends InventoryItem {

    /** How much health is inside the kit? */
    private final int healthAmount;


    public FirstAidItem(Point location, double boundingRadius, long activationInterval, int healthAmount) {
        super(location, boundingRadius, activationInterval);
        this.healthAmount = healthAmount;
    }


    @Override
    public BufferedImage loadImage(Game game) {
        return game.getImage(Sprites.SPRITE_HEALTH_PATH);
    }


    @Override
    protected void applyTo(Bot bot) {
        bot.acquireHealth(healthAmount);
    }


    public int getHealthAmount() {
        return healthAmount;
    }


    @Override
    public String toString() {
        return "health";
    }
}
