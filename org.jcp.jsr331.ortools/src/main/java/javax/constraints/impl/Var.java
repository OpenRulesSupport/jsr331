package javax.constraints.impl;

import java.util.HashSet;
import java.util.Set;

import javax.constraints.DomainType;
import javax.constraints.extra.PropagationEvent;
import javax.constraints.extra.Propagator;

import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearArgument;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.util.Domain;

/** Integer variable wrapper for OR-Tools CP-SAT. */
public class Var extends AbstractVar implements javax.constraints.Var {
    private final Domain initialDomain;
    private final Set<Integer> removedValues = new HashSet<>();
    private int lowerBound;
    private int upperBound;
    private Integer solutionValue;

    public Var(javax.constraints.Problem problem, IntVar variable) {
        super(problem, variable.getName());
        setImpl(variable);
        initialDomain = variable.getDomain();
        lowerBound = Problem.clamp(initialDomain.min());
        upperBound = Problem.clamp(initialDomain.max());
        ((Problem) problem).register(this);
    }

    public Var(javax.constraints.Problem problem, String name, int min, int max) {
        this(problem, ((Problem) problem).getCpModel().newIntVar(
                min, max, name == null || name.isBlank()
                        ? ((Problem) problem).nextName("var") : name));
    }

    public Var(javax.constraints.Problem problem, String name, int[] domain) {
        this(problem, createDomainVariable((Problem) problem, name, domain));
    }

    private static IntVar createDomainVariable(Problem problem, String name, int[] domain) {
        if (domain == null || domain.length == 0) {
            throw new IllegalArgumentException("A variable domain must not be empty");
        }
        long[] values = new long[domain.length];
        for (int i = 0; i < domain.length; i++) {
            values[i] = domain[i];
        }
        return problem.getCpModel().newIntVarFromDomain(
                Domain.fromValues(values), name == null || name.isBlank()
                        ? problem.nextName("var") : name);
    }

    public IntVar getCpVar() {
        return (IntVar) getImpl();
    }

    @Override
    public void setDomainType(DomainType type) {
        domainType = type;
    }

    @Override
    public boolean contains(int value) {
        return value >= lowerBound && value <= upperBound
                && initialDomain.contains(value) && !removedValues.contains(value);
    }

    public void setMin(int minimum) throws Exception {
        if (minimum > getMax()) {
            throw new Exception("Cannot set minimum of " + getName());
        }
        if (minimum > lowerBound) {
            ((Problem) getProblem()).getCpModel().addGreaterOrEqual(getCpVar(), minimum);
            lowerBound = minimum;
        }
    }

    @Override
    public int getMin() {
        if (solutionValue != null) {
            return solutionValue;
        }
        return nextAllowed(lowerBound, 1);
    }

    public void removeValue(int value) throws Exception {
        if (!contains(value)) {
            return;
        }
        if (getDomainSize() == 1) {
            throw new Exception("Cannot remove the last value from " + getName());
        }
        ((Problem) getProblem()).getCpModel().addDifferent(getCpVar(), value);
        removedValues.add(value);
    }

    @Override
    public int getDomainSize() {
        Domain bounded = boundedDomain();
        long size = bounded.size();
        for (int value : removedValues) {
            if (bounded.contains(value)) {
                size--;
            }
        }
        return size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.max(0, size);
    }

    public void setMax(int maximum) throws Exception {
        if (maximum < getMin()) {
            throw new Exception("Cannot set maximum of " + getName());
        }
        if (maximum < upperBound) {
            ((Problem) getProblem()).getCpModel().addLessOrEqual(getCpVar(), maximum);
            upperBound = maximum;
        }
    }

    @Override
    public int getMax() {
        if (solutionValue != null) {
            return solutionValue;
        }
        return nextAllowed(upperBound, -1);
    }

    @Override
    public boolean isBound() {
        return solutionValue != null || getDomainSize() == 1;
    }

