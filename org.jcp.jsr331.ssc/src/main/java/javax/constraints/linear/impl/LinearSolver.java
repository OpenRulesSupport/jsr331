package javax.constraints.linear.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarReal;

import org.ssclab.pl.milp.ConsType;
import org.ssclab.pl.milp.GoalType;
import org.ssclab.pl.milp.LP;
import org.ssclab.pl.milp.LinearObjectiveFunction;
import org.ssclab.pl.milp.MILP;
import org.ssclab.pl.milp.SolutionType;

/** JSR 331 adapter for SSC 4.7.8. */
public class LinearSolver extends javax.constraints.linear.LinearSolver {

    public static final String JSR331_LINEAR_SOLVER_VERSION = "SSC-LP v.4.7.8";
    private static final double REAL_STRICT_OFFSET = 1.0e-7;
    private static final double INTEGER_TOLERANCE = 1.0e-6;

    private Problem problem;
    private ArrayList<ConstrainedVariable> variables;
    private Map<ConstrainedVariable, Integer> variableIndexes;
    private org.ssclab.pl.milp.Solution sscSolution;

    @Override
    public Solution findOptimalSolution(Objective objectiveDirection, Var objectiveVar) {
        return optimize(objectiveDirection, objectiveVar);
    }

    @Override
    public Solution findOptimalSolution(Objective objectiveDirection, VarReal objectiveVar) {
        return optimize(objectiveDirection, objectiveVar);
    }

    private Solution optimize(Objective objectiveDirection, ConstrainedVariable objectiveVar) {
        try {
            problem = getProblem();
            if (objectiveVar instanceof Var) {
                problem.add((Var) objectiveVar);
            } else {
                problem.add((VarReal) objectiveVar);
            }
            initializeVariables();

            double[] objectiveCoefficients = new double[variables.size()];
            objectiveCoefficients[indexOf(objectiveVar)] = 1.0;
            GoalType goal = toGoalType(objectiveDirection);
            LinearObjectiveFunction objective = new LinearObjectiveFunction(objectiveCoefficients, goal);
            ArrayList<org.ssclab.pl.milp.Constraint> constraints = createConstraints();

            SolutionType result;
            Var[] intVariables = problem.getVars();
            if (intVariables == null || intVariables.length == 0) {
                LP solver = new LP(objective, constraints);
                result = solver.resolve();
                if (result == SolutionType.OPTIMUM || result == SolutionType.FEASIBLE) {
                    sscSolution = solver.getSolution();
                }
            } else {
                constraints.add(createIntegerTypeConstraint());
                MILP solver = new MILP(objective, constraints);
                result = solver.resolve();
                if (result == SolutionType.OPTIMUM || result == SolutionType.FEASIBLE) {
                    sscSolution = solver.getSolution();
                }
            }

            if (sscSolution == null) {
                log("SSC did not find a solution: " + result);
                return null;
            }
            return createSolution(sscSolution.getValuesSolution());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("SSC failed to solve the linear problem", e);
        }
    }

    private void initializeVariables() {
        variables = new ArrayList<ConstrainedVariable>();
        variableIndexes = new IdentityHashMap<ConstrainedVariable, Integer>();

        Var[] intVariables = problem.getVars();
        if (intVariables != null) {
            for (Var var : intVariables) {
                addVariable(var);
            }
        }
        VarReal[] realVariables = problem.getVarReals();
        if (realVariables != null) {
            for (VarReal var : realVariables) {
                addVariable(var);
            }
        }
    }

    private void addVariable(ConstrainedVariable variable) {
        variableIndexes.put(variable, variables.size());
        variables.add(variable);
    }

    private int indexOf(ConstrainedVariable variable) {
        Integer index = variableIndexes.get(variable);
        if (index == null) {
            throw new IllegalArgumentException("Variable is not part of the problem: " + variable.getName());
        }
        return index.intValue();
    }

