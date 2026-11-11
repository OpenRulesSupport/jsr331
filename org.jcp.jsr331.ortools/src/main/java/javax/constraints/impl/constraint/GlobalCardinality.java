package javax.constraints.impl.constraint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.constraints.ConsistencyLevel;
import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.Literal;

/** Closed global-cardinality constraint. */
public class GlobalCardinality extends Constraint {
    public GlobalCardinality(Var[] variables, int[] values, int[] minimums, int[] maximums) {
        super(variables[0].getProblem(), "GlobalCardinality",
                create(variables, values, minimums, maximums, null));
    }

    public GlobalCardinality(Var[] variables, int[] values, Var[] cardinalities) {
        super(variables[0].getProblem(), "GlobalCardinality",
                create(variables, values, null, null, cardinalities));
    }

    public GlobalCardinality(Var[] variables, Var[] cardinalities) {
        this(variables, consecutiveValues(cardinalities.length), cardinalities);
    }

    private static BoolVar create(
            Var[] variables,
            int[] values,
            int[] minimums,
            int[] maximums,
            Var[] cardinalities) {
        if (cardinalities != null && values.length != cardinalities.length) {
            throw new IllegalArgumentException(
                    "values and cardinality variables must have the same length");
        }
        if (cardinalities == null
                && (values.length != minimums.length || values.length != maximums.length)) {
            throw new IllegalArgumentException(
                    "values, minimums, and maximums must have the same length");
        }
        Set<Integer> unique = new HashSet<>();
        for (int value : values) {
            if (!unique.add(value)) {
                throw new IllegalArgumentException("Global-cardinality values must be unique");
            }
        }

        Problem problem = (Problem) variables[0].getProblem();
        BoolVar[][] matches = new BoolVar[variables.length][values.length];
        List<Literal> conditions = new ArrayList<>();
        for (int variableIndex = 0; variableIndex < variables.length; variableIndex++) {
            for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
                matches[variableIndex][valueIndex] = problem.relation(
                        ((javax.constraints.impl.Var) variables[variableIndex]).getCpVar(),
                        "=", values[valueIndex]);
            }
            conditions.add(problem.orTruth(matches[variableIndex]));
        }

        for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
            BoolVar[] occurrences = new BoolVar[variables.length];
            for (int variableIndex = 0; variableIndex < variables.length; variableIndex++) {
                occurrences[variableIndex] = matches[variableIndex][valueIndex];
            }
            if (cardinalities != null) {
                conditions.add(problem.relation(
                        LinearExpr.sum(occurrences), "=",
                        ((javax.constraints.impl.Var) cardinalities[valueIndex]).getCpVar()));
            } else {
                if (minimums[valueIndex] > maximums[valueIndex]) {
                    throw new IllegalArgumentException("A cardinality minimum exceeds its maximum");
                }
                conditions.add(problem.relation(
                        LinearExpr.sum(occurrences), ">=", minimums[valueIndex]));
                conditions.add(problem.relation(
                        LinearExpr.sum(occurrences), "<=", maximums[valueIndex]));
            }
        }
        return problem.andTruth(conditions.toArray(new Literal[0]));
    }

    private static int[] consecutiveValues(int length) {
        int[] values = new int[length];
        for (int i = 0; i < length; i++) {
            values[i] = i;
        }
        return values;
    }

    @Override
    public void post(ConsistencyLevel consistencyLevel) {
        post();
    }
}
