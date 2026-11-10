package javax.constraints.linear.impl;

import java.io.File;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Objective;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.linear.NonLinearProblemException;

import com.google.ortools.Loader;
import com.google.ortools.init.OrToolsVersion;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

/** JSR 331 linear and mixed-integer programming adapter for Google OR-Tools. */
public final class LinearSolver extends javax.constraints.linear.LinearSolver {

    public static final String SOLVER_PROPERTY = "jsr331.ortools.solver";
    public static final String DEFAULT_BACKEND = "SCIP";

    private static final double INTEGER_TOLERANCE = 1.0e-5;

    static {
        Loader.loadNativeLibraries();
    }

    @Override
    public Objective getDefaultOptimizationObjective() {
        return Objective.MINIMIZE;
    }

    @Override
    public String getVersion() {
        MPSolver solver = createSolver(false);
        try {
            return "Google OR-Tools " + OrToolsVersion.getVersionString()
                    + " using " + solver.solverVersion();
        } finally {
            solver.delete();
        }
    }

    @Override
    public String getCommanLine() {
        return null;
    }

    @Override
    public HashMap<String, String> readResults() {
        return null;
    }

    @Override
    public Solution findOptimalSolution(Objective direction, Var objectiveVar) {
        linearProblem().add(objectiveVar);
        return solveModel(direction, objectiveVar, getTimeLimitGlobal());
    }

    @Override
    public Solution findOptimalSolution(Objective direction, VarReal objectiveVar) {
        linearProblem().add(objectiveVar);
        return solveModel(direction, objectiveVar, getTimeLimitGlobal());
    }

    /** OR-Tools' Java MPSolver API does not import MPS files. */
    @Override
    public Solution solve(File file) {
        throw new UnsupportedOperationException(
                "The OR-Tools Java MPSolver API does not import MPS files");
    }

    /** OR-Tools' Java MPSolver API does not import MPS files. */
    @Override
    public Solution solve(File file, int timeoutMilliSeconds) {
        throw new UnsupportedOperationException(
                "The OR-Tools Java MPSolver API does not import MPS files");
    }

    private Solution solveModel(
            Objective direction,
            ConstrainedVariable objectiveVar,
            int timeoutMilliSeconds) {

        if (direction == null) {
            throw new IllegalArgumentException("The optimization direction must not be null");
        }

        javax.constraints.impl.Problem problem = linearProblem();
        boolean hasIntegerVariables = problem.getVars() != null
                && problem.getVars().length > 0;
        MPSolver solver = createSolver(hasIntegerVariables);
        Map<ConstrainedVariable, MPVariable> variables = new IdentityHashMap<>();
        Set<String> names = new LinkedHashSet<>();

        try {
            if (timeoutMilliSeconds > 0) {
                solver.setTimeLimit(timeoutMilliSeconds);
            }

            Var[] integerVariables = problem.getVars();
            if (integerVariables != null) {
                for (Var variable : integerVariables) {
                    ensureVariable(solver, variables, names, variable);
                }
            }
            VarReal[] realVariables = problem.getVarReals();
            if (realVariables != null) {
                for (VarReal variable : realVariables) {
                    ensureVariable(solver, variables, names, variable);
                }
            }

            MPVariable nativeObjective = ensureVariable(
                    solver, variables, names, objectiveVar);

            javax.constraints.Constraint[] constraints = problem.getConstraints();
            for (int index = 0; index < constraints.length; index++) {
                addConstraint(solver, variables, names,
                        (javax.constraints.impl.Constraint) constraints[index], index);
            }

            MPObjective objective = solver.objective();
            objective.setCoefficient(nativeObjective, 1.0);
            if (direction == Objective.MAXIMIZE) {
                objective.setMaximization();
            } else {
                objective.setMinimization();
            }

            MPSolver.ResultStatus status = solver.solve();
            if (status != MPSolver.ResultStatus.OPTIMAL
                    && status != MPSolver.ResultStatus.FEASIBLE) {
                return null;
            }

            for (Map.Entry<ConstrainedVariable, MPVariable> entry : variables.entrySet()) {
                setJsrValue(entry.getKey(), entry.getValue().solutionValue());
            }
            return new javax.constraints.impl.search.Solution(this, 1);
        } finally {
            solver.delete();
        }
    }

    private static MPSolver createSolver(boolean integerModel) {
        String backend = System.getProperty(SOLVER_PROPERTY, DEFAULT_BACKEND).trim();
        if (backend.isEmpty()) {
            backend = DEFAULT_BACKEND;
        }

        MPSolver solver = MPSolver.createSolver(backend);
        if (solver == null) {
            throw new IllegalStateException(
                    "OR-Tools MPSolver backend is unavailable: " + backend);
        }
        if (integerModel && !solver.isMip()) {
            solver.delete();
            throw new IllegalArgumentException(
                    "OR-Tools backend " + backend
                    + " cannot solve a model containing integer variables");
        }
        return solver;
    }

