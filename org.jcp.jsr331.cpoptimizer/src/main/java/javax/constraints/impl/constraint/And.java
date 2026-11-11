package javax.constraints.impl.constraint;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import ilog.concert.IloException;

public class And extends Constraint {
    public And(javax.constraints.Constraint left, javax.constraints.Constraint right) {
        super(left.getProblem(), "And", create(left, right));
    }

    private static ilog.concert.IloConstraint create(
            javax.constraints.Constraint left, javax.constraints.Constraint right) {
        Problem problem = (Problem) left.getProblem();
        try {
            return problem.getCp().and(((Constraint) left).getCpConstraint(),
                    ((Constraint) right).getCpConstraint());
        } catch (IloException exception) {
            throw Problem.failure("create logical AND", exception);
        }
    }
}
