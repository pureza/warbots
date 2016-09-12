package io.github.pureza.warbots.fuzzy;

import nrc.fuzzy.FuzzyException;
import nrc.fuzzy.FuzzyRule;
import nrc.fuzzy.FuzzyValue;

import java.util.ArrayList;
import java.util.List;


/**
 * Builds a FuzzyRule using a DSL as the following example demonstrates:
 *
 *   when(distance.is(far))
 *     .and(ammoStatus.is(low))
 *     .then(desirability.is(veryDesirable)))
 *     .toFuzzyRule();
 */
public final class RuleBuilder {

    /** Rule's antecedents */
    private final List<Condition> antecedents;

    /** Rule's conclusions */
    private final List<Condition> conclusions;


    /**
     * Creates a new rule with a single antecedent
     *
     * One should call and() and then() in order to add more antecedents
     * or conclusions.
     */
    public static RuleBuilder when(Condition condition) {
        RuleBuilder rule = new RuleBuilder();
        rule.antecedents.add(condition);
        return rule;
    }


    /**
     * Creates a new, empty rule.
     */
    private RuleBuilder() {
        this.antecedents = new ArrayList<>();
        this.conclusions = new ArrayList<>();
    }


    /**
     * Appends a new condition to the set of antecedents or conclusions
     */
    public RuleBuilder and(Condition condition) {
        if (this.conclusions.isEmpty()) {
            this.antecedents.add(condition);
        } else {
            this.conclusions.add(condition);
        }

        return this;
    }


    /**
     * Adds a new condition to the conclusions of the rule.
     */
    public RuleBuilder then(Condition condition) {
        if (!this.conclusions.isEmpty()) {
            throw new IllegalArgumentException("Use then() for the first conclusion and and() to add more");
        }

        this.conclusions.add(condition);
        return this;
    }


    /**
     * Converts the Rule into FuzzyRule format
     */
    public FuzzyRule toFuzzyRule() {
        FuzzyRule fuzzyRule = new FuzzyRule();

        try {
            // Add each antecedent
            for (Condition condition : antecedents) {
                fuzzyRule.addAntecedent(new FuzzyValue(condition.variable().fuzzyVariable(), condition.term().name()));
            }

            // Add each conclusion
            for (Condition condition : conclusions) {
                fuzzyRule.addConclusion(new FuzzyValue(condition.variable().fuzzyVariable(), condition.term().name()));
            }
        } catch (FuzzyException ex) {
            throw new RuntimeException(ex);
        }

        return fuzzyRule;
    }
}