    private javax.constraints.impl.Problem linearProblem() {
        if (!(getProblem() instanceof javax.constraints.impl.Problem problem)) {
            throw new IllegalStateException(
                    "OR-Tools requires the JSR 331 common linear Problem implementation");
        }
        return problem;
    }

    private static MPVariable ensureVariable(
            MPSolver solver,
            Map<ConstrainedVariable, MPVariable> variables,
            Set<String> names,
            ConstrainedVariable jsrVariable) {

        MPVariable existing = variables.get(jsrVariable);
        if (existing != null) {
            return existing;
        }

        String name = uniqueName(jsrVariable.getName(), names, variables.size());
        MPVariable created;
        if (jsrVariable instanceof Var integer) {
            created = solver.makeIntVar(integer.getMin(), integer.getMax(), name);
        } else if (jsrVariable instanceof VarReal real) {
            created = solver.makeNumVar(real.getMin(), real.getMax(), name);
        } else {
            throw new NonLinearProblemException(
                    "Unsupported constrained variable type: "
                    + jsrVariable.getClass().getName());
        }

        variables.put(jsrVariable, created);
        return created;
    }

    private static void addConstraint(
            MPSolver solver,
            Map<ConstrainedVariable, MPVariable> variables,
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

        boolean integralExpression = true;
        Map<MPVariable, Double> combined = new IdentityHashMap<>();
        for (int i = 0; i < jsrVariables.length; i++) {
            MPVariable variable = ensureVariable(
                    solver, variables, names, jsrVariables[i]);
            combined.merge(variable, coefficients[i], Double::sum);
            integralExpression &= jsrVariables[i] instanceof Var
                    && isIntegral(coefficients[i]);
        }

        Bounds bounds = bounds(constraint.getOper(), constraint.getValue(),
                integralExpression, MPSolver.infinity());
        String constraintName = constraint.getName();
        if (constraintName == null || constraintName.isBlank()) {
            constraintName = "constraint_" + (index + 1);
        }
        MPConstraint nativeConstraint = solver.makeConstraint(
                bounds.lower(), bounds.upper(), constraintName + "_" + index);
        for (Map.Entry<MPVariable, Double> entry : combined.entrySet()) {
            if (entry.getValue() != 0.0) {
                nativeConstraint.setCoefficient(entry.getKey(), entry.getValue());
            }
        }
    }

    private static Bounds bounds(
            String operator, double value, boolean integralExpression, double infinity) {
        if (operator == null) {
            throw new IllegalArgumentException("A linear constraint has no operator");
        }
        return switch (operator.trim().toLowerCase(Locale.ROOT)) {
            case "=", "==", "is" -> new Bounds(value, value);
            case "<=" -> new Bounds(-infinity, value);
            case ">=" -> new Bounds(value, infinity);
            case "<" -> new Bounds(-infinity,
                    strictIntegerBound(value, -1, integralExpression, operator));
            case ">" -> new Bounds(
                    strictIntegerBound(value, 1, integralExpression, operator), infinity);
            case "!=", "<>", "is not" -> throw new NonLinearProblemException(
                    "OR-Tools cannot represent a disjunctive != constraint as one linear row");
            default -> throw new IllegalArgumentException(
                    "Unsupported linear constraint operator: " + operator);
        };
    }

    private static double strictIntegerBound(
            double value, int adjustment, boolean integralExpression, String operator) {
        if (!integralExpression || !isIntegral(value)) {
            throw new NonLinearProblemException(
                    "Strict operator " + operator
                    + " is supported only for integral expressions and integral bounds");
        }
        return Math.rint(value) + adjustment;
    }

    private static boolean isIntegral(double value) {
        return Math.abs(value - Math.rint(value)) <= INTEGER_TOLERANCE;
    }

    private static void setJsrValue(ConstrainedVariable variable, double value) {
        if (variable instanceof javax.constraints.impl.Var integer) {
            long rounded = Math.round(value);
            if (Math.abs(value - rounded) > INTEGER_TOLERANCE) {
                throw new IllegalStateException(
                        "OR-Tools returned a non-integral value for "
                        + variable.getName() + ": " + value);
            }
            if (rounded < Integer.MIN_VALUE || rounded > Integer.MAX_VALUE) {
                throw new ArithmeticException(
                        "Integer result is outside the JSR 331 int range: " + rounded);
            }
            integer.setValue((int) rounded);
        } else if (variable instanceof javax.constraints.impl.VarReal real) {
            real.setValue(value);
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

    private record Bounds(double lower, double upper) {
    }
}
