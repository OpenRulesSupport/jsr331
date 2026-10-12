package javax.constraints.impl;

import javax.constraints.DomainType;
import javax.constraints.Problem;
import javax.constraints.extra.PropagationEvent;
import javax.constraints.extra.Propagator;

import org.maxicp.cp.CPFactory;
import org.maxicp.cp.engine.core.CPIntVar;

/** JSR-331 integer variable backed by a MaxiCP {@link CPIntVar}. */
public class Var extends AbstractVar implements javax.constraints.Var {
    public Var(Problem problem, String name, int min, int max) {
        this(problem, name, CPFactory.makeIntVar(
                ((javax.constraints.impl.Problem) problem).getMaxiCPSolver(), min, max));
    }

    public Var(Problem problem, String name, CPIntVar variable) {
        super(problem, name == null ? "" : name);
        setImpl(variable);
    }

    public Var(Problem problem, CPIntVar variable) { this(problem, "", variable); }

    public CPIntVar getMaxiCPVar() { return (CPIntVar) getImpl(); }

    @Override public boolean contains(int value) { return getMaxiCPVar().contains(value); }
    @Override public int getMin() { return getMaxiCPVar().min(); }
    @Override public int getMax() { return getMaxiCPVar().max(); }
    @Override public int getDomainSize() { return getMaxiCPVar().size(); }
    @Override public boolean isBound() { return getMaxiCPVar().isFixed(); }

    public void setMin(int min) {
        getMaxiCPVar().removeBelow(min);
        getMaxiCPVar().getSolver().fixPoint();
    }

    public void setMax(int max) {
        getMaxiCPVar().removeAbove(max);
        getMaxiCPVar().getSolver().fixPoint();
    }

    public void removeValue(int value) {
        getMaxiCPVar().remove(value);
        getMaxiCPVar().getSolver().fixPoint();
    }

    @Override public void setDomainType(DomainType type) {
        // MaxiCP selects the domain representation internally.
    }

    @Override
    public javax.constraints.Var plus(int value) {
        return derived(getName() + (value >= 0 ? "+" : "") + value,
                CPFactory.plus(getMaxiCPVar(), value));
    }

    @Override
    public javax.constraints.Var plus(javax.constraints.Var other) {
        return derived(getName() + "+" + other.getName(),
                CPFactory.sum(getMaxiCPVar(), unwrap(other)));
    }

    @Override
    public javax.constraints.Var multiply(int value) {
        return derived(getName() + "*" + value, CPFactory.mul(getMaxiCPVar(), value));
    }

    @Override
    public javax.constraints.Var multiply(javax.constraints.Var other) {
        return derived(getName() + "*" + other.getName(),
                CPFactory.mul(getMaxiCPVar(), unwrap(other)));
    }

    @Override
    public javax.constraints.Var divide(int value) {
        if (value == 0) throw new IllegalArgumentException("Division by zero");
        if (value == 1) return this;
        int a = getMin() / value;
        int b = getMax() / value;
        javax.constraints.Var quotient =
                getProblem().variable("", Math.min(a, b), Math.max(a, b));
        getProblem().post(this, "=", quotient.multiply(value));
        return quotient;
    }

    @Override
    public javax.constraints.Var divide(javax.constraints.Var divisor) throws Exception {
        if (divisor.isBound() && divisor.getValue() == 0)
            throw new ArithmeticException("Division by zero");
        long[] candidates = {
            safeDivide(getMin(), divisor.getMin()), safeDivide(getMin(), divisor.getMax()),
            safeDivide(getMax(), divisor.getMin()), safeDivide(getMax(), divisor.getMax())
        };
        javax.constraints.Var quotient = getProblem().variable("",
                clamp(min(candidates)), clamp(max(candidates)));
        getProblem().post(this, "=", quotient.multiply(divisor));
        return quotient;
    }

    @Override
    public javax.constraints.Var abs() {
        return derived("abs(" + getName() + ")", CPFactory.abs(getMaxiCPVar()));
    }

    private javax.constraints.Var derived(String name, CPIntVar variable) {
        return new javax.constraints.impl.Var(getProblem(), name, variable);
    }

    private static CPIntVar unwrap(javax.constraints.Var variable) {
        if (!(variable instanceof javax.constraints.impl.Var maxicp))
            throw new IllegalArgumentException("Variable belongs to another JSR-331 implementation");
        return maxicp.getMaxiCPVar();
    }

    private static long safeDivide(int value, int divisor) {
        if (divisor == 0) return value < 0 ? Integer.MIN_VALUE + 1L : Integer.MAX_VALUE - 1L;
        return value / divisor;
    }

    private static int clamp(long value) {
        return (int) Math.max(Integer.MIN_VALUE + 1L, Math.min(Integer.MAX_VALUE - 1L, value));
    }

    private static long min(long[] values) {
        long result = Long.MAX_VALUE;
        for (long value : values) result = Math.min(result, value);
        return result;
    }

    private static long max(long[] values) {
        long result = Long.MIN_VALUE;
        for (long value : values) result = Math.max(result, value);
        return result;
    }

    @Override
    public void addPropagator(Propagator propagator, PropagationEvent event) {
        Runnable callback = () -> {
            try {
                propagator.propagate(event);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        };
        switch (event) {
            case VALUE -> getMaxiCPVar().whenFixed(callback);
            case MIN, MAX, RANGE -> getMaxiCPVar().whenBoundChange(callback);
            case REMOVE, ANY -> getMaxiCPVar().whenDomainChange(callback);
        }
    }
}
