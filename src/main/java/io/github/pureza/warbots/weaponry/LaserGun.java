package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.fuzzy.RuleBuilder;
import io.github.pureza.warbots.entities.Bot;

/**
 * A laser gun is a weapon that fires laser rays.
 */
public class LaserGun extends Weapon {


    LaserGun(Bot agent, int initialAmmo, int maxAmmo, int fireRate, ProjectileBuilder projectileBuilder) {
        super(agent, initialAmmo, maxAmmo, fireRate, projectileBuilder);
    }


    @Override
    protected WeaponEvaluator initEvaluator() {
        return new WeaponEvaluator(this) {

            @Override
            protected void configure() {

                /*
                 * We mostly prefer the lasergun at medium range
                 */

                addRules(
                        RuleBuilder.when(distance.is(far))
                                .and(ammoStatus.is(loads))
                                .then(desirability.is(desirable))
                                .toFuzzyRule(),

                        RuleBuilder.when(distance.is(far))
                                .and(ammoStatus.is(ok))
                                .then(desirability.is(undesirable))
                                .toFuzzyRule(),

                        RuleBuilder.when(distance.is(far))
                                .and(ammoStatus.is(low))
                                .then(desirability.is(undesirable))
                                .toFuzzyRule(),

                        RuleBuilder.when(distance.is(medium))
                                .and(ammoStatus.is(loads))
                                .then(desirability.is(veryDesirable))
                                .toFuzzyRule(),

                        RuleBuilder.when(distance.is(medium))
                                .and(ammoStatus.is(ok))
                                .then(desirability.is(veryDesirable))
                                .toFuzzyRule(),

                        RuleBuilder.when(distance.is(medium))
                                .and(ammoStatus.is(low))
                                .then(desirability.is(undesirable))
                                .toFuzzyRule(),

                        RuleBuilder.when(distance.is(close))
                                .and(ammoStatus.is(loads))
                                .then(desirability.is(desirable))
                                .toFuzzyRule(),

                        RuleBuilder.when(distance.is(close))
                                .and(ammoStatus.is(ok))
                                .then(desirability.is(desirable))
                                .toFuzzyRule(),

                        RuleBuilder.when(distance.is(close))
                                .and(ammoStatus.is(low))
                                .then(desirability.is(undesirable))
                                .toFuzzyRule());
            }
        };
    }


    @Override
    protected WeaponType getWeaponType() {
        return WeaponType.LASER_GUN;
    }
}
