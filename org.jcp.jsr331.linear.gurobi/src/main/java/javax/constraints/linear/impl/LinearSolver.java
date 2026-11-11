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

import com.gurobi.gurobi.GRB;
import com.gurobi.gurobi.GRBEnv;
import com.gurobi.gurobi.GRBException;
import com.gurobi.gurobi.GRBLinExpr;
import com.gurobi.gurobi.GRBModel;
import com.gurobi.gurobi.GRBVar;

/** JSR 331 linear and mixed-integer programming adapter for Gurobi Optimizer. */
public final class LinearSolver extends javax.constraints.linear.LinearSolver {

    private static final double INTEGER_TOLERANCE = 1.0e-5;

    @Override
    public Objective getDefaultOptimizationObjective() {
        return Objective.MINIMIZE;
    }

    @Override
    public String getVersion() {
        return "Gurobi " + GRB.VERSION_MAJOR + "." + GRB.VERSION_MINOR
                + "." + GRB.VERSION_TECHNICAL;
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

    @Override
    public Solution solve(File file) {
        return solve(file, -1);
    }

    @Override
    public Solution solve(File file, int timeoutMilliSeconds) {
        if (file == null || !file.isFile()) {
            throw new IllegalArgumentException("Model file does not exist: " + file);
        }

        GRBEnv environment = createEnvironment();
        GRBModel model = null;
        try {
            model = new GRBModel(environment, file.getAbsolutePath());
            configure(model, timeoutMilliSeconds);
            model.optimize();
            if (!hasSolution(model)) {
                return null;
            }
            copyImportedValues(model);
            return new javax.constraints.impl.search.Solution(this, 1);
        } catch (GRBException exception) {
            throw gurobiFailure("import and solve model " + file, exception);
        } finally {
            if (model != null) {
                model.dispose();
            }
            disposeEnvironment(environment);
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
        GRBEnv environment = createEnvironment();
        GRBModel model = null;
        Map<ConstrainedVariable, GRBVar> variables = new IdentityHashMap<>();
        Set<String> names = new LinkedHashSet<>();

        try {
            model = new GRBModel(environment);
            configure(model, timeoutMilliSeconds);
            if (problem.getName() != null && !problem.getName().isBlank()) {
                model.set(GRB.StringAttr.ModelName, problem.getName());
            }

            Var[] integerVariables = problem.getVars();
            if (integerVariables != null) {
                for (Var variable : integerVariables) {
                    ensureVariable(model, variables, names, variable);
                }
            }
            VarReal[] realVariables = problem.getVarReals();
            if (realVariables != null) {
                for (VarReal variable : realVariables) {
                    ensureVariable(model, variables, names, variable);
                }
            }

            GRBVar nativeObjective = ensureVariable(
                    model, variables, names, objectiveVar);

            javax.constraints.Constraint[] constraints = problem.getConstraints();
            for (int index = 0; index < constraints.length; index++) {
                addConstraint(model, variables, names,
                        (javax.constraints.impl.Constraint) constraints[index], index);
            }

            GRBLinExpr objective = new GRBLinExpr();
            objective.addTerm(1.0, nativeObjective);
            model.setObjective(objective,
                    direction == Objective.MAXIMIZE ? GRB.MAXIMIZE : GRB.MINIMIZE);
            model.optimize();
            if (!hasSolution(model)) {
                return null;
            }

            for (Map.Entry<ConstrainedVariable, GRBVar> entry : variables.entrySet()) {
                setJsrValue(entry.getKey(), entry.getValue().get(GRB.DoubleAttr.X));
            }
            return new javax.constraints.impl.search.Solution(this, 1);
        } catch (GRBException exception) {
            throw gurobiFailure("build and solve the JSR 331 model", exception);
        } finally {
            if (model != null) {
                model.dispose();
            }
            disposeEnvironment(environment);
        }
    }

    private static GRBEnv createEnvironment() {
        GRBEnv environment = null;
        try {
            environment = new GRBEnv(true);
            environment.set(GRB.IntParam.OutputFlag, 0);
            environment.start();
            return environment;
        } catch (GRBException exception) {
            if (environment != null) {
                disposeEnvironment(environment);
            }
            throw gurobiFailure("create a licensed Gurobi environment", exception);
        } catch (UnsatisfiedLinkError error) {
            if (environment != null) {
                disposeEnvironment(environment);
            }
            throw new IllegalStateException(
                    "Gurobi native libraries are unavailable or incompatible", error);
        }
    }

    private static void disposeEnvironment(GRBEnv environment) {
        try {
            environment.dispose();
        } catch (GRBException ignored) {
            // Preserve the solve result or the original initialization failure.
        }
    }

    private static void configure(GRBModel model, int timeoutMilliSeconds)
            throws GRBException {
        if (timeoutMilliSeconds > 0) {
            model.set(GRB.DoubleParam.TimeLimit, timeoutMilliSeconds / 1000.0);
        }
    }

    private static boolean hasSolution(GRBModel model) throws GRBException {
        return model.get(GRB.IntAttr.SolCount) > 0;
    }

    private static GRBVar ensureVariable(
            GRBModel model,
            Map<ConstrainedVariable, GRBVar> variables,
            Set<String> names,
            ConstrainedVariable jsrVariable) throws GRBException {

        GRBVar existing = variables.get(jsrVariable);
        if (existing != null) {
            return existing;
        }

        String name = uniqueName(jsrVariable.getName(), names, variables.size());
        GRBVar created;
        if (jsrVariable instanceof Var integer) {
            created = model.addVar(integer.getMin(), integer.getMax(), 0.0,
                    GRB.INTEGER, name);
        } else if (jsrVariable instanceof VarReal real) {
            created = model.addVar(real.getMin(), real.getMax(), 0.0,
                    GRB.CONTINUOUS, name);
        } else {
            throw new NonLinearProblemException(
                    "Unsupported constrained variable type: "
                    + jsrVariable.getClass().getName());
        }

        variables.put(jsrVariable, created);
        return created;
    }

    private static void addConstraint(
            GRBModel model,
            Map<ConstrainedVariable, GRBVar> variables,
            Set<String> names,
            javax.constraints.impl.Constraint constraint,
            int index) throws GRBException {

        ConstrainedVariable[] jsrVariables = constraint.getVars();
        double[] coefficients = constraint.getCoefficients();
        if (jsrVariables == null || coefficients == null
                || jsrVariables.length != coefficients.length) {
            throw new IllegalArgumentException(
                    "Malformed linear constraint " + constraint.getName());
        }

        boolean integralExpression = true;
        Map<GRBVar, Double> combined = new IdentityHashMap<>();
        for (int i = 0; i < jsrVariables.length; i++) {
            GRBVar variable = ensureVariable(
                    model, variables, names, jsrVariables[i]);
            combined.merge(variable, coefficients[i], Double::sum);
            integralExpression &= jsrVariables[i] instanceof Var
                    && isIntegral(coefficients[i]);
        }

        GRBLinExpr expression = new GRBLinExpr();
        for (Map.Entry<GRBVar, Double> entry : combined.entrySet()) {
            if (entry.getValue() != 0.0) {
                expression.addTerm(entry.getValue(), entry.getKey());
            }
        }

        String name = constraint.getName();
        if (name == null || name.isBlank()) {
            name = "constraint_" + (index + 1);
        }
        name += "_" + index;
        String operator = constraint.getOper();
        double value = constraint.getValue();
        if (operator == null) {
            throw new IllegalArgumentException("A linear constraint has no operator");
        }
        switch (operator.trim().toLowerCase(Locale.ROOT)) {
            case "=", "==", "is" -> model.addConstr(expression, GRB.EQUAL, value, name);
            case "<=" -> model.addConstr(expression, GRB.LESS_EQUAL, value, name);
            case ">=" -> model.addConstr(expression, GRB.GREATER_EQUAL, value, name);
            case "<" -> model.addConstr(expression, GRB.LESS_EQUAL,
                    strictIntegerBound(value, -1, integralExpression, operator), name);
            case ">" -> model.addConstr(expression, GRB.GREATER_EQUAL,
                    strictIntegerBound(value, 1, integralExpression, operator), name);
            case "!=", "<>", "is not" -> throw new NonLinearProblemException(
                    "Gurobi cannot represent a disjunctive != constraint as one linear row");
            default -> throw new IllegalArgumentException(
                    "Unsupported linear constraint operator: " + operator);
        }
    }

    private void copyImportedValues(GRBModel model) throws GRBException {
        GRBVar[] nativeVariables = model.getVars();
        String[] names = model.get(GRB.StringAttr.VarName, nativeVariables);
        double[] nativeValues = model.get(GRB.DoubleAttr.X, nativeVariables);
        Map<String, Double> values = new HashMap<>();
        for (int index = 0; index < nativeVariables.length; index++) {
            values.put(names[index], nativeValues[index]);
        }

        javax.constraints.impl.Problem problem = linearProblem();
        Var[] integerVariables = problem.getVars();
        if (integerVariables != null) {
            for (Var variable : integerVariables) {
                copyImportedValue(values, variable);
            }
        }
        VarReal[] realVariables = problem.getVarReals();
        if (realVariables != null) {
            for (VarReal variable : realVariables) {
                copyImportedValue(values, variable);
            }
        }
    }

    private static void copyImportedValue(
            Map<String, Double> values, ConstrainedVariable variable) {
        Double value = values.get(variable.getName());
        if (value == null) {
            value = values.get(variable.getId());
        }
        if (value != null) {
            setJsrValue(variable, value);
        }
    }

    private javax.constraints.impl.Problem linearProblem() {
        if (!(getProblem() instanceof javax.constraints.impl.Problem problem)) {
            throw new IllegalStateException(
                    "Gurobi requires the JSR 331 common linear Problem implementation");
        }
        return problem;
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
                        "Gurobi returned a non-integral value for "
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

    private static IllegalStateException gurobiFailure(String action, GRBException cause) {
        return new IllegalStateException("Gurobi could not " + action + ": "
                + cause.getMessage() + " (error " + cause.getErrorCode() + ")", cause);
    }
}
