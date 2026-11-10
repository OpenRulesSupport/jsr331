package javax.constraints.linear.impl;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.constraints.ConstrainedVariable;
import javax.constraints.Objective;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.linear.NonLinearProblemException;

import com.sun.jna.Pointer;

/** JSR 331 linear and mixed-integer programming adapter for HiGHS. */
public final class LinearSolver extends javax.constraints.linear.LinearSolver {

    private static final int HIGHS_STATUS_ERROR = -1;
    private static final int HIGHS_VAR_CONTINUOUS = 0;
    private static final int HIGHS_VAR_INTEGER = 1;
    private static final int HIGHS_SENSE_MINIMIZE = 1;
    private static final int HIGHS_SENSE_MAXIMIZE = -1;
    private static final int HIGHS_MATRIX_COLWISE = 1;
    private static final int HIGHS_MODEL_OPTIMAL = 7;
    private static final int HIGHS_MODEL_OBJECTIVE_BOUND = 11;
    private static final int HIGHS_MODEL_OBJECTIVE_TARGET = 12;
    private static final int HIGHS_MODEL_TIME_LIMIT = 13;
    private static final int HIGHS_MODEL_ITERATION_LIMIT = 14;
    private static final int HIGHS_SOLUTION_FEASIBLE = 2;
    private static final int MAX_NAME_LENGTH = 512;
    private static final double INTEGER_TOLERANCE = 1.0e-5;
    private static final double HIGHS_INFINITY = 1.0e30;

    private final HighsLibrary highs = HighsNativeLoader.get();

    @Override
    public Objective getDefaultOptimizationObjective() {
        return Objective.MINIMIZE;
    }

    @Override
    public String getVersion() {
        Pointer version = highs.Highs_version();
        return "HiGHS " + (version == null ? "unknown" : version.getString(0));
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

        Pointer instance = createInstance(timeoutMilliSeconds);
        try {
            checkStatus(highs.Highs_readModel(instance, file.getAbsolutePath()),
                    "read model " + file);
            if (!runHasSolution(instance)) {
                return null;
            }

            int columns = Math.toIntExact(highs.Highs_getNumCol(instance));
            double[] values = new double[columns];
            checkStatus(highs.Highs_getSolution(instance, values, null, null, null),
                    "read solution");
            copyImportedValues(instance, values);
            return new javax.constraints.impl.search.Solution(this, 1);
        } finally {
            highs.Highs_destroy(instance);
        }
    }

    private Solution solveModel(
            Objective direction,
            ConstrainedVariable objectiveVar,
            int timeoutMilliSeconds) {

        if (direction == null) {
            throw new IllegalArgumentException("The optimization direction must not be null");
        }

        ModelData data = buildModel(direction, objectiveVar);
        Pointer instance = createInstance(timeoutMilliSeconds);
        try {
            long passStatus = data.hasIntegerVariables()
                    ? highs.Highs_passMip(
                            instance, data.columnCount(), data.rowCount(), data.nonzeroCount(),
                            HIGHS_MATRIX_COLWISE, data.sense(), 0.0,
                            data.cost(), data.columnLower(), data.columnUpper(),
                            data.rowLower(), data.rowUpper(), data.columnStart(),
                            data.rowIndex(), data.coefficient(), data.integrality())
                    : highs.Highs_passLp(
                            instance, data.columnCount(), data.rowCount(), data.nonzeroCount(),
                            HIGHS_MATRIX_COLWISE, data.sense(), 0.0,
                            data.cost(), data.columnLower(), data.columnUpper(),
                            data.rowLower(), data.rowUpper(), data.columnStart(),
                            data.rowIndex(), data.coefficient());
            checkStatus(passStatus, "pass model");

            if (!runHasSolution(instance)) {
                return null;
            }

            double[] values = new double[data.columnCount()];
            checkStatus(highs.Highs_getSolution(instance, values, null, null, null),
                    "read solution");
            for (int index = 0; index < values.length; index++) {
                setJsrValue(data.variables().get(index), values[index]);
            }
            return new javax.constraints.impl.search.Solution(this, 1);
        } finally {
            highs.Highs_destroy(instance);
        }
    }