    private ArrayList<org.ssclab.pl.milp.Constraint> createConstraints() throws Exception {
        ArrayList<org.ssclab.pl.milp.Constraint> result =
                new ArrayList<org.ssclab.pl.milp.Constraint>();

        for (javax.constraints.Constraint source : problem.getConstraints()) {
            javax.constraints.impl.Constraint constraint =
                    (javax.constraints.impl.Constraint) source;
            double[] row = new double[variables.size()];
            ConstrainedVariable[] constrainedVariables = constraint.getVars();
            double[] coefficients = constraint.getCoefficients();
            for (int i = 0; i < constrainedVariables.length; i++) {
                row[indexOf(constrainedVariables[i])] += coefficients[i];
            }

            double rhs = constraint.getValue();
            String operator = constraint.getOper();
            ConsType relation;
            if ("=".equals(operator)) {
                relation = ConsType.EQ;
            } else if (">=".equals(operator)) {
                relation = ConsType.GE;
            } else if ("<=".equals(operator)) {
                relation = ConsType.LE;
            } else if (">".equals(operator)) {
                relation = ConsType.GE;
                rhs += strictOffset(constrainedVariables);
            } else if ("<".equals(operator)) {
                relation = ConsType.LE;
                rhs -= strictOffset(constrainedVariables);
            } else {
                throw new IllegalArgumentException("SSC does not support linear operator: " + operator);
            }
            result.add(new org.ssclab.pl.milp.Constraint(row, relation, rhs, constraint.getName()));
        }

        result.add(createBoundsConstraint(ConsType.LOWER));
        result.add(createBoundsConstraint(ConsType.UPPER));
        return result;
    }

    private double strictOffset(ConstrainedVariable[] constrainedVariables) {
        for (ConstrainedVariable variable : constrainedVariables) {
            if (variable instanceof VarReal) {
                return REAL_STRICT_OFFSET;
            }
        }
        return 1.0;
    }

    private org.ssclab.pl.milp.Constraint createBoundsConstraint(ConsType type) throws Exception {
        double[] bounds = new double[variables.size()];
        for (int i = 0; i < variables.size(); i++) {
            ConstrainedVariable variable = variables.get(i);
            if (variable instanceof Var) {
                Var var = (Var) variable;
                bounds[i] = type == ConsType.LOWER ? var.getMin() : var.getMax();
            } else {
                VarReal var = (VarReal) variable;
                bounds[i] = type == ConsType.LOWER ? var.getMin() : var.getMax();
            }
        }
        return new org.ssclab.pl.milp.Constraint(bounds, type, Double.NaN);
    }

    private org.ssclab.pl.milp.Constraint createIntegerTypeConstraint() throws Exception {
        double[] integerTypes = new double[variables.size()];
        for (int i = 0; i < integerTypes.length; i++) {
            integerTypes[i] = variables.get(i) instanceof Var ? 1.0 : 0.0;
        }
        return new org.ssclab.pl.milp.Constraint(integerTypes, ConsType.INT, Double.NaN);
    }

    private Solution createSolution(double[] values) {
        Var[] intVariables = problem.getVars();
        int intVariableCount = intVariables == null ? 0 : intVariables.length;
        if (intVariables != null) {
            for (int i = 0; i < intVariables.length; i++) {
                double value = values[i];
                int rounded = (int) Math.round(value);
                if (Math.abs(value - rounded) > INTEGER_TOLERANCE) {
                    log("WARNING: " + intVariables[i].getName() + " = " + value
                            + " rounded to " + rounded);
                }
                ((javax.constraints.impl.Var) intVariables[i]).setValue(rounded);
            }
        }

        VarReal[] realVariables = problem.getVarReals();
        if (realVariables != null) {
            for (int i = 0; i < realVariables.length; i++) {
                ((javax.constraints.impl.VarReal) realVariables[i]).setValue(values[intVariableCount + i]);
            }
        }
        return new javax.constraints.impl.search.Solution(this, 1);
    }

    private GoalType toGoalType(Objective objectiveDirection) {
        if (Objective.MAXIMIZE.equals(objectiveDirection)) {
            return GoalType.MAX;
        }
        if (Objective.MINIMIZE.equals(objectiveDirection)) {
            return GoalType.MIN;
        }
        throw new IllegalArgumentException("Unknown optimization direction: " + objectiveDirection);
    }

    @Override
    public String getVersion() {
        return JSR331_LINEAR_SOLVER_VERSION;
    }

    @Override
    public Objective getDefaultOptimizationObjective() {
        return Objective.MINIMIZE;
    }

    @Override
    public String getCommanLine() {
        throw new UnsupportedOperationException("SSC is invoked through its Java API");
    }

    @Override
    public HashMap<String, String> readResults() {
        throw new UnsupportedOperationException("SSC results are read through its Java API");
    }
}
