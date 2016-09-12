package io.github.pureza.warbots.fuzzy;

import nrc.fuzzy.FuzzyException;
import nrc.fuzzy.FuzzyVariable;

/**
 * A fuzzy variable in the rule DSL.
 *
 * A variable has a name, a domain, and a set of terms (cold, hot, etc).
 */
public class Variable {

    /** The variable's name */
    private String name;

    /** The corresponding fuzzy variable */
    private FuzzyVariable fuzzyVariable;


    /**
     * Creates a new, empty variable, that can take a value within the given
     * interval
     */
    public Variable(String name, int min, int max) {
        this.name = name;

        try {
            this.fuzzyVariable = new FuzzyVariable(name, min, max, "units");
        } catch (FuzzyException ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * Creates a new condition where the current variable instance takes the
     * value of the given term (i.e., temperature is hot)
     */
    public Condition is(Term term) {
        return new Condition(this, term);
    }


    /**
     * Adds a new term to the list of terms of this variable
     */
    public Term addTerm(Term term) {
        try {
            fuzzyVariable.addTerm(term.name(), term.fuzzySet());
        } catch (FuzzyException ex) {
            throw new RuntimeException(ex);
        }

        return term;
    }


    public String name() {
        return name;
    }


    public FuzzyVariable fuzzyVariable() {
        return fuzzyVariable;
    }
}