    private ModelData buildModel(Objective direction, ConstrainedVariable objectiveVar) {
        javax.constraints.impl.Problem problem = linearProblem();
        List<ConstrainedVariable> variables = new ArrayList<>();
        Map<ConstrainedVariable, Integer> indices = new IdentityHashMap<>();

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

        javax.constraints.Constraint[] problemConstraints = problem.getConstraints();
        List<RowData> rows = new ArrayList<>(problemConstraints.length);
        for (javax.constraints.Constraint problemConstraint : problemConstraints) {
            javax.constraints.impl.Constraint constraint =
                    (javax.constraints.impl.Constraint) problemConstraint;
            ConstrainedVariable[] rowVariables = constraint.getVars();
            double[] rowCoefficients = constraint.getCoefficients();
            if (rowVariables == null || rowCoefficients == null
                    || rowVariables.length != rowCoefficients.length) {
                throw new IllegalArgumentException(
                        "Malformed linear constraint " + constraint.getName());
            }

            int[] rowIndices = new int[rowVariables.length];
            boolean integralExpression = true;
            for (int i = 0; i < rowVariables.length; i++) {
                rowIndices[i] = ensureVariable(variables, indices, rowVariables[i]);
                integralExpression &= rowVariables[i] instanceof Var
                        && isIntegral(rowCoefficients[i]);
            }
            rows.add(new RowData(rowIndices, rowCoefficients,
                    constraint.getOper(), constraint.getValue(), integralExpression));
        }

        int columnCount = variables.size();
        double[] cost = new double[columnCount];
        double[] columnLower = new double[columnCount];
        double[] columnUpper = new double[columnCount];
        long[] integrality = new long[columnCount];
        boolean hasIntegerVariables = false;
        for (int column = 0; column < columnCount; column++) {
            ConstrainedVariable variable = variables.get(column);
            if (variable instanceof Var integer) {
                columnLower[column] = integer.getMin();
                columnUpper[column] = integer.getMax();
                integrality[column] = HIGHS_VAR_INTEGER;
                hasIntegerVariables = true;
            } else if (variable instanceof VarReal real) {
                columnLower[column] = real.getMin();
                columnUpper[column] = real.getMax();
                integrality[column] = HIGHS_VAR_CONTINUOUS;
            } else {
                throw new NonLinearProblemException(
                        "Unsupported constrained variable type: "
                        + variable.getClass().getName());
            }
        }
        cost[objectiveIndex] = 1.0;

        double[] rowLower = new double[rows.size()];
        double[] rowUpper = new double[rows.size()];
        @SuppressWarnings("unchecked")
        List<MatrixEntry>[] columns = new List[columnCount];
        for (int column = 0; column < columnCount; column++) {
            columns[column] = new ArrayList<>();
        }
        for (int row = 0; row < rows.size(); row++) {
            RowData rowData = rows.get(row);
            Bounds rowBounds = bounds(rowData.operator(), rowData.value(),
                    rowData.integralExpression(), HIGHS_INFINITY);
            rowLower[row] = rowBounds.lower();
            rowUpper[row] = rowBounds.upper();
            for (int entry = 0; entry < rowData.indices().length; entry++) {
                double coefficientValue = rowData.coefficients()[entry];
                if (coefficientValue != 0.0) {
                    columns[rowData.indices()[entry]]
                            .add(new MatrixEntry(row, coefficientValue));
                }
            }
        }

        int nonzeroCount = columnsToArrays(columns, null, null, null);
        long[] columnStart = new long[columnCount + 1];
        long[] rowIndex = new long[nonzeroCount];
        double[] coefficient = new double[nonzeroCount];
        columnsToArrays(columns, columnStart, rowIndex, coefficient);

        return new ModelData(
                variables, rows.size(), nonzeroCount,
                direction == Objective.MAXIMIZE
                        ? HIGHS_SENSE_MAXIMIZE : HIGHS_SENSE_MINIMIZE,
                cost, columnLower, columnUpper, integrality,
                rowLower, rowUpper, columnStart, rowIndex, coefficient,
                hasIntegerVariables);
    }

    private Pointer createInstance(int timeoutMilliSeconds) {
        Pointer instance = highs.Highs_create();
        if (instance == null) {
            throw new IllegalStateException("HiGHS could not create a solver instance");
        }
        try {
            checkStatus(highs.Highs_setBoolOptionValue(instance, "output_flag", 0L),
                    "disable solver output");
            if (timeoutMilliSeconds > 0) {
                checkStatus(highs.Highs_setDoubleOptionValue(
                        instance, "time_limit", timeoutMilliSeconds / 1000.0),
                        "set time limit");
            }
            return instance;
        } catch (RuntimeException exception) {
            highs.Highs_destroy(instance);
            throw exception;
        }
    }

