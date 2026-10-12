package javax.constraints.impl;

import javax.constraints.extra.AbstractReversible;

import org.chocosolver.memory.IStateInt;

/** Backtrackable integer backed by Choco's environment. */
public class Reversible extends AbstractReversible {
    private final IStateInt state;

    public Reversible(javax.constraints.Problem problem, int value) { this(problem, "", value); }

    public Reversible(javax.constraints.Problem problem, String name, int value) {
        super(problem, name, value);
        state = ((Problem) problem).getChocoModel().getEnvironment().makeInt(value);
        setImpl(state);
    }

    @Override public int getValue() { return state.get(); }
    @Override public void setValue(int value) { state.set(value); }
}
