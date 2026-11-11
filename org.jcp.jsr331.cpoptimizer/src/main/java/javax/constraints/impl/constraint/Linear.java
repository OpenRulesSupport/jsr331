package javax.constraints.impl.constraint;

import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import ilog.concert.IloException;
import ilog.concert.IloIntExpr;

/** Linear or arithmetic relationship. */
public class Linear extends Constraint {
    public Linear(Var left, String operator, Var right) {
        super(left.getProblem(), "Linear", ((Problem) left.getProblem()).relation(
                nativeVar(left), operator, nativeVar(right)));
    }

    public Linear(Var variable, String operator, int value) {
        super(variable.getProblem(), "Linear", ((Problem) variable.getProblem()).relation(
                nativeVar(variable), operator, value));
    }

    public Linear(int[] coefficients, Var[] variables, String operator, int value) {
        super(variables[0].getProblem(), "Linear",
                relation(coefficients, variables, operator, null, value));
    }

    public Linear(Var[] variables, String operator, int value) {
        super(variables[0].getProblem(), "Linear",
                relation(null, variables, operator, null, value));
    }

    public Linear(int[] coefficients, Var[] variables, String operator, Var variable) {
        super(variables[0].getProblem(), "Linear",
                relation(coefficients, variables, operator, variable, null));
    }

    public Linear(Var[] variables, String operator, Var variable) {
        super(variables[0].getProblem(), "Linear",
                relation(null, variables, operator, variable, null));
    }

    private static ilog.concert.IloConstraint relation(
            int[] coefficients, Var[] variables, String operator,
            Var rightVariable, Integer rightValue) {
        if (coefficients != null && coefficients.length != variables.length) {
            throw new IllegalArgumentException(
                    "coefficients and variables must have the same length");
        }
        Problem problem = (Problem) variables[0].getProblem();
        try {
            IloIntExpr left = coefficients == null
                    ? problem.getCp().sum(problem.expressions(variables))
                    : problem.getCp().prod(coefficients, problem.expressions(variables));
            return rightVariable == null
                    ? problem.relation(left, operator, rightValue)
                    : problem.relation(left, operator, nativeVar(rightVariable));
        } catch (IloException exception) {
            throw Problem.failure("create linear constraint", exception);
        }
    }

    private static ilog.concert.IloIntVar nativeVar(Var variable) {
        return ((javax.constraints.impl.Var) variable).getCpVar();
    }
}