    private boolean runHasSolution(Pointer instance) {
        checkStatus(highs.Highs_run(instance), "solve model");
        long modelStatus = highs.Highs_getModelStatus(instance);
        if (modelStatus == HIGHS_MODEL_OPTIMAL) {
            return true;
        }
        if (modelStatus == HIGHS_MODEL_OBJECTIVE_BOUND
                || modelStatus == HIGHS_MODEL_OBJECTIVE_TARGET
                || modelStatus == HIGHS_MODEL_TIME_LIMIT
                || modelStatus == HIGHS_MODEL_ITERATION_LIMIT) {
            long[] solutionStatus = new long[1];
            long status = highs.Highs_getIntInfoValue(
                    instance, "primal_solution_status", solutionStatus);
            return status != HIGHS_STATUS_ERROR
                    && solutionStatus[0] == HIGHS_SOLUTION_FEASIBLE;
        }
        return false;
    }

    private void copyImportedValues(Pointer instance, double[] values) {
        Map<String, Double> byName = new LinkedHashMap<>();
        for (int column = 0; column < values.length; column++) {
            byte[] buffer = new byte[MAX_NAME_LENGTH];
            if (highs.Highs_getColName(instance, column, buffer) != HIGHS_STATUS_ERROR) {
                int length = 0;
                while (length < buffer.length && buffer[length] != 0) {
                    length++;
                }
                byName.put(new String(buffer, 0, length, StandardCharsets.UTF_8),
                        values[column]);
            }
        }

        javax.constraints.impl.Problem problem = linearProblem();
        Var[] integerVariables = problem.getVars();
        if (integerVariables != null) {
            for (Var variable : integerVariables) {
                copyImportedValue(byName, variable);
            }
        }
        VarReal[] realVariables = problem.getVarReals();
        if (realVariables != null) {
            for (VarReal variable : realVariables) {
                copyImportedValue(byName, variable);
            }
        }
    }

    private static void copyImportedValue(
            Map<String, Double> byName, ConstrainedVariable variable) {
        Double value = byName.get(variable.getName());
        if (value == null) {
            value = byName.get(variable.getId());
        }
        if (value != null) {
            setJsrValue(variable, value);
        }
    }

    private javax.constraints.impl.Problem linearProblem() {
        if (!(getProblem() instanceof javax.constraints.impl.Problem problem)) {
            throw new IllegalStateException(
                    "HiGHS requires the JSR 331 common linear Problem implementation");
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

    private static Bounds bounds(
            String operator, double value, boolean integralExpression, double infinity) {
        if (operator == null) {
            throw new IllegalArgumentException("A linear constraint has no operator");
        }
        return switch (operator.trim().toLowerCase()) {
            case "=", "==", "is" -> new Bounds(value, value);
            case "<=" -> new Bounds(-infinity, value);
            case ">=" -> new Bounds(value, infinity);
            case "<" -> new Bounds(-infinity,
                    strictIntegerBound(value, -1, integralExpression, operator));
            case ">" -> new Bounds(
                    strictIntegerBound(value, 1, integralExpression, operator), infinity);
            case "!=", "<>", "is not" -> throw new NonLinearProblemException(
                    "HiGHS cannot represent a disjunctive != constraint as one linear row");
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

    private static int columnsToArrays(
            List<MatrixEntry>[] columns,
            long[] columnStart,
            long[] rowIndex,
            double[] coefficient) {
        int offset = 0;
        for (int column = 0; column < columns.length; column++) {
            if (columnStart != null) {
                columnStart[column] = offset;
            }
            for (MatrixEntry entry : columns[column]) {
                if (rowIndex != null) {
                    rowIndex[offset] = entry.row();
                    coefficient[offset] = entry.value();
                }
                offset++;
            }
        }
        if (columnStart != null) {
            columnStart[columns.length] = offset;
        }
        return offset;
    }

    private static void checkStatus(long status, String operation) {
        if (status == HIGHS_STATUS_ERROR) {
            throw new IllegalStateException("HiGHS failed to " + operation);
        }
    }

    private static void setJsrValue(ConstrainedVariable variable, double value) {
        if (variable instanceof javax.constraints.impl.Var integer) {
            long rounded = Math.round(value);
            if (Math.abs(value - rounded) > INTEGER_TOLERANCE) {
                throw new IllegalStateException(
                        "HiGHS returned a non-integral value for "
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

    private record Bounds(double lower, double upper) {
    }

    private record MatrixEntry(int row, double value) {
    }

    private record RowData(
            int[] indices,
            double[] coefficients,
            String operator,
            double value,
            boolean integralExpression) {
    }

    private record ModelData(
            List<ConstrainedVariable> variables,
            int rowCount,
            int nonzeroCount,
            int sense,
            double[] cost,
            double[] columnLower,
            double[] columnUpper,
            long[] integrality,
            double[] rowLower,
            double[] rowUpper,
            long[] columnStart,
            long[] rowIndex,
            double[] coefficient,
            boolean hasIntegerVariables) {

        int columnCount() {
            return variables.size();
        }
    }
}
