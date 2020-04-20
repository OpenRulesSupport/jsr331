package jsetl;

/**
 * Heuristics to use when labeling booleans.
 */
public enum BoolHeuristic {
    
    /**
     * Assign value {@code false} first when labeling.
     */
    FALSE,
    
    /**
     * Assign value {@code true} first when labeling.
     */
    TRUE,
    
    /**
     * Choose the order of {@code true} and {@code false} randomly.
     */
    RANDOM
}
