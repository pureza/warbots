package io.github.pureza.warbots.fuzzy;

import nrc.fuzzy.FuzzySet;

/**
 * Represents a term in the fuzzy logic DSL
 *
 * A term is a possible value for a variable. For instance, if the variable is
 * temperature, cold and hot are possible names for its terms.
 */
public class Term {

    /** The name of the term */
    private String name;

    /** The fuzzy set of the term */
    private FuzzySet fuzzySet;


    /**
     * Creates a new term with the given name and fuzzy set.
     */
    public Term(String termName, FuzzySet fuzzySet) {
        this.name = termName;
        this.fuzzySet = fuzzySet;
    }


    public String name() {
        return name;
    }


    public FuzzySet fuzzySet() {
        return fuzzySet;
    }
}
