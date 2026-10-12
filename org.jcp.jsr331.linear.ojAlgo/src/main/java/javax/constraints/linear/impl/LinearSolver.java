package javax.constraints.linear.impl;

import java.io.File;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Objective;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.linear.NonLinearProblemException;

import org.ojalgo.optimisation.Expression;
import org.ojalgo.optimisation.ExpressionsBasedModel;
import org.ojalgo.optimisation.Optimisation;
import org.ojalgo.optimisation.Variable;

/**
 * JSR-331 linear solver adapter backed by ojAlgo's in-process LP/MIP model.
 *
 * <p>The common JSR-331 linear implementation stores a model as variables and
 * linear constraints. This adapter translates that representation directly to
 * an {@link ExpressionsBasedModel}; no temporary MPS or solution files are
 * required.</p>
 */
public final class LinearSolver extends javax.constraints.linear.LinearSolver {

    public static final String OJALGO_VERSION = "57.4.0";
    private static final double INTEGER_TOLERANCE = 1.0e-5;

    @Override
    public Objective getDefaultOptimizationObjective() {
        return Objective.MINIMIZE;
    }

    @Override
    public String getVersion() {
        return "ojAlgo " + OJALGO_VERSION;
    }

    /**
     * Kept only to satisfy the legacy command-line-oriented base class.
     * ojAlgo runs in this JVM and needs no executable command.
     */
    @Override
    public String getCommanLine() {
        return null;
    }

    /**
     * Kept only to satisfy the legacy file-result contract. Direct solves do
     * not create an output file.
     */
    @Override
    public HashMap<String, String> readResults() {
        return null;
    }

    @Override
    public Solution findOptimalSolution(Objective direction, Var objectiveVar) {
        javax.constraints.impl.Problem problem = linearProblem();
        problem.add(objectiveVar);
        return solveModel(direction, objectiveVar, getTimeLimitGlobal());
    }

    @Override
    public Solution findOptimalSolution(Objective direction, VarReal objectiveVar) {
        javax.constraints.impl.Problem problem = linearProblem();
        problem.add(objectiveVar);
        return solveModel(direction, objectiveVar, getTimeLimitGlobal());
    }

    /**
     * Supports callers that already have an MPS file. For ordinary JSR-331
     * use, {@code findOptimalSolution} uses the safer direct translation path.
     */
    @Override
    public Solution solve(File file) {
        return solve(file, -1);
    }

    /**
     * Parses an ojAlgo-supported model file and transfers matching variable
     * values back to the current JSR-331 problem.
     */
    @Override
    public Solution solve(File file, int timeoutMilliSeconds) {
        ExpressionsBasedModel model = ExpressionsBasedModel.parse(file);
        try {
            applyTimeLimit(model, timeoutMilliSeconds);
            Optimisation.Result result = model.minimise();
            if (!result.getState().isFeasible()) {
                return null;
            }
            copyImportedValues(model, result);
            return new javax.constraints.impl.search.Solution(this, 1);
        } finally {
            model.dispose();
        }
    }

    private Solution solveModel(
            Objective direction,
            ConstrainedVariable objectiveVar,
            int timeoutMilliSeconds) {

        if (direction == null) {
            throw new IllegalArgumentException("The optimization direction must not be null");
        }

        javax.constraints.impl.Problem problem = linearProblem();
        ExpressionsBasedModel model = new ExpressionsBasedModel();
        Map<ConstrainedVariable, Variable> variables = new IdentityHashMap<>();
        Set<String> names = new LinkedHashSet<>();

        try {
            Var[] integerVariables = problem.getVars();
            if (integerVariables != null) {
                for (Var var : integerVariables) {
                    ensureVariable(model, variables, names, var);
                }
            }
            VarReal[] realVariables = problem.getVarReals();
            if (realVariables != null) {
                for (VarReal var : realVariables) {
                    ensureVariable(model, variables, names, var);
                }
            }

            Variable ojObjective = ensureVariable(
                    model, variables, names, objectiveVar);
            ojObjective.weight(1.0);

            javax.constraints.Constraint[] constraints = problem.getConstraints();
            for (int index = 0; index < constraints.length; index++) {
                addConstraint(model, variables, names,
                        (javax.constraints.impl.Constraint) constraints[index], index);
            }

            applyTimeLimit(model, timeoutMilliSeconds);
            Optimisation.Result result = direction == Objective.MAXIMIZE
                    ? model.maximise()
                    : model.minimise();

            if (!result.getState().isFeasible()) {
                return null;
            }

            copyValues(model, result, variables);
            return new javax.constraints.impl.search.Solution(this, 1);
        } finally {
            model.dispose();
        }
    }

    private javax.constraints.impl.Problem linearProblem() {
        if (!(getProblem() instanceof javax.constraints.impl.Problem)) {
            throw new IllegalStateException(
                    "ojAlgo requires the JSR-331 common linear Problem implementation");
        }
        return (javax.constraints.impl.Problem) getProblem();
    }

    private static Variable ensureVariable(
            ExpressionsBasedModel model,
            Map<ConstrainedVariable, Variable> variables,
            Set<String> names,
            ConstrainedVariable jsrVariable) {

        Variable existing = variables.get(jsrVariable);
        if (existing != null) {
            return existing;
        }

        String name = uniqueName(jsrVariable.getName(), names, variables.size());
        Variable created = model.newVariable(name);
        if (jsrVariable instanceof Var) {
            Var integer = (Var) jsrVariable;
            created.lower(integer.getMin()).upper(integer.getMax()).integer(true);
        } else if (jsrVariable instanceof VarReal) {
            VarReal real = (VarReal) jsrVariable;
            created.lower(real.getMin()).upper(real.getMax());
        } else {
            throw new NonLinearProblemException(
                    "Unsupported constrained variable type: " + jsrVariable.getClass().getName());
        }

        variables.put(jsrVariable, created);
        return created;
    }

