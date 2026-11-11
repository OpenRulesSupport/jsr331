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

import com.google.ortools.Loader;
import com.google.ortools.init.OrToolsVersion;
import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearArgument;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.Literal;
import com.google.ortools.util.Domain;

/** JSR 331 implementation backed by Google OR-Tools CP-SAT. */
public class Problem extends AbstractProblem {
    private static final Logger LOGGER = Logger.getLogger("javax.constraints");

    static {
        Loader.loadNativeLibraries();
    }

    private final CpModel model = new CpModel();
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
        setDomainType(DomainType.DOMAIN_AUTOMATIC);
    }

    @Override
    public String getImplVersion() {
        return "JSR 331 implementation based on Google OR-Tools CP-SAT "
                + OrToolsVersion.getVersionString();
    }

    @Override
    public void log(String text) {
        LOGGER.info(text);
    }

    @Override
    public void debug(String text) {
        LOGGER.fine(text);
    }

    @Override
    public void error(String text) {
        LOGGER.log(Level.SEVERE, text);
    }

    public CpModel getCpModel() {
        return model;
    }

    public String nextName(String prefix) {
        return "_jsr331_" + prefix + "_" + (++auxiliaryNumber);
    }

    void register(javax.constraints.impl.Var variable) {
        if (registered.add(variable)) {
            allVariables.add(variable);
        }
    }

    public List<javax.constraints.impl.Var> getAllVariables() {
        return Collections.unmodifiableList(allVariables);
    }

    public void clearSolutionValues() {
        for (javax.constraints.impl.Var variable : allVariables) {
            variable.clearSolutionValue();
        }
    }

    @Override
    public Var createVariable(String name, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("Variable minimum exceeds maximum: " + name);
        }
        return new javax.constraints.impl.Var(
                this, model.newIntVar(min, max, safeName(name, "var")));
    }

    @Override
    public Var variable(String name, int[] domain) {
        if (domain == null || domain.length == 0) {
            throw new IllegalArgumentException("A variable domain must not be empty");
        }
        long[] values = new long[domain.length];
        for (int i = 0; i < domain.length; i++) {
            values[i] = domain[i];
        }
        javax.constraints.impl.Var variable = new javax.constraints.impl.Var(
                this, model.newIntVarFromDomain(
                        Domain.fromValues(values), safeName(name, "var")));
        add(variable);
        return variable;
    }

    @Override
    public VarBool variableBool(String name) {
        javax.constraints.impl.VarBool variable = new javax.constraints.impl.VarBool(this, name);
        add(variable);
        return variable;
    }

    public IntVar[] createIntVarArray(Var[] variables) {
        IntVar[] result = new IntVar[variables.length];
        for (int i = 0; i < variables.length; i++) {
            result[i] = ((javax.constraints.impl.Var) variables[i]).getCpVar();
        }
        return result;
    }

    @Override
    public void post(Constraint constraint) {
        constraint.post();
    }

    @Override
    public javax.constraints.extra.Reversible addReversible(String name, int value) {
        return new Reversible(this, name, value);
    }

    @Override
    public Constraint post(Var variable, String operator, int value) {
        Constraint constraint = add(new Linear(variable, operator, value));
        constraint.post();
        return constraint;
    }

    @Override
    public Constraint linear(Var variable, String operator, int value) {
        return add(new Linear(variable, operator, value));
    }

    @Override
    public Constraint post(Var left, String operator, Var right) {
        Constraint constraint = add(new Linear(left, operator, right));
        constraint.post();
        return constraint;
    }

    @Override
    public Constraint linear(Var left, String operator, Var right) {
        return add(new Linear(left, operator, right));
    }

    @Override
    public Constraint postElement(int[] array, Var index, String operator, int value) {
        Constraint constraint = add(new Element(array, index, operator, value));
        constraint.post();
        return constraint;
    }

    @Override
    public Constraint postElement(int[] array, Var index, String operator, Var value) {
        Constraint constraint = add(new Element(array, index, operator, value));
        constraint.post();
        return constraint;
    }

    @Override
    public Constraint postElement(Var[] array, Var index, String operator, int value) {
        Constraint constraint = add(new Element(array, index, operator, value));
        constraint.post();
        return constraint;
    }

    @Override
    public Constraint postElement(Var[] array, Var index, String operator, Var value) {
        Constraint constraint = add(new Element(array, index, operator, value));
        constraint.post();
        return constraint;
    }

    @Override
    public Var sum(Var[] variables) {
        if (variables.length == 0) {
            return new javax.constraints.impl.Var(this, model.newConstant(0));
        }
        long min = 0;
        long max = 0;
        for (Var variable : variables) {
            min += variable.getMin();
            max += variable.getMax();
        }
        javax.constraints.impl.Var result = new javax.constraints.impl.Var(this,
                model.newIntVar(clamp(min), clamp(max), nextName("sum")));
        model.addEquality(result.getCpVar(), LinearExpr.sum(createIntVarArray(variables)));
        return result;
    }

    @Override
    public Var scalProd(int[] coefficients, Var[] variables) {
        if (coefficients.length != variables.length) {
            throw new IllegalArgumentException(
                    "coefficients and variables must have the same length");
        }
        long min = 0;
        long max = 0;
        long[] longCoefficients = new long[coefficients.length];
        for (int i = 0; i < coefficients.length; i++) {
            longCoefficients[i] = coefficients[i];
            long first = (long) coefficients[i] * variables[i].getMin();
            long second = (long) coefficients[i] * variables[i].getMax();
            min += Math.min(first, second);
            max += Math.max(first, second);
        }
        javax.constraints.impl.Var result = new javax.constraints.impl.Var(this,
                model.newIntVar(clamp(min), clamp(max), nextName("scalar")));
        model.addEquality(result.getCpVar(),
                LinearExpr.weightedSum(createIntVarArray(variables), longCoefficients));
        return result;
    }

    @Override
    public Constraint allDiff(Var[] variables) {
        return add(new AllDifferent(variables));
    }

    @Override
    public Constraint postCardinality(
            Var[] variables, int cardinalityValue, String operator, int value) {
        Constraint constraint = add(
                new Cardinality(variables, cardinalityValue, operator, value));
        constraint.post();
        return constraint;
    }

    @Override
    public Constraint postCardinality(
            Var[] variables, int cardinalityValue, String operator, Var value) {
        Constraint constraint = add(
                new Cardinality(variables, cardinalityValue, operator, value));
        constraint.post();
        return constraint;
    }

    @Override
    public Constraint postGlobalCardinality(
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

    @Override
    public Constraint postGlobalCardinality(
            Var[] variables, int[] values, int[] minimums, int[] maximums) {
        Constraint constraint = add(
                new GlobalCardinality(variables, values, minimums, maximums));
        constraint.post();
        return constraint;
    }

    @Override
    protected Solver createSolver() {
        return new javax.constraints.impl.search.Solver(this);
    }

    public BoolVar relation(LinearArgument left, String operator, long right) {
        String normalized = normalizeOperator(operator);
        BoolVar truth = model.newBoolVar(nextName("relation"));
        addComparison(left, normalized, right).onlyEnforceIf(truth);
        addComparison(left, oppositeOperator(normalized), right).onlyEnforceIf(truth.not());
        return truth;
    }

    public BoolVar relation(
            LinearArgument left, String operator, LinearArgument right) {
        String normalized = normalizeOperator(operator);
        BoolVar truth = model.newBoolVar(nextName("relation"));
        addComparison(left, normalized, right).onlyEnforceIf(truth);
        addComparison(left, oppositeOperator(normalized), right).onlyEnforceIf(truth.not());
        return truth;
    }

    private com.google.ortools.sat.Constraint addComparison(
            LinearArgument left, String operator, long right) {
        return switch (operator) {
            case "=" -> model.addEquality(left, right);
            case "!=" -> model.addDifferent(left, right);
            case "<" -> model.addLessThan(left, right);
            case "<=" -> model.addLessOrEqual(left, right);
            case ">" -> model.addGreaterThan(left, right);
            case ">=" -> model.addGreaterOrEqual(left, right);
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    private com.google.ortools.sat.Constraint addComparison(
            LinearArgument left, String operator, LinearArgument right) {
        return switch (operator) {
            case "=" -> model.addEquality(left, right);
            case "!=" -> model.addDifferent(left, right);
            case "<" -> model.addLessThan(left, right);
            case "<=" -> model.addLessOrEqual(left, right);
            case ">" -> model.addGreaterThan(left, right);
            case ">=" -> model.addGreaterOrEqual(left, right);
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    public BoolVar andTruth(Literal... literals) {
        if (literals.length == 0) {
            return fixedTruth(true);
        }
        if (literals.length == 1 && literals[0] instanceof BoolVar booleanVariable) {
            return booleanVariable;
        }
        BoolVar result = model.newBoolVar(nextName("and"));
        for (Literal literal : literals) {
            model.addImplication(result, literal);
        }
        Literal[] clause = new Literal[literals.length + 1];
        for (int i = 0; i < literals.length; i++) {
            clause[i] = literals[i].not();
        }
        clause[literals.length] = result;
        model.addBoolOr(clause);
        return result;
    }

    public BoolVar orTruth(Literal... literals) {
        if (literals.length == 0) {
            return fixedTruth(false);
        }
        if (literals.length == 1 && literals[0] instanceof BoolVar booleanVariable) {
            return booleanVariable;
        }
        BoolVar result = model.newBoolVar(nextName("or"));
        for (Literal literal : literals) {
            model.addImplication(literal, result);
        }
        Literal[] clause = new Literal[literals.length + 1];
        System.arraycopy(literals, 0, clause, 0, literals.length);
        clause[literals.length] = result.not();
        model.addBoolOr(clause);
        return result;
    }

    public BoolVar notTruth(Literal literal) {
        BoolVar result = model.newBoolVar(nextName("not"));
        model.addBoolOr(new Literal[] {result.not(), literal.not()});
        model.addBoolOr(new Literal[] {result, literal});
        return result;
    }

    public BoolVar fixedTruth(boolean value) {
        BoolVar result = model.newBoolVar(nextName(value ? "true" : "false"));
        model.addEquality(result, value ? 1 : 0);
        return result;
    }

    public static String normalizeOperator(String operator) {
        if (operator == null) {
            throw new IllegalArgumentException("Constraint operator must not be null");
        }
        if ("==".equals(operator) || "is".equalsIgnoreCase(operator)) {
            return "=";
        }
        if ("is not".equalsIgnoreCase(operator) || "<>".equals(operator)) {
            return "!=";
        }
        if ("=".equals(operator) || "!=".equals(operator) || "<".equals(operator)
                || "<=".equals(operator) || ">".equals(operator)
                || ">=".equals(operator)) {
            return operator;
        }
        throw new IllegalArgumentException("Unsupported operator: " + operator);
    }

    private static String oppositeOperator(String operator) {
        return switch (operator) {
            case "=" -> "!=";
            case "!=" -> "=";
            case "<" -> ">=";
            case "<=" -> ">";
            case ">" -> "<=";
            case ">=" -> "<";
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        };
    }

    @Override
    public Constraint getFalseConstraint() {
        if (falseConstraint == null) {
            falseConstraint = new javax.constraints.impl.Constraint(
                    this, "False", fixedTruth(false));
        }
        return falseConstraint;
    }

    @Override
    public Constraint getTrueConstraint() {
        if (trueConstraint == null) {
            trueConstraint = new javax.constraints.impl.Constraint(
                    this, "True", fixedTruth(true));
        }
        return trueConstraint;
    }

    @Override
    public void loadFromXML(InputStream input) {
        throw new UnsupportedOperationException("OR-Tools adapter does not support XML import");
    }

    @Override
    public void storeToXML(OutputStream output, String comment) {
        throw new UnsupportedOperationException("OR-Tools adapter does not support XML export");
    }

    public static int clamp(long value) {
        return (int) Math.max(Integer.MIN_VALUE, Math.min(Integer.MAX_VALUE, value));
    }

    private String safeName(String requested, String prefix) {
        return requested == null || requested.isBlank() ? nextName(prefix) : requested;
    }
}
