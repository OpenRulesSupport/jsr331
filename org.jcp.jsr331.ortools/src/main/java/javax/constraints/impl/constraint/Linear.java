package javax.constraints.impl.constraint;

import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearArgument;
import com.google.ortools.sat.LinearExpr;

/** Reified linear relationship. */
public class Linear extends Constraint {
    public Linear(Var left, String operator, Var right) {
        super(left.getProblem(), "Linear",
                ((Problem) left.getProblem()).relation(
                        ((javax.constraints.impl.Var) left).getCpVar(), operator,
                        ((javax.constraints.impl.Var) right).getCpVar()));
    }

    public Linear(Var variable, String operator, int value) {
        super(variable.getProblem(), "Linear",
                ((Problem) variable.getProblem()).relation(
                        ((javax.constraints.impl.Var) variable).getCpVar(), operator, value));
    }

    public Linear(int[] coefficients, Var[] variables, String operator, int value) {
        super(variables[0].getProblem(), "Linear",
                relation(coefficients, variables, operator, LinearExpr.constant(value)));
    }

    public Linear(Var[] variables, String operator, int value) {
        super(variables[0].getProblem(), "Linear",
                ((Problem) variables[0].getProblem()).relation(
                        LinearExpr.sum(toNative(variables)), operator, value));
    }

    public Linear(int[] coefficients, Var[] variables, String operator, Var variable) {
        super(variables[0].getProblem(), "Linear",
                relation(coefficients, variables, operator,
                        ((javax.constraints.impl.Var) variable).getCpVar()));
    }

    public Linear(Var[] variables, String operator, Var variable) {
        super(variables[0].getProblem(), "Linear",
                ((Problem) variables[0].getProblem()).relation(
                        LinearExpr.sum(toNative(variables)), operator,
                        ((javax.constraints.impl.Var) variable).getCpVar()));
    }

    private static com.google.ortools.sat.BoolVar relation(
            int[] coefficients,
            Var[] variables,
            String operator,
            LinearArgument right) {
        if (coefficients.length != variables.length) {
            throw new IllegalArgumentException(
                    "coefficients and variables must have the same length");
        }
        long[] longCoefficients = new long[coefficients.length];
        for (int i = 0; i < coefficients.length; i++) {
            longCoefficients[i] = coefficients[i];
        }
        Problem problem = (Problem) variables[0].getProblem();
        return problem.relation(
                LinearExpr.weightedSum(toNative(variables), longCoefficients),
                operator, right);
    }

    private static IntVar[] toNative(Var[] variables) {
        return ((Problem) variables[0].getProblem()).createIntVarArray(variables);
    }
}
