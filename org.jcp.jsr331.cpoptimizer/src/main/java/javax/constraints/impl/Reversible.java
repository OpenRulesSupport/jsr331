package javax.constraints.impl;

import javax.constraints.extra.AbstractReversible;

/** Mutable JSR 331 reversible value used by the CP Optimizer adapter. */
public class Reversible extends AbstractReversible {
    private int value;

    public Reversible(javax.constraints.Problem problem, int value) {
        this(problem, "", value);
    }

    public Reversible(javax.constraints.Problem problem, String name, int value) {
        super(problem, name, value);
        this.value = value;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }
}
