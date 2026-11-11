package javax.constraints.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.constraints.Constraint;
import javax.constraints.DomainType;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarBool;
import javax.constraints.impl.constraint.AllDifferent;
import javax.constraints.impl.constraint.Cardinality;
import javax.constraints.impl.constraint.Element;
import javax.constraints.impl.constraint.GlobalCardinality;
import javax.constraints.impl.constraint.Linear;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloIntExpr;
import ilog.concert.IloIntVar;
import ilog.cp.IloCP;

/** JSR 331 implementation backed by IBM ILOG CP Optimizer. */
public class Problem extends AbstractProblem {
    private static final Logger LOGGER = Logger.getLogger("javax.constraints");

    private final IloCP cp;
    private final List<javax.constraints.impl.Var> allVariables = new ArrayList<>();
    private final Set<javax.constraints.impl.Var> registered =
            Collections.newSetFromMap(new IdentityHashMap<>());
    private int auxiliaryNumber;
    private Constraint falseConstraint;
    private Constraint trueConstraint;

    public Problem() {
        this("");
    }

    public Problem(String name) {
        super(name);
        CpOptimizerRuntime.ensureLoaded();
        cp = new IloCP();
        cp.setOut(null);
        cp.setWarning(null);
        setDomainType(DomainType.DOMAIN_AUTOMATIC);
    }

    @Override
    public String getImplVersion() {
        if (cp == null) {
            String version = IloCP.class.getPackage().getImplementationVersion();
            return "JSR 331 implementation based on IBM ILOG CP Optimizer "
                    + (version == null ? "22.2" : version);
        }
        try {
            return "JSR 331 implementation based on IBM ILOG CP Optimizer "
                    + cp.getVersion();
        } catch (IloException exception) {
            throw failure("read the CP Optimizer version", exception);
        }
    }

    @Override public void log(String text) { LOGGER.info(text); }
    @Override public void debug(String text) { LOGGER.fine(text); }
    @Override public void error(String text) { LOGGER.log(Level.SEVERE, text); }

    public IloCP getCp() { return cp; }

    public String nextName(String prefix) {
        return "_jsr331_" + prefix + "_" + (++auxiliaryNumber);
    }

    void register(javax.constraints.impl.Var variable) {
        if (registered.add(variable)) {
            allVariables.add(variable);
            try {
                // A JSR 331 variable may remain otherwise unconstrained but still be
                // included in a search strategy. CP Optimizer requires every such
                // search variable to be explicitly extracted into the model.
                cp.add(variable.getCpVar());
            } catch (IloException exception) {
                throw failure("extract variable " + variable.getName(), exception);
            }
        }
    }

    public List<javax.constraints.impl.Var> getAllVariables() {
        return Collections.unmodifiableList(allVariables);
    }

    public void clearSolutionValues() {
        allVariables.forEach(javax.constraints.impl.Var::clearSolutionValue);
    }

