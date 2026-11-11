package javax.constraints.impl.constraint;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import ilog.concert.IloException;

public class Or extends Constraint {
    public Or(javax.constraints.Constraint left, javax.constraints.Constraint right) {
        super(left.getProblem(), "Or", create(left, right));
    }

    private static ilog.concert.IloConstraint create(
            javax.constraints.Constraint left, javax.constraints.Constraint right) {
        Problem problem = (Problem) left.getProblem();
        try {
            return problem.getCp().or(((Constraint) left).getCpConstraint(),
                    ((Constraint) right).getCpConstraint());
        } catch (IloException exception) {
            throw Problem.failure("create logical OR", exception);
        }
    }
}
