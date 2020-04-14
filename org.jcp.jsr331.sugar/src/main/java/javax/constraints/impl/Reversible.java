package javax.constraints.impl;

import javax.constraints.Problem;
import javax.constraints.extra.AbstractReversible;

/**
 * This class implements reversible integers that
 * automatically restore their values when a solver backtracks.
 */
public class Reversible extends AbstractReversible {
    public Reversible(Problem problem, int value) {
        this(problem, "", value);
    }
    
    public Reversible(Problem problem, String name, int value) {
        super(problem, name, value);
        // TODO JSR331 Implementation
        throw new RuntimeException("Reversible is not supported");
    }
    
    public int getValue() {
        // TODO JSR331 Implementation
        return -1;
    }
    
    public void setValue(int value) {
        // TODO JSR331 Implementation
    }
}
