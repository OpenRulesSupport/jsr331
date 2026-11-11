package javax.constraints.impl.constraint;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import ilog.concert.IloException;

public class Neg extends Constraint {
    public Neg(javax.constraints.Constraint constraint) {
        super(constraint.getProblem(), "Negation", create(constraint));
    }

    private static ilog.concert.IloConstraint create(javax.constraints.Constraint constraint) {
        Problem problem = (Problem) constraint.getProblem();
        try {
            return problem.getCp().not(((Constraint) constraint).getCpConstraint());
        } catch (IloException exception) {
            throw Problem.failure("negate constraint", exception);
        }
    }
}
