package javax.constraints.linear.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Objective;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.linear.NonLinearProblemException;

import com.sun.jna.Pointer;

/** JSR 331 linear and mixed-integer programming adapter for GLPK. */
public final class LinearSolver extends javax.constraints.linear.LinearSolver {

    private static final int GLP_MIN = 1;
    private static final int GLP_MAX = 2;
    private static final int GLP_CV = 1;
    private static final int GLP_IV = 2;
    private static final int GLP_FR = 1;
    private static final int GLP_LO = 2;
    private static final int GLP_UP = 3;
    private static final int GLP_DB = 4;
    private static final int GLP_FX = 5;
    private static final int GLP_FEAS = 2;
    private static final int GLP_OPT = 5;
    private static final int GLP_ON = 1;
    private static final int GLP_MSG_OFF = 0;
    private static final int GLP_MPS_FILE = 2;
    private static final int UNLIMITED_TIME = Integer.MAX_VALUE;
    private static final double INTEGER_TOLERANCE = 1.0e-5;
    private static final double INFINITY_THRESHOLD = 1.0e20;

    private final GlpkLibrary glpk = GlpkNativeLoader.get();

    @Override
    public Objective getDefaultOptimizationObjective() {
        return Objective.MINIMIZE;
    }

    @Override
    public String getVersion() {
        return "GLPK " + glpk.glp_version();
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

        Pointer nativeProblem = createProblem(file.getName());
        try {
            int result = glpk.glp_read_mps(
                    nativeProblem, GLP_MPS_FILE, null, file.getAbsolutePath());
            if (result != 0) {
                throw new IllegalArgumentException("GLPK cannot read MPS model: " + file);
            }

            boolean integerModel = glpk.glp_get_num_int(nativeProblem) > 0;
            if (!solveNative(nativeProblem, integerModel, timeoutMilliSeconds)) {
                return null;
            }
            copyImportedValues(nativeProblem, integerModel);
            return new javax.constraints.impl.search.Solution(this, 1);
        } finally {
            glpk.glp_delete_prob(nativeProblem);
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
        Pointer nativeProblem = createProblem(problem.getName());
        List<ConstrainedVariable> variables = new ArrayList<>();
        Map<ConstrainedVariable, Integer> indices = new IdentityHashMap<>();
        Set<String> names = new LinkedHashSet<>();

        try {
            Var[] integerVariables = problem.getVars();
            if (integerVariables != null) {
                for (Var variable : integerVariables) {
                    ensureVariable(variables, indices, variable);
                }
            }
            VarReal[] realVariables = problem.getVarReals();
            if (realVariables != null) {
                for (VarReal variable : realVariables) {
                    ensureVariable(variables, indices, variable);
                }
            }
            int objectiveIndex = ensureVariable(variables, indices, objectiveVar);

            glpk.glp_add_cols(nativeProblem, variables.size());
            boolean integerModel = false;
            for (int index = 0; index < variables.size(); index++) {
                ConstrainedVariable variable = variables.get(index);
                int column = index + 1;
                glpk.glp_set_col_name(nativeProblem, column,
                        uniqueName(variable.getName(), names, index));
                if (variable instanceof Var integer) {
                    setBounds(nativeProblem, column, integer.getMin(), integer.getMax());
                    glpk.glp_set_col_kind(nativeProblem, column, GLP_IV);
                    integerModel = true;
                } else if (variable instanceof VarReal real) {
                    setBounds(nativeProblem, column, real.getMin(), real.getMax());
                    glpk.glp_set_col_kind(nativeProblem, column, GLP_CV);
                } else {
                    throw new NonLinearProblemException(
                            "Unsupported constrained variable type: "
                            + variable.getClass().getName());
                }
            }

            javax.constraints.Constraint[] constraints = problem.getConstraints();
            glpk.glp_add_rows(nativeProblem, constraints.length);
            for (int index = 0; index < constraints.length; index++) {
                addConstraint(nativeProblem, variables, indices,
                        (javax.constraints.impl.Constraint) constraints[index], index);
            }

            glpk.glp_set_obj_dir(nativeProblem,
                    direction == Objective.MAXIMIZE ? GLP_MAX : GLP_MIN);
            glpk.glp_set_obj_coef(nativeProblem, objectiveIndex + 1, 1.0);

            if (!solveNative(nativeProblem, integerModel, timeoutMilliSeconds)) {
                return null;
            }

            for (int index = 0; index < variables.size(); index++) {
                double value = integerModel
                        ? glpk.glp_mip_col_val(nativeProblem, index + 1)
                        : glpk.glp_get_col_prim(nativeProblem, index + 1);
                setJsrValue(variables.get(index), value);
            }
            return new javax.constraints.impl.search.Solution(this, 1);
        } finally {
            glpk.glp_delete_prob(nativeProblem);
        }
    }

    private Pointer createProblem(String name) {
        Pointer problem = glpk.glp_create_prob();
        if (problem == null) {
            throw new IllegalStateException("GLPK could not create a problem object");
        }
        if (name != null && !name.isBlank()) {
            glpk.glp_set_prob_name(problem, name);
        }
        return problem;
    }

    private boolean solveNative(
            Pointer nativeProblem, boolean integerModel, int timeoutMilliSeconds) {

        GlpkLibrary.SimplexParameters simplex = new GlpkLibrary.SimplexParameters();
        glpk.glp_init_smcp(simplex);
        simplex.read();
        simplex.msgLev = GLP_MSG_OFF;
        simplex.timeLimit = timeLimit(timeoutMilliSeconds);
        simplex.presolve = GLP_ON;
        simplex.write();
        glpk.glp_simplex(nativeProblem, simplex);

        if (!integerModel) {
            int status = glpk.glp_get_status(nativeProblem);
            return status == GLP_OPT || status == GLP_FEAS;
        }

        GlpkLibrary.IntegerParameters integer = new GlpkLibrary.IntegerParameters();
        glpk.glp_init_iocp(integer);
        integer.read();
        integer.msgLev = GLP_MSG_OFF;
        integer.timeLimit = timeLimit(timeoutMilliSeconds);
        integer.presolve = GLP_ON;
        integer.write();
        glpk.glp_intopt(nativeProblem, integer);

        int status = glpk.glp_mip_status(nativeProblem);
        return status == GLP_OPT || status == GLP_FEAS;
    }

    private static int timeLimit(int timeoutMilliSeconds) {
        return timeoutMilliSeconds > 0 ? timeoutMilliSeconds : UNLIMITED_TIME;
    }

    private void addConstraint(
            Pointer nativeProblem,
            List<ConstrainedVariable> variables,
            Map<ConstrainedVariable, Integer> indices,
            javax.constraints.impl.Constraint constraint,
            int index) {

        ConstrainedVariable[] rowVariables = constraint.getVars();
        double[] coefficients = constraint.getCoefficients();
        if (rowVariables == null || coefficients == null
                || rowVariables.length != coefficients.length) {
            throw new IllegalArgumentException(
                    "Malformed linear constraint " + constraint.getName());
        }

        boolean integralExpression = true;
        Map<Integer, Double> combined = new LinkedHashMap<>();
        for (int i = 0; i < rowVariables.length; i++) {
            int column = ensureVariable(variables, indices, rowVariables[i]) + 1;
            combined.merge(column, coefficients[i], Double::sum);
            integralExpression &= rowVariables[i] instanceof Var
                    && isIntegral(coefficients[i]);
        }

        RowBounds bounds = rowBounds(
                constraint.getOper(), constraint.getValue(), integralExpression);
        int row = index + 1;
        String name = constraint.getName();
        if (name == null || name.isBlank()) {
            name = "constraint_" + row;
        }
        glpk.glp_set_row_name(nativeProblem, row, name);
        glpk.glp_set_row_bnds(
                nativeProblem, row, bounds.type(), bounds.lower(), bounds.upper());

        int nonzeros = (int) combined.values().stream().filter(value -> value != 0.0).count();
        int[] columnIndices = new int[nonzeros + 1];
        double[] values = new double[nonzeros + 1];
        int position = 1;
        for (Map.Entry<Integer, Double> entry : combined.entrySet()) {
            if (entry.getValue() != 0.0) {
                columnIndices[position] = entry.getKey();
                values[position] = entry.getValue();
                position++;
            }
        }
        glpk.glp_set_mat_row(nativeProblem, row, nonzeros, columnIndices, values);
    }

    private static RowBounds rowBounds(
            String operator, double value, boolean integralExpression) {
        if (operator == null) {
            throw new IllegalArgumentException("A linear constraint has no operator");
        }
        return switch (operator.trim().toLowerCase(Locale.ROOT)) {
            case "=", "==", "is" -> new RowBounds(GLP_FX, value, value);
            case "<=" -> new RowBounds(GLP_UP, 0.0, value);
            case ">=" -> new RowBounds(GLP_LO, value, 0.0);
            case "<" -> new RowBounds(GLP_UP, 0.0,
                    strictIntegerBound(value, -1, integralExpression, operator));
            case ">" -> new RowBounds(GLP_LO,
                    strictIntegerBound(value, 1, integralExpression, operator), 0.0);
            case "!=", "<>", "is not" -> throw new NonLinearProblemException(
                    "GLPK cannot represent a disjunctive != constraint as one linear row");
            default -> throw new IllegalArgumentException(
                    "Unsupported linear constraint operator: " + operator);
        };
    }

    private void setBounds(Pointer nativeProblem, int column, double lower, double upper) {
        int type;
        if (lower <= -INFINITY_THRESHOLD && upper >= INFINITY_THRESHOLD) {
            type = GLP_FR;
        } else if (lower <= -INFINITY_THRESHOLD) {
            type = GLP_UP;
        } else if (upper >= INFINITY_THRESHOLD) {
            type = GLP_LO;
        } else if (lower == upper) {
            type = GLP_FX;
        } else {
            type = GLP_DB;
        }
        glpk.glp_set_col_bnds(nativeProblem, column, type, lower, upper);
    }

    private void copyImportedValues(Pointer nativeProblem, boolean integerModel) {
        Map<String, Double> values = new HashMap<>();
        int columns = glpk.glp_get_num_cols(nativeProblem);
        for (int column = 1; column <= columns; column++) {
            String name = glpk.glp_get_col_name(nativeProblem, column);
            double value = integerModel
                    ? glpk.glp_mip_col_val(nativeProblem, column)
                    : glpk.glp_get_col_prim(nativeProblem, column);
            values.put(name, value);
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
                    "GLPK requires the JSR 331 common linear Problem implementation");
        }
        return problem;
    }

    private static int ensureVariable(
            List<ConstrainedVariable> variables,
            Map<ConstrainedVariable, Integer> indices,
            ConstrainedVariable variable) {
        Integer existing = indices.get(variable);
        if (existing != null) {
            return existing;
        }
        int index = variables.size();
        variables.add(variable);
        indices.put(variable, index);
        return index;
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
                        "GLPK returned a non-integral value for "
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

    private record RowBounds(int type, double lower, double upper) {
    }
}