    public void setSolutionValue(long value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new ArithmeticException("OR-Tools result is outside the JSR 331 int range");
        }
        solutionValue = (int) value;
    }

    public void clearSolutionValue() {
        solutionValue = null;
    }

    @Override
    public javax.constraints.Var plus(int value) {
        Problem problem = (Problem) getProblem();
        Var result = new Var(problem, problem.getCpModel().newIntVar(
                Problem.clamp((long) getMin() + value),
                Problem.clamp((long) getMax() + value), problem.nextName("plus")));
        problem.getCpModel().addEquality(
                result.getCpVar(), LinearExpr.affine(getCpVar(), 1, value));
        return result;
    }

    @Override
    public javax.constraints.Var plus(javax.constraints.Var variable) {
        Problem problem = (Problem) getProblem();
        Var other = (Var) variable;
        Var result = new Var(problem, problem.getCpModel().newIntVar(
                Problem.clamp((long) getMin() + other.getMin()),
                Problem.clamp((long) getMax() + other.getMax()), problem.nextName("plus")));
        problem.getCpModel().addEquality(result.getCpVar(),
                LinearExpr.sum(new LinearArgument[] {getCpVar(), other.getCpVar()}));
        return result;
    }

    @Override
    public javax.constraints.Var multiply(int value) {
        Problem problem = (Problem) getProblem();
        long first = (long) getMin() * value;
        long second = (long) getMax() * value;
        Var result = new Var(problem, problem.getCpModel().newIntVar(
                Problem.clamp(Math.min(first, second)),
                Problem.clamp(Math.max(first, second)), problem.nextName("multiply")));
        problem.getCpModel().addEquality(
                result.getCpVar(), LinearExpr.term(getCpVar(), value));
        return result;
    }

    @Override
    public javax.constraints.Var multiply(javax.constraints.Var variable) {
        Problem problem = (Problem) getProblem();
        Var other = (Var) variable;
        long[] bounds = {
            (long) getMin() * other.getMin(), (long) getMin() * other.getMax(),
            (long) getMax() * other.getMin(), (long) getMax() * other.getMax()
        };
        long min = bounds[0];
        long max = bounds[0];
        for (long bound : bounds) {
            min = Math.min(min, bound);
            max = Math.max(max, bound);
        }
        Var result = new Var(problem, problem.getCpModel().newIntVar(
                Problem.clamp(min), Problem.clamp(max), problem.nextName("multiply")));
        problem.getCpModel().addMultiplicationEquality(
                result.getCpVar(), getCpVar(), other.getCpVar());
        return result;
    }

    @Override
    public javax.constraints.Var divide(int value) {
        if (value == 0) {
            throw new IllegalArgumentException("Division by zero");
        }
        if (value == 1) {
            return this;
        }
        Problem problem = (Problem) getProblem();
        int first = getMin() / value;
        int second = getMax() / value;
        Var result = new Var(problem, problem.getCpModel().newIntVar(
                Math.min(first, second), Math.max(first, second), problem.nextName("divide")));
        problem.getCpModel().addDivisionEquality(
                result.getCpVar(), getCpVar(), LinearExpr.constant(value));
        return result;
    }

    @Override
    public javax.constraints.Var divide(javax.constraints.Var variable) {
        Problem problem = (Problem) getProblem();
        Var divisor = (Var) variable;
        problem.getCpModel().addDifferent(divisor.getCpVar(), 0);
        Var result = new Var(problem, problem.getCpModel().newIntVar(
                Integer.MIN_VALUE, Integer.MAX_VALUE, problem.nextName("divide")));
        problem.getCpModel().addDivisionEquality(
                result.getCpVar(), getCpVar(), divisor.getCpVar());
        return result;
    }

    @Override
    public javax.constraints.Var abs() {
        Problem problem = (Problem) getProblem();
        int max = Problem.clamp(Math.max(
                Math.abs((long) getMin()), Math.abs((long) getMax())));
        Var result = new Var(problem, problem.getCpModel().newIntVar(
                0, max, problem.nextName("abs")));
        problem.getCpModel().addAbsEquality(result.getCpVar(), getCpVar());
        return result;
    }

    @Override
    public void addPropagator(Propagator propagator, PropagationEvent event) {
        throw new UnsupportedOperationException(
                "Custom JSR 331 propagators are not supported by OR-Tools CP-SAT");
    }

    private Domain boundedDomain() {
        return initialDomain.intersectionWith(
                Domain.fromIntervals(new long[][] {{lowerBound, upperBound}}));
    }

    private int nextAllowed(int startingValue, int direction) {
        Domain bounded = boundedDomain();
        long candidate = direction > 0 ? bounded.min() : bounded.max();
        while (removedValues.contains((int) candidate)) {
            long next = candidate + direction;
            Domain remainder = direction > 0
                    ? bounded.intersectionWith(
                            Domain.fromIntervals(new long[][] {{next, upperBound}}))
                    : bounded.intersectionWith(
                            Domain.fromIntervals(new long[][] {{lowerBound, next}}));
            candidate = direction > 0 ? remainder.min() : remainder.max();
        }
        return Problem.clamp(candidate);
    }
}