    @Override
    public Var createVariable(String name, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Variable minimum exceeds maximum: " + name);
        }
        try {
            return new javax.constraints.impl.Var(
                    this, cp.intVar(min, max, safeName(name, "var")), min, max, null);
        } catch (IloException exception) {
            throw failure("create integer variable " + name, exception);
        }
    }

    @Override
    public Var variable(String name, int[] domain) {
        if (domain == null || domain.length == 0) {
            throw new IllegalArgumentException("A variable domain must not be empty");
        }
        try {
            javax.constraints.impl.Var variable = new javax.constraints.impl.Var(
                    this, cp.intVar(domain, safeName(name, "var")),
                    java.util.Arrays.stream(domain).min().orElseThrow(),
                    java.util.Arrays.stream(domain).max().orElseThrow(), domain.clone());
            add(variable);
            return variable;
        } catch (IloException exception) {
            throw failure("create sparse-domain variable " + name, exception);
        }
    }

    @Override
    public VarBool variableBool(String name) {
        try {
            javax.constraints.impl.VarBool variable = new javax.constraints.impl.VarBool(
                    this, cp.intVar(0, 1, safeName(name, "bool")));
            add(variable);
            return variable;
        } catch (IloException exception) {
            throw failure("create Boolean variable " + name, exception);
        }
    }

    public IloIntExpr[] expressions(Var[] variables) {
        IloIntExpr[] result = new IloIntExpr[variables.length];
        for (int index = 0; index < variables.length; index++) {
            result[index] = ((javax.constraints.impl.Var) variables[index]).getCpVar();
        }
        return result;
    }

    @Override public void post(Constraint constraint) { constraint.post(); }

    @Override
    public javax.constraints.extra.Reversible addReversible(String name, int value) {
        return new Reversible(this, name, value);
    }

    @Override public Constraint post(Var variable, String operator, int value) {
        Constraint constraint = add(new Linear(variable, operator, value));
        constraint.post();
        return constraint;
    }
    @Override public Constraint linear(Var variable, String operator, int value) {
        return add(new Linear(variable, operator, value));
    }
    @Override public Constraint post(Var left, String operator, Var right) {
        Constraint constraint = add(new Linear(left, operator, right));
        constraint.post();
        return constraint;
    }
    @Override public Constraint linear(Var left, String operator, Var right) {
        return add(new Linear(left, operator, right));
    }

    @Override public Constraint postElement(int[] array, Var index, String operator, int value) {
        Constraint constraint = add(new Element(array, index, operator, value));
        constraint.post();
        return constraint;
    }
    @Override public Constraint postElement(int[] array, Var index, String operator, Var value) {
        Constraint constraint = add(new Element(array, index, operator, value));
        constraint.post();
        return constraint;
    }
    @Override public Constraint postElement(Var[] array, Var index, String operator, int value) {
        Constraint constraint = add(new Element(array, index, operator, value));
        constraint.post();
        return constraint;
    }
    @Override public Constraint postElement(Var[] array, Var index, String operator, Var value) {
        Constraint constraint = add(new Element(array, index, operator, value));
        constraint.post();
        return constraint;
    }

    @Override
    public Var sum(Var[] variables) {
        try {
            long min = 0;
            long max = 0;
            for (Var variable : variables) {
                min += variable.getMin();
                max += variable.getMax();
            }
            IloIntExpr expression = variables.length == 0
                    ? cp.intVar(0, 0, nextName("zero"))
                    : cp.sum(expressions(variables));
            return bind(expression, clamp(min), clamp(max), "sum");
        } catch (IloException exception) {
            throw failure("create sum expression", exception);
        }
    }

    @Override
    public Var scalProd(int[] coefficients, Var[] variables) {
        if (coefficients.length != variables.length) {
            throw new IllegalArgumentException(
                    "coefficients and variables must have the same length");
        }
        try {
            long min = 0;
            long max = 0;
            for (int index = 0; index < coefficients.length; index++) {
                long first = (long) coefficients[index] * variables[index].getMin();
                long second = (long) coefficients[index] * variables[index].getMax();
                min += Math.min(first, second);
                max += Math.max(first, second);
            }
            return bind(cp.prod(coefficients, expressions(variables)),
                    clamp(min), clamp(max), "scalar");
        } catch (IloException exception) {
            throw failure("create scalar-product expression", exception);
        }
    }

    @Override public Constraint allDiff(Var[] variables) {
        return add(new AllDifferent(variables));
    }

    @Override public Constraint postCardinality(
            Var[] variables, int cardinalityValue, String operator, int value) {
        Constraint constraint = add(new Cardinality(variables, cardinalityValue, operator, value));
        constraint.post();
        return constraint;
    }
    @Override public Constraint postCardinality(
            Var[] variables, int cardinalityValue, String operator, Var value) {
        Constraint constraint = add(new Cardinality(variables, cardinalityValue, operator, value));
        constraint.post();
        return constraint;
    }
    @Override public Constraint postGlobalCardinality(
            Var[] variables, int[] values, Var[] cardinalities) {
        Constraint constraint = add(new GlobalCardinality(variables, values, cardinalities));
        constraint.post();
        return constraint;
    }
    public Constraint postGlobalCardinality(Var[] variables, Var[] cardinalities) {
        Constraint constraint = add(new GlobalCardinality(variables, cardinalities));
        constraint.post();
        return constraint;
    }
    @Override public Constraint postGlobalCardinality(
            Var[] variables, int[] values, int[] minimums, int[] maximums) {
        Constraint constraint = add(new GlobalCardinality(variables, values, minimums, maximums));
        constraint.post();
        return constraint;
    }

    @Override protected Solver createSolver() {
        return new javax.constraints.impl.search.Solver(this);
    }

    public IloConstraint relation(IloIntExpr left, String operator, int right) {
        return switch (normalizeOperator(operator)) {
            case "=" -> cp.eq(left, right);
            case "!=" -> cp.neq(left, right);
            case "<" -> cp.lt(left, right);
            case "<=" -> cp.le(left, right);
            case ">" -> cp.gt(left, right);
            case ">=" -> cp.ge(left, right);
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    public IloConstraint relation(IloIntExpr left, String operator, IloIntExpr right) {
        return switch (normalizeOperator(operator)) {
            case "=" -> cp.eq(left, right);
            case "!=" -> cp.neq(left, right);
            case "<" -> cp.lt(left, right);
            case "<=" -> cp.le(left, right);
            case ">" -> cp.gt(left, right);
            case ">=" -> cp.ge(left, right);
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    public IloIntVar reify(IloConstraint relation) {
        try {
            IloIntVar truth = cp.intVar(0, 1, nextName("truth"));
            cp.add(cp.ifThenElse(relation, cp.eq(truth, 1), cp.eq(truth, 0)));
            return truth;
        } catch (IloException exception) {
            throw failure("reify a constraint", exception);
        }
    }

    public javax.constraints.impl.Var bind(
            IloIntExpr expression, int min, int max, String prefix) {
        try {
            IloIntVar variable = cp.intVar(min, max, nextName(prefix));
            cp.add(cp.eq(variable, expression));
            return new javax.constraints.impl.Var(this, variable, min, max, null);
        } catch (IloException exception) {
            throw failure("bind integer expression", exception);
        }
    }

    public static String normalizeOperator(String operator) {
        if (operator == null) {
            throw new IllegalArgumentException("Constraint operator must not be null");
        }
        if ("==".equals(operator) || "is".equalsIgnoreCase(operator)) return "=";
        if ("is not".equalsIgnoreCase(operator) || "<>".equals(operator)) return "!=";
        if ("=".equals(operator) || "!=".equals(operator) || "<".equals(operator)
                || "<=".equals(operator) || ">".equals(operator) || ">=".equals(operator)) {
            return operator;
        }
        throw new IllegalArgumentException("Unsupported operator: " + operator);
    }

    @Override public Constraint getFalseConstraint() {
        if (falseConstraint == null) {
            try {
                falseConstraint = new javax.constraints.impl.Constraint(
                        this, "False", cp.falseConstraint());
            } catch (IloException exception) {
                throw failure("create false constraint", exception);
            }
        }
        return falseConstraint;
    }

    @Override public Constraint getTrueConstraint() {
        if (trueConstraint == null) {
            try {
                trueConstraint = new javax.constraints.impl.Constraint(
                        this, "True", cp.trueConstraint());
            } catch (IloException exception) {
                throw failure("create true constraint", exception);
            }
        }
        return trueConstraint;
    }

    @Override public void loadFromXML(InputStream input) {
        throw new UnsupportedOperationException(
                "CP Optimizer adapter does not support JSR 331 XML import");
    }
    @Override public void storeToXML(OutputStream output, String comment) {
        throw new UnsupportedOperationException(
                "CP Optimizer adapter does not support JSR 331 XML export");
    }

    public static int clamp(long value) {
        return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, value));
    }

    public static IllegalStateException failure(String action, IloException exception) {
        return new IllegalStateException(
                "CP Optimizer could not " + action + ": " + exception.getMessage(), exception);
    }

    private String safeName(String requested, String prefix) {
        return requested == null || requested.isBlank() ? nextName(prefix) : requested;
    }
}