    private static void addConstraint(
            ExpressionsBasedModel model,
            Map<ConstrainedVariable, Variable> variables,
            Set<String> names,
            javax.constraints.impl.Constraint constraint,
            int index) {

        ConstrainedVariable[] jsrVariables = constraint.getVars();
        double[] coefficients = constraint.getCoefficients();
        if (jsrVariables == null || coefficients == null
                || jsrVariables.length != coefficients.length) {
            throw new IllegalArgumentException(
                    "Malformed linear constraint " + constraint.getName());
        }

        String expressionName = constraint.getName();
        if (expressionName == null || expressionName.isBlank()) {
            expressionName = "constraint_" + (index + 1);
        }
        Expression expression = model.newExpression(expressionName + "_" + index);
        boolean integralExpression = true;

        for (int i = 0; i < jsrVariables.length; i++) {
            ConstrainedVariable jsrVariable = jsrVariables[i];
            Variable ojVariable = ensureVariable(model, variables, names, jsrVariable);
            expression.set(ojVariable, coefficients[i]);
            integralExpression &= jsrVariable instanceof Var
                    && Math.abs(coefficients[i] - Math.rint(coefficients[i]))
                    <= INTEGER_TOLERANCE;
        }

        applyOperator(expression, constraint.getOper(), constraint.getValue(), integralExpression);
    }

    private static void applyOperator(
            Expression expression, String operator, double value, boolean integralExpression) {

        if (operator == null) {
            throw new IllegalArgumentException("A linear constraint has no operator");
        }

        switch (operator.trim().toLowerCase()) {
            case "=":
            case "==":
            case "is":
                expression.level(value);
                return;
            case "<=":
                expression.upper(value);
                return;
            case ">=":
                expression.lower(value);
                return;
            case "<":
                expression.upper(strictIntegerBound(value, -1, integralExpression, operator));
                return;
            case ">":
                expression.lower(strictIntegerBound(value, 1, integralExpression, operator));
                return;
            case "!=":
            case "<>":
            case "is not":
                throw new NonLinearProblemException(
                        "ojAlgo's linear model cannot represent a disjunctive != constraint");
            default:
                throw new IllegalArgumentException(
                        "Unsupported linear constraint operator: " + operator);
        }
    }

    private static double strictIntegerBound(
            double value, int adjustment, boolean integralExpression, String operator) {

        double rounded = Math.rint(value);
        if (!integralExpression || Math.abs(value - rounded) > INTEGER_TOLERANCE) {
            throw new NonLinearProblemException(
                    "Strict operator " + operator
                    + " is supported only for integral expressions and integral bounds");
        }
        return rounded + adjustment;
    }

    private static void applyTimeLimit(ExpressionsBasedModel model, int timeoutMilliSeconds) {
        if (timeoutMilliSeconds > 0) {
            model.options.time_abort = timeoutMilliSeconds;
        }
    }

    private static void copyValues(
            ExpressionsBasedModel model,
            Optimisation.Result result,
            Map<ConstrainedVariable, Variable> variables) {

        for (Map.Entry<ConstrainedVariable, Variable> entry : variables.entrySet()) {
            double value = result.doubleValue(model.indexOf(entry.getValue()));
            setJsrValue(entry.getKey(), value);
        }
    }

    private void copyImportedValues(
            ExpressionsBasedModel model, Optimisation.Result result) {

        Map<String, Double> values = new HashMap<>();
        for (Variable variable : model.getVariables()) {
            values.put(variable.getName(), result.doubleValue(model.indexOf(variable)));
        }

        javax.constraints.impl.Problem problem = linearProblem();
        Var[] integerVariables = problem.getVars();
        if (integerVariables != null) {
            for (Var variable : integerVariables) {
                Double value = findImportedValue(values, variable);
                if (value != null) {
                    setJsrValue(variable, value);
                }
            }
        }
        VarReal[] realVariables = problem.getVarReals();
        if (realVariables != null) {
            for (VarReal variable : realVariables) {
                Double value = findImportedValue(values, variable);
                if (value != null) {
                    setJsrValue(variable, value);
                }
            }
        }
    }

    private static Double findImportedValue(
            Map<String, Double> values, ConstrainedVariable variable) {
        Double value = values.get(variable.getName());
        return value != null ? value : values.get(variable.getId());
    }

    private static void setJsrValue(ConstrainedVariable variable, double value) {
        if (variable instanceof javax.constraints.impl.Var) {
            long rounded = Math.round(value);
            if (Math.abs(value - rounded) > INTEGER_TOLERANCE) {
                throw new IllegalStateException(
                        "ojAlgo returned a non-integral value for " + variable.getName()
                        + ": " + value);
            }
            if (rounded < Integer.MIN_VALUE || rounded > Integer.MAX_VALUE) {
                throw new ArithmeticException(
                        "Integer result is outside the JSR-331 int range: " + rounded);
            }
            ((javax.constraints.impl.Var) variable).setValue((int) rounded);
        } else if (variable instanceof javax.constraints.impl.VarReal) {
            ((javax.constraints.impl.VarReal) variable).setValue(value);
        }
    }

    private static String uniqueName(String preferred, Set<String> names, int index) {
        String base = preferred == null || preferred.isBlank()
                ? "variable_" + (index + 1)
                : preferred;
        String candidate = base;
        int suffix = 2;
        while (!names.add(candidate)) {
            candidate = base + "_" + suffix++;
        }
        return candidate;
    }
}
