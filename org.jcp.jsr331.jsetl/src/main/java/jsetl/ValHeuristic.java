package jsetl;

/**
 * This enumeration implements the possible value choice heuristics for a selected {@code IntLVar} x.
 * The {@code IntLVar} has the multi-interval D_x = I_1 ∪ ... ∪ I_n as its domain.
 */
public enum ValHeuristic {

    /**
     * Selects the GLB (greatest lower bound) of D_x.
     */
    GLB,

    /**
     * Selects the LUB (least upper bound) of D_x.
     */
    LUB,

    /**
     * Selects the middle point of the 'central' interval I_k with k = n/2.
     */
    MID_MOST,

    /**
     * Selects the median value of D_x.
     * Note that if |D_x| is even, the minimum between the two median values of D_x will be selected.
     */
    MEDIAN,

    /**
     * Selects a pseudorandom equidistributed value in D_x.
     */
    EQUI_RANDOM,

    /**
     * Selects a pseudorandom equidistributed value in I_k, where k is a pseudorandom equidistributed value in {1,...,n}.
     */
    RANGE_RANDOM,

    /**
     * Selects the midpoint of an interval I_k, where k is a pseudorandom equidistributed value in {1,...,n}.
     */
    MID_RANDOM
}
