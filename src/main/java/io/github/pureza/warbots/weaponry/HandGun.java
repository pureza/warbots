package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.entities.Bot;
import io.github.pureza.warbots.fuzzy.RuleBuilder;

/**
 * A handgun is a weapon that fires bullets
 */
public class HandGun extends Weapon {


    HandGun(Bot agent, int initialAmmo, int maxAmmo, int fireRate, ProjectileBuilder projectileBuilder) {
        super(agent, initialAmmo, maxAmmo, fireRate, projectileBuilder);
    }


    @Override
    protected WeaponEvaluator initEvaluator() {
        return new WeaponEvaluator(this) {

            @Override
            protected void configure() {

                /*
                 * We prefer the hand gun when the opponent is far
                 */

                addRules(
                        RuleBuilder.when(distance.is(far))
                                .and(ammoStatus.is(loads))
                                .then(desirability.is(veryDesirable))
                                .toFuzzyRule(),

                        RuleBuilder.when(distance.is(far))
                                .and(ammoStatus.is(ok))
                                .then(desirability.is(veryDesirable))
                                .toFuzzyRule(),

                        RuleBuilder.when(distance.is(far))
                                .and(ammoStatus.is(low))
                                .then(desirability.is(desirable))
                                .toFuzzyRule(),

                        RuleBuilder.when(distance.is(medium))
                                .and(ammoStatus.is(loads))
                                .then(desirability.is(veryDesirable))
                                .toFuzzyRule(),

                        RuleBuilder.when(distance.is(medium))
                                .and(ammoStatus.is(ok))
                                .then(desirability.is(desirable))
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
                                .then(desirability.is(undesirable))
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
        return WeaponType.HANDGUN;
    }
}
