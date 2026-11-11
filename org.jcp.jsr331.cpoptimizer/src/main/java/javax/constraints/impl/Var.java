package javax.constraints.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.constraints.DomainType;
import javax.constraints.extra.PropagationEvent;
import javax.constraints.extra.Propagator;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;

/** Integer variable wrapper for IBM ILOG CP Optimizer. */
public class Var extends AbstractVar implements javax.constraints.Var {
    private final int initialMin;
    private final int initialMax;
    private final int[] initialValues;
    private final Set<Integer> removedValues = new HashSet<>();
    private int lowerBound;
    private int upperBound;
    private Integer solutionValue;

    public Var(javax.constraints.Problem problem, IloIntVar variable,
            int min, int max, int[] values) {
        super(problem, variable.getName());
        setImpl(variable);
        initialMin = min;
        initialMax = max;
        initialValues = values == null ? null : values.clone();
        if (initialValues != null) {
            Arrays.sort(initialValues);
        }
        lowerBound = min;
        upperBound = max;
        ((Problem) problem).register(this);
    }

    public Var(javax.constraints.Problem problem, String name, int min, int max) {
        this(problem, createNative((Problem) problem, name, min, max), min, max, null);
    }

    public Var(javax.constraints.Problem problem, String name, int[] domain) {
        this(problem, createNative((Problem) problem, name, domain),
                Arrays.stream(domain).min().orElseThrow(),
                Arrays.stream(domain).max().orElseThrow(), domain);
    }

    public IloIntVar getCpVar() {
        return (IloIntVar) getImpl();
    }

    @Override public void setDomainType(DomainType type) { domainType = type; }

    @Override
    public boolean contains(int value) {
        return value >= lowerBound && value <= upperBound
                && inInitialDomain(value) && !removedValues.contains(value);
    }

    public void setMin(int minimum) throws Exception {
        if (minimum > getMax()) {
            throw new Exception("Cannot set minimum of " + getName());
        }
        if (minimum > lowerBound) {
            try {
                getCpVar().setMin(minimum);
                lowerBound = minimum;
            } catch (IloException exception) {
                throw Problem.failure("set minimum of " + getName(), exception);
            }
        }
    }

    @Override public int getMin() {
        return solutionValue != null ? solutionValue : nextAllowed(lowerBound, 1);
    }

    public void removeValue(int value) throws Exception {
        if (!contains(value)) return;
        if (getDomainSize() == 1) {
            throw new Exception("Cannot remove the last value from " + getName());
        }
        new javax.constraints.impl.constraint.Linear(this, "!=", value).post();
        removedValues.add(value);
    }

    @Override
    public int getDomainSize() {
        long size = initialValues == null
                ? (long) upperBound - lowerBound + 1
                : Arrays.stream(initialValues)
                        .filter(value -> value >= lowerBound && value <= upperBound)
                        .distinct().count();
        for (int value : removedValues) {
            if (value >= lowerBound && value <= upperBound && inInitialDomain(value)) size--;
        }
        return (int) Math.max(0, Math.min(Integer.MAX_VALUE, size));
    }

    public void setMax(int maximum) throws Exception {
        if (maximum < getMin()) {
            throw new Exception("Cannot set maximum of " + getName());
        }
        if (maximum < upperBound) {
            try {
                getCpVar().setMax(maximum);
                upperBound = maximum;
            } catch (IloException exception) {
                throw Problem.failure("set maximum of " + getName(), exception);
            }
        }
    }

    @Override public int getMax() {
        return solutionValue != null ? solutionValue : nextAllowed(upperBound, -1);
    }

    @Override public boolean isBound() {
        return solutionValue != null || getDomainSize() == 1;
    }

