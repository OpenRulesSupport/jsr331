package jsetl;

/**
 * This enumeration implements the possible variable choice heuristics for a given collection x_1,...,x_n of {@code IntLVar}s.
 */
public enum VarHeuristic {

    /**
     * Selects the rightmost variable x_n.
     */
    RIGHT_MOST,

    /**
     * Selects the leftmost variable x_1.
     */
    LEFT_MOST,

    /**
     * Selects the midmost variable x_k, where k = n/2.
     */
    MID_MOST,

    /**
     * Selects the leftmost variable with the smallest greatest lower bound.
     */
    MIN,

    /**
     * Selects the leftmost variable with the greatest least upper bound.
     */
    MAX,

    /**
     * Selects the leftmost variable with the smallest domain.
     */
    FIRST_FAIL,

    /**
     * Selects a variable x_k, where k is a pseudorandom equidistributed value in {1,...,n}.
     */
    RANDOM
}
