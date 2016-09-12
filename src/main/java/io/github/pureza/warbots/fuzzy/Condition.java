package io.github.pureza.warbots.fuzzy;

/**
 * Represents a condition in a fuzzy rule, like "distance is far"
 */
public class Condition {

    /** Fuzzy variable */
    private final Variable variable;

    /** The variable's value */
    private final Term term;


    /**
     * Creates a new condition where the variable with the name given by the
     * first parameter has the value given the second parameter
     */
    public Condition(Variable variable, Term term) {
        this.variable = variable;
        this.term = term;
    }


    public Variable variable() {
        return variable;
    }


    public Term term() {
        return term;
    }
}
