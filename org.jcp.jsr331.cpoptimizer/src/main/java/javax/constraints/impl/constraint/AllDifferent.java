package javax.constraints.impl.constraint;

import javax.constraints.ConsistencyLevel;
import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import ilog.concert.IloException;

/** Native CP Optimizer all-different constraint. */
public class AllDifferent extends Constraint {
    public AllDifferent(Var[] variables) {
        super(variables[0].getProblem(), "AllDiff", create(variables));
    }

    private static ilog.concert.IloConstraint create(Var[] variables) {
        Problem problem = (Problem) variables[0].getProblem();
        try {
            return problem.getCp().allDiff(problem.expressions(variables));
        } catch (IloException exception) {
            throw Problem.failure("create all-different constraint", exception);
        }
    }

    @Override public void post(ConsistencyLevel consistencyLevel) { post(); }
}
