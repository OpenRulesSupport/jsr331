package javax.constraints.impl.constraint;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.constraints.ConsistencyLevel;
import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloIntExpr;

/** Closed global-cardinality constraint. */
public class GlobalCardinality extends Constraint {
    public GlobalCardinality(Var[] variables, int[] values,
            int[] minimums, int[] maximums) {
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

    private static IloConstraint create(Var[] variables, int[] values,
            int[] minimums, int[] maximums, Var[] cardinalities) {
        validate(values, minimums, maximums, cardinalities);
        Problem problem = (Problem) variables[0].getProblem();
        List<IloConstraint> conditions = new ArrayList<>();
        try {
            for (Var variable : variables) {
                IloConstraint[] allowed = new IloConstraint[values.length];
                for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
                    allowed[valueIndex] = problem.relation(
                            ((javax.constraints.impl.Var) variable).getCpVar(),
                            "=", values[valueIndex]);
                }
                conditions.add(problem.getCp().or(allowed));
            }

            for (int valueIndex = 0; valueIndex < values.length; valueIndex++) {
                IloIntExpr count = problem.getCp().count(
                        problem.expressions(variables), values[valueIndex]);
                if (cardinalities != null) {
                    conditions.add(problem.relation(count, "=",
                            ((javax.constraints.impl.Var) cardinalities[valueIndex]).getCpVar()));
                } else {
                    conditions.add(problem.relation(count, ">=", minimums[valueIndex]));
                    conditions.add(problem.relation(count, "<=", maximums[valueIndex]));
                }
            }
            return problem.getCp().and(conditions.toArray(new IloConstraint[0]));
        } catch (IloException exception) {
            throw Problem.failure("create global-cardinality constraint", exception);
        }
    }

    private static void validate(int[] values, int[] minimums,
            int[] maximums, Var[] cardinalities) {
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
        for (int index = 0; index < values.length; index++) {
            if (!unique.add(values[index])) {
                throw new IllegalArgumentException(
                        "Global-cardinality values must be unique");
            }
            if (cardinalities == null && minimums[index] > maximums[index]) {
                throw new IllegalArgumentException(
                        "A cardinality minimum exceeds its maximum");
            }
        }
    }

    private static int[] consecutiveValues(int length) {
        int[] values = new int[length];
        for (int index = 0; index < length; index++) values[index] = index;
        return values;
    }

    @Override public void post(ConsistencyLevel consistencyLevel) { post(); }
}
