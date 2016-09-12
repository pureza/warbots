package io.github.pureza.warbots.weaponry;

import io.github.pureza.warbots.fuzzy.Term;
import io.github.pureza.warbots.fuzzy.Variable;
import nrc.fuzzy.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Evaluator for weapon selection using fuzzy logic
 *
 * Each weapon has its own evaluator with rules to calculate its desirability
 * taking variables such as ammunition left and distance to opponent into
 * consideration.
 */
public abstract class WeaponEvaluator {

    /** Fuzzy rules */
    private List<FuzzyRule> rules = new LinkedList<>();

    /** Distance fuzzy variable */
    protected Variable distance;

    /** Close distance term */
    protected Term close;

    /** Medium distance term */
    protected Term medium;

    /** Far distance term */
    protected Term far;

    /** Desirability fuzzy variable */
    protected Variable desirability;

    /** Undesirable term */
    protected Term undesirable;

    /** Desirable term */
    protected Term desirable;

    /** Very desirable term */
    protected Term veryDesirable;

    /** Ammunition status fuzzy variable */
    protected Variable ammoStatus;

    /** Low ammunition */
    protected Term low;

    /** Ok ammunition */
    protected Term ok;

    /** Loads of ammunition */
    protected Term loads;

    /** The logger */
    private final Logger logger = LoggerFactory.getLogger(getClass());


    public WeaponEvaluator(Weapon weapon) {
        initializeVariables(weapon);
        configure();
    }


    /**
     * Evaluates the rules for some input values
     */
    public double evaluate(double dist, int ammo) {
        try {
            final double delta = 0.05;
            FuzzyValue result = null;

            FuzzyValue inputDistance = new FuzzyValue(distance.fuzzyVariable(),
                    new TriangleFuzzySet(Math.max(dist - delta, 0.0), dist, Math.min(dist + delta, 50)));

            FuzzyValue inputAmmo = new FuzzyValue(ammoStatus.fuzzyVariable(),
                    new TriangleFuzzySet(ammo - delta, ammo, ammo + delta));

            // Call each rule and add the result to the resulting value
            for (FuzzyRule rule : this.rules) {
                rule.removeAllInputs();
                rule.addInput(inputDistance);
                rule.addInput(inputAmmo);

                if (rule.testRuleMatching()) {
                    FuzzyValueVector vector = rule.execute();
                    if (result == null) {
                        result = vector.fuzzyValueAt(0);
                    } else {
                        result = result.fuzzyUnion(vector.fuzzyValueAt(0));
                    }
                }
            }

            assert result != null;

            // Defuzzify the final result
            return result.momentDefuzzify();
        } catch (FuzzyException ex) {
            ex.printStackTrace();
            logger.error("An error occurred while evaluating a weapon with parameters distance={}, ammo={}",
                    dist, ammo, ex);
            throw new RuntimeException(ex);
        }
    }


    /**
     * Adds a rule to the fuzzy evaluator.
     */
    protected void addRules(FuzzyRule... rules) {
        Collections.addAll(this.rules, rules);
    }


    /**
     * Configures the rules
     */
    protected abstract void configure();


    /**
     * Initializes the fuzzy variables and terms
     */
    private void initializeVariables(Weapon weapon) {
        try {
            final int maxDistance = 50;
            distance = new Variable("distance", 0, 50);
            close = distance.addTerm(new Term("close", new TrapezoidFuzzySet(0, 0, 1, 4)));
            medium = distance.addTerm(new Term("medium", new TriangleFuzzySet(1, 4, 8)));
            far = distance.addTerm(new Term("far", new TrapezoidFuzzySet(4, 8, maxDistance, maxDistance)));

            final int firstQuarter = weapon.getMaxAmmo() / 4;
            final int thirdQuarter = 3 * firstQuarter;
            ammoStatus = new Variable("ammoStatus", -1, weapon.getMaxAmmo() + 1);
            low = ammoStatus.addTerm(new Term("low", new TrapezoidFuzzySet(0, 0, 0, firstQuarter)));
            ok = ammoStatus.addTerm(new Term("ok", new TriangleFuzzySet(0, firstQuarter, thirdQuarter)));
            loads = ammoStatus.addTerm(new Term("loads", new TrapezoidFuzzySet(firstQuarter, thirdQuarter,
                    weapon.getMaxAmmo(), weapon.getMaxAmmo())));

            desirability = new Variable("desirability", 0, 100);
            undesirable = desirability.addTerm(new Term("undesirable", new TrapezoidFuzzySet(0, 0, 25, 50)));
            desirable = desirability.addTerm(new Term("desirable", new TriangleFuzzySet(25, 50, 75)));
            veryDesirable = desirability.addTerm(new Term("veryDesirable", new TrapezoidFuzzySet(50, 75, 100, 100)));
        } catch (FuzzyException ex) {
            throw new RuntimeException(ex);
        }
    }
}
