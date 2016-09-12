package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.geometry.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Bot's artillery
 *
 * The artillery is basically the set of weapons an agent carries.
 *
 * This class also implements the logic related to acquiring weapons or
 * projectiles and even some of the higher-level logic of choosing the weapon
 * to use or firing at the enemy.
 */
public class Artillery {

    /** The owner */
    private Bot owner;

    /** Weapons currently in possession of the bot */
    private Map<Weapon.WeaponType, Weapon> weapons = new HashMap<>();

    /** The currently active weapon */
    private Weapon currentWeapon;


    public Artillery(Bot owner) {
        this.owner = owner;
    }


    /**
     * Acquires a weapon
     *
     * If the bot is already carrying one of these, acquire
     * only the available ammunition (up to the maximum limit).
     */
    public void acquire(Weapon weapon) {
        if (this.weapons.containsKey(weapon.getWeaponType())) {
            this.weapons.get(weapon.getWeaponType()).acquireAmmo(weapon.getRemainingAmmo());
        } else {
            this.weapons.put(weapon.getWeaponType(), weapon);
            this.currentWeapon = weapon;
        }
    }


    /**
     * Averages the sum of each weapon's individual strength to obtain the total
     * "weapon's strength" of this bot. This number is then used by the planning
     * layer to decide if the carrier should attack or retreat
     */
    public double totalWeaponStrength() {
        double totalStrength = weapons.values()
                .stream()
                .mapToDouble(Weapon::individualWeaponStrength)
                .sum();

        // Average by the total number of weapons, not just the number of
        // weapons in possession
        return totalStrength / Weapon.WeaponType.values().length;
    }


    /**
     * Fires against an enemy and returns the projectile
     */
    public Projectile fireAt(Bot enemy, long dt) {
        Vector toEnemy = enemy.getLocation().minus(owner.getLocation());

        if (owner.rotateFacing(toEnemy, dt)) {
            this.currentWeapon = this.chooseWeapon(enemy);
            return this.currentWeapon.fire();
        }

        return null;
    }


    /**
     * Chooses which weapon to use against the specified enemy
     */
    Weapon chooseWeapon(Bot other) {
        double distance = owner.getLocation().distanceTo(other.getLocation());
        return weapons.values()
                .stream()
                .max((a, b) -> Double.compare(a.desirability(distance), b.desirability(distance)))
                .get();
    }


    public Map<Weapon.WeaponType, Weapon> getWeapons() {
        return weapons;
    }


    public Weapon getCurrentWeapon() {
        return currentWeapon;
    }
}
