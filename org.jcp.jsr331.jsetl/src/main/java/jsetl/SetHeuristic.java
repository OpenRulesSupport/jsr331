package jsetl;

/**
 * This enumeration is used to decide which (non-)membership constraint will be solved first.
 */
public enum SetHeuristic {

    /**
     * The membership constraint k in X is solved first.
     */
    FIRST_IN,

    /**
     * The non-membership constraint k not in X is solved first
     */
    FIRST_NIN
}