    public void setSolutionValue(long value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new ArithmeticException(
                    "CP Optimizer result is outside the JSR 331 int range");
        }
        solutionValue = (int) value;
    }

    public void clearSolutionValue() { solutionValue = null; }

    @Override
    public javax.constraints.Var plus(int value) {
        Problem problem = (Problem) getProblem();
        try {
            return problem.bind(problem.getCp().sum(getCpVar(), value),
                    Problem.clamp((long) getMin() + value),
                    Problem.clamp((long) getMax() + value), "plus");
        } catch (IloException exception) {
            throw Problem.failure("create addition expression", exception);
        }
    }

    @Override
    public javax.constraints.Var plus(javax.constraints.Var variable) {
        Problem problem = (Problem) getProblem();
        Var other = (Var) variable;
        try {
            return problem.bind(problem.getCp().sum(getCpVar(), other.getCpVar()),
                    Problem.clamp((long) getMin() + other.getMin()),
                    Problem.clamp((long) getMax() + other.getMax()), "plus");
        } catch (IloException exception) {
            throw Problem.failure("create addition expression", exception);
        }
    }

    @Override
    public javax.constraints.Var multiply(int value) {
        Problem problem = (Problem) getProblem();
        long first = (long) getMin() * value;
        long second = (long) getMax() * value;
        try {
            return problem.bind(problem.getCp().prod(getCpVar(), value),
                    Problem.clamp(Math.min(first, second)),
                    Problem.clamp(Math.max(first, second)), "multiply");
        } catch (IloException exception) {
            throw Problem.failure("create multiplication expression", exception);
        }
    }

    @Override
    public javax.constraints.Var multiply(javax.constraints.Var variable) {
        Problem problem = (Problem) getProblem();
        Var other = (Var) variable;
        long[] bounds = {
            (long) getMin() * other.getMin(), (long) getMin() * other.getMax(),
            (long) getMax() * other.getMin(), (long) getMax() * other.getMax()
        };
        try {
            return problem.bind(problem.getCp().prod(getCpVar(), other.getCpVar()),
                    Problem.clamp(Arrays.stream(bounds).min().orElseThrow()),
                    Problem.clamp(Arrays.stream(bounds).max().orElseThrow()), "multiply");
        } catch (IloException exception) {
            throw Problem.failure("create multiplication expression", exception);
        }
    }

    @Override
    public javax.constraints.Var divide(int value) {
        if (value == 0) throw new IllegalArgumentException("Division by zero");
        if (value == 1) return this;
        Problem problem = (Problem) getProblem();
        int first = getMin() / value;
        int second = getMax() / value;
        return problem.bind(problem.getCp().div(getCpVar(), value),
                Math.min(first, second), Math.max(first, second), "divide");
    }

    @Override
    public javax.constraints.Var divide(javax.constraints.Var variable) {
        Problem problem = (Problem) getProblem();
        Var divisor = (Var) variable;
        new javax.constraints.impl.constraint.Linear(divisor, "!=", 0).post();
        return problem.bind(problem.getCp().div(getCpVar(), divisor.getCpVar()),
                Integer.MIN_VALUE, Integer.MAX_VALUE, "divide");
    }

    @Override
    public javax.constraints.Var abs() {
        Problem problem = (Problem) getProblem();
        int maximum = Problem.clamp(Math.max(
                Math.abs((long) getMin()), Math.abs((long) getMax())));
        try {
            return problem.bind(problem.getCp().abs(getCpVar()), 0, maximum, "abs");
        } catch (IloException exception) {
            throw Problem.failure("create absolute-value expression", exception);
        }
    }

    @Override
    public void addPropagator(Propagator propagator, PropagationEvent event) {
        throw new UnsupportedOperationException(
                "Custom JSR 331 propagators are not supported by CP Optimizer");
    }

    private boolean inInitialDomain(int value) {
        return initialValues == null
                ? value >= initialMin && value <= initialMax
                : Arrays.binarySearch(initialValues, value) >= 0;
    }

    private int nextAllowed(int start, int direction) {
        if (initialValues != null) {
            if (direction > 0) {
                for (int value : initialValues) {
                    if (value >= start && value <= upperBound
                            && !removedValues.contains(value)) return value;
                }
            } else {
                for (int index = initialValues.length - 1; index >= 0; index--) {
                    int value = initialValues[index];
                    if (value <= start && value >= lowerBound
                            && !removedValues.contains(value)) return value;
                }
            }
            throw new IllegalStateException("Variable domain is empty: " + getName());
        }
        int candidate = start;
        while (removedValues.contains(candidate)) candidate += direction;
        return candidate;
    }

    private static IloIntVar createNative(
            Problem problem, String name, int min, int max) {
        try {
            return problem.getCp().intVar(min, max,
                    name == null || name.isBlank() ? problem.nextName("var") : name);
        } catch (IloException exception) {
            throw Problem.failure("create integer variable " + name, exception);
        }
    }

    private static IloIntVar createNative(Problem problem, String name, int[] domain) {
        if (domain == null || domain.length == 0) {
            throw new IllegalArgumentException("A variable domain must not be empty");
        }
        try {
            return problem.getCp().intVar(domain,
                    name == null || name.isBlank() ? problem.nextName("var") : name);
        } catch (IloException exception) {
            throw Problem.failure("create sparse-domain variable " + name, exception);
        }
    }
}
