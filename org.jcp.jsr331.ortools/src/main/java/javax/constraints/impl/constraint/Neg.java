package javax.constraints.impl.constraint;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

public class Neg extends Constraint {
    public Neg(javax.constraints.Constraint constraint) {
        super(constraint.getProblem(), "Negation",
                ((Problem) constraint.getProblem()).notTruth(
                        ((Constraint) constraint).getTruthVar()));
    }
}
