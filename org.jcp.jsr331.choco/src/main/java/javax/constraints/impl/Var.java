package javax.constraints.impl;

import javax.constraints.DomainType;
import javax.constraints.extra.PropagationEvent;
import javax.constraints.extra.Propagator;

import org.chocosolver.solver.Cause;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.variables.IntVar;

/** Integer variable wrapper for Choco Solver 6. */
public class Var extends AbstractVar implements javax.constraints.Var {
    public Var(javax.constraints.Problem problem, IntVar variable) {
        super(problem, variable.getName());
        setImpl(variable);
    }

    public Var(javax.constraints.Problem problem, String name, int min, int max) {
        this(problem, ((Problem) problem).getChocoModel().intVar(name, min, max,
                ((Problem) problem).getDomainType() == DomainType.DOMAIN_MIN_MAX));
    }

    public Var(javax.constraints.Problem problem, String name, int[] domain) {
        this(problem, ((Problem) problem).getChocoModel().intVar(name, domain));
    }

    public IntVar getChocoVar() { return (IntVar) getImpl(); }
    public IntVar getChocoDomainVar() { return getChocoVar(); }

    @Override public void setDomainType(DomainType type) { domainType = type; }
    @Override public boolean contains(int value) { return getChocoVar().contains(value); }

    public void setMin(int min) throws Exception {
        try { getChocoVar().updateLowerBound(min, Cause.Null); }
        catch (ContradictionException e) { throw new Exception("Cannot set minimum of " + getName(), e); }
    }
    @Override public int getMin() { return getChocoVar().getLB(); }

    public void removeValue(int value) throws Exception {
        try { getChocoVar().removeValue(value, Cause.Null); }
        catch (ContradictionException e) { throw new Exception("Cannot remove value from " + getName(), e); }
    }
    @Override public int getDomainSize() { return getChocoVar().getDomainSize(); }

    public void setMax(int max) throws Exception {
        try { getChocoVar().updateUpperBound(max, Cause.Null); }
        catch (ContradictionException e) { throw new Exception("Cannot set maximum of " + getName(), e); }
    }
    @Override public int getMax() { return getChocoVar().getUB(); }
    @Override public boolean isBound() { return getChocoVar().isInstantiated(); }

    @Override public javax.constraints.Var plus(int value) {
        Problem p = (Problem) getProblem();
        IntVar result = p.getChocoModel().intVar(getName() + "+" + value,
                Problem.clamp((long) getMin() + value), Problem.clamp((long) getMax() + value), true);
        p.getChocoModel().scalar(new IntVar[]{getChocoVar(), result}, new int[]{1, -1}, "=", -value).post();
        return new Var(p, result);
    }

    @Override public javax.constraints.Var plus(javax.constraints.Var var) {
        Problem p = (Problem) getProblem();
        IntVar other = ((Var) var).getChocoVar();
        IntVar result = p.getChocoModel().intVar(getName() + "+" + var.getName(),
                Problem.clamp((long) getMin() + other.getLB()),
                Problem.clamp((long) getMax() + other.getUB()), true);
        p.getChocoModel().sum(new IntVar[]{getChocoVar(), other}, "=", result).post();
        return new Var(p, result);
    }

    @Override public javax.constraints.Var multiply(int value) {
        Problem p = (Problem) getProblem();
        long a = (long) getMin() * value, b = (long) getMax() * value;
        IntVar result = p.getChocoModel().intVar("mul", Problem.clamp(Math.min(a, b)),
                Problem.clamp(Math.max(a, b)), true);
        p.getChocoModel().times(getChocoVar(), value, result).post();
        return new Var(p, result);
    }

    @Override public javax.constraints.Var multiply(javax.constraints.Var var) {
        Problem p = (Problem) getProblem();
        IntVar other = ((Var) var).getChocoVar();
        long[] bounds = {(long) getMin() * other.getLB(), (long) getMin() * other.getUB(),
                (long) getMax() * other.getLB(), (long) getMax() * other.getUB()};
        long min = bounds[0], max = bounds[0];
        for (long bound : bounds) { min = Math.min(min, bound); max = Math.max(max, bound); }
        IntVar result = p.getChocoModel().intVar("mul", Problem.clamp(min), Problem.clamp(max), true);
        p.getChocoModel().times(getChocoVar(), other, result).post();
        return new Var(p, result);
    }

    @Override public javax.constraints.Var divide(int value) {
        if (value == 0) throw new IllegalArgumentException("Division by zero");
        if (value == 1) return this;
        Problem p = (Problem) getProblem();
        int a = getMin() / value, b = getMax() / value;
        IntVar result = p.getChocoModel().intVar("quotient", Math.min(a, b), Math.max(a, b), true);
        p.getChocoModel().div(getChocoVar(), p.getChocoModel().intVar(value), result).post();
        return new Var(p, result);
    }

    @Override public javax.constraints.Var divide(javax.constraints.Var var) {
        Problem p = (Problem) getProblem();
        IntVar result = p.getChocoModel().intVar("quotient", IntVar.MIN_INT_BOUND, IntVar.MAX_INT_BOUND, true);
        p.getChocoModel().div(getChocoVar(), ((Var) var).getChocoVar(), result).post();
        return new Var(p, result);
    }

    @Override public javax.constraints.Var abs() {
        Problem p = (Problem) getProblem();
        int max = Problem.clamp(Math.max(Math.abs((long) getMin()), Math.abs((long) getMax())));
        IntVar result = p.getChocoModel().intVar("abs", 0, max, true);
        p.getChocoModel().absolute(result, getChocoVar()).post();
        return new Var(p, result);
    }

    @Override public void addPropagator(Propagator propagator, PropagationEvent event) {
        throw new UnsupportedOperationException("Custom JSR-331 propagators are not supported by this adapter");
    }
}
