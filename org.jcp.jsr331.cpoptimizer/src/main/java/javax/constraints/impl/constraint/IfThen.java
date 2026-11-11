package javax.constraints.impl.constraint;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import ilog.concert.IloException;

public class IfThen extends Constraint {
    public IfThen(javax.constraints.Constraint antecedent,
            javax.constraints.Constraint consequent) {
        super(antecedent.getProblem(), "IfThen", create(antecedent, consequent));
    }

    private static ilog.concert.IloConstraint create(
            javax.constraints.Constraint antecedent,
            javax.constraints.Constraint consequent) {
        Problem problem = (Problem) antecedent.getProblem();
        try {
            return problem.getCp().ifThen(
                    ((Constraint) antecedent).getCpConstraint(),
                    ((Constraint) consequent).getCpConstraint());
        } catch (IloException exception) {
            throw Problem.failure("create implication", exception);
        }
    }
}
