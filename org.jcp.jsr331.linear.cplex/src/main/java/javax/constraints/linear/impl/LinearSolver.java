package javax.constraints.linear.impl;

import java.io.File;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
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

import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

/** JSR 331 linear and mixed-integer programming adapter for IBM ILOG CPLEX. */
public final class LinearSolver extends javax.constraints.linear.LinearSolver {

    private static final double INTEGER_TOLERANCE = 1.0e-5;

    @Override
    public Objective getDefaultOptimizationObjective() {
        return Objective.MINIMIZE;
    }

    @Override
    public String getVersion() {
        IloCplex cplex = createCplex();
        try {
            return "IBM ILOG CPLEX " + cplex.getVersion();
        } catch (IloException exception) {
            throw cplexFailure("read the CPLEX version", exception);
        } finally {
            cplex.end();
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

    @Override
    public Solution solve(File file) {
        return solve(file, -1);
    }

    @Override
    public Solution solve(File file, int timeoutMilliSeconds) {
        if (file == null || !file.isFile()) {
            throw new IllegalArgumentException("Model file does not exist: " + file);
        }

        IloCplex cplex = createCplex();
        try {
            configure(cplex, timeoutMilliSeconds);
            cplex.importModel(file.getAbsolutePath());
            if (!cplex.solve()) {
                return null;
            }
            copyImportedValues(cplex);
            return new javax.constraints.impl.search.Solution(this, 1);
        } catch (IloException exception) {
            throw cplexFailure("import and solve model " + file, exception);
        } finally {
            cplex.end();
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
        IloCplex cplex = createCplex();
        Map<ConstrainedVariable, IloNumVar> variables = new IdentityHashMap<>();
        Set<String> names = new LinkedHashSet<>();

        try {
            configure(cplex, timeoutMilliSeconds);

            Var[] integerVariables = problem.getVars();
            if (integerVariables != null) {
                for (Var variable : integerVariables) {
                    ensureVariable(cplex, variables, names, variable);
                }
            }
            VarReal[] realVariables = problem.getVarReals();
            if (realVariables != null) {
                for (VarReal variable : realVariables) {
                    ensureVariable(cplex, variables, names, variable);
                }
            }

            IloNumVar nativeObjective = ensureVariable(
                    cplex, variables, names, objectiveVar);

            javax.constraints.Constraint[] constraints = problem.getConstraints();
            for (int index = 0; index < constraints.length; index++) {
                addConstraint(cplex, variables, names,
                        (javax.constraints.impl.Constraint) constraints[index], index);
            }

            IloLinearNumExpr objective = cplex.linearNumExpr();
            objective.addTerm(1.0, nativeObjective);
            if (direction == Objective.MAXIMIZE) {
                cplex.addMaximize(objective);
            } else {
                cplex.addMinimize(objective);
            }

            if (!cplex.solve()) {
                return null;
            }

            for (Map.Entry<ConstrainedVariable, IloNumVar> entry : variables.entrySet()) {
                setJsrValue(entry.getKey(), cplex.getValue(entry.getValue()));
            }
            return new javax.constraints.impl.search.Solution(this, 1);
        } catch (IloException exception) {
            throw cplexFailure("build and solve the JSR 331 model", exception);
        } finally {
            cplex.end();
        }
    }

    private static IloCplex createCplex() {
        try {
            return new IloCplex();
        } catch (IloException exception) {
            throw cplexFailure("create a CPLEX environment", exception);
        } catch (UnsatisfiedLinkError error) {
            throw new IllegalStateException(
                    "CPLEX native libraries are unavailable. Configure java.library.path "
                    + "or the operating-system library path for the installed CPLEX version.",
                    error);
        }
    }

    private static void configure(IloCplex cplex, int timeoutMilliSeconds)
            throws IloException {
        cplex.setOut(null);
        cplex.setWarning(null);
        if (timeoutMilliSeconds > 0) {
            cplex.setParam(IloCplex.DoubleParam.TiLim, timeoutMilliSeconds / 1000.0);
        }
    }

    private static IloNumVar ensureVariable(
            IloCplex cplex,
            Map<ConstrainedVariable, IloNumVar> variables,
            Set<String> names,
            ConstrainedVariable jsrVariable) throws IloException {

        IloNumVar existing = variables.get(jsrVariable);
        if (existing != null) {
            return existing;
        }

        String name = uniqueName(jsrVariable.getName(), names, variables.size());
        IloNumVar created;
        if (jsrVariable instanceof Var integer) {
            created = cplex.intVar(integer.getMin(), integer.getMax(), name);
        } else if (jsrVariable instanceof VarReal real) {
            created = cplex.numVar(real.getMin(), real.getMax(), name);
        } else {
            throw new NonLinearProblemException(
                    "Unsupported constrained variable type: "
                    + jsrVariable.getClass().getName());
        }

        variables.put(jsrVariable, created);
        return created;
    }

    private static void addConstraint(
            IloCplex cplex,
            Map<ConstrainedVariable, IloNumVar> variables,
            Set<String> names,
            javax.constraints.impl.Constraint constraint,
            int index) throws IloException {

        ConstrainedVariable[] jsrVariables = constraint.getVars();
        double[] coefficients = constraint.getCoefficients();
        if (jsrVariables == null || coefficients == null
                || jsrVariables.length != coefficients.length) {
            throw new IllegalArgumentException(
                    "Malformed linear constraint " + constraint.getName());
        }

        boolean integralExpression = true;
        Map<IloNumVar, Double> combined = new IdentityHashMap<>();
        for (int i = 0; i < jsrVariables.length; i++) {
            IloNumVar variable = ensureVariable(
                    cplex, variables, names, jsrVariables[i]);
            combined.merge(variable, coefficients[i], Double::sum);
            integralExpression &= jsrVariables[i] instanceof Var
                    && isIntegral(coefficients[i]);
        }

        IloLinearNumExpr expression = cplex.linearNumExpr();
        for (Map.Entry<IloNumVar, Double> entry : combined.entrySet()) {
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
            case "=", "==", "is" -> cplex.addEq(expression, value, name);
            case "<=" -> cplex.addLe(expression, value, name);
            case ">=" -> cplex.addGe(expression, value, name);
            case "<" -> cplex.addLe(expression,
                    strictIntegerBound(value, -1, integralExpression, operator), name);
            case ">" -> cplex.addGe(expression,
                    strictIntegerBound(value, 1, integralExpression, operator), name);
            case "!=", "<>", "is not" -> throw new NonLinearProblemException(
                    "CPLEX cannot represent a disjunctive != constraint as one linear row");
            default -> throw new IllegalArgumentException(
                    "Unsupported linear constraint operator: " + operator);
        }
    }

    private void copyImportedValues(IloCplex cplex) throws IloException {
        Iterator<?> matrices = cplex.LPMatrixIterator();
        if (!matrices.hasNext()) {
            throw new IllegalArgumentException("The imported model contains no LP matrix");
        }
        IloLPMatrix matrix = (IloLPMatrix) matrices.next();
        Map<String, Double> values = new HashMap<>();
        for (IloNumVar variable : matrix.getNumVars()) {
            values.put(variable.getName(), cplex.getValue(variable));
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
                    "CPLEX requires the JSR 331 common linear Problem implementation");
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
                        "CPLEX returned a non-integral value for "
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

    private static IllegalStateException cplexFailure(String action, IloException cause) {
        return new IllegalStateException("CPLEX could not " + action + ": "
                + cause.getMessage(), cause);
    }
}
