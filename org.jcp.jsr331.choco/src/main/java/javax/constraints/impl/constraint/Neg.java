package javax.constraints.impl.constraint;

import javax.constraints.impl.Constraint;

public class Neg extends Constraint {
    public Neg(javax.constraints.Constraint constraint) {
        super(constraint.getProblem(), "Negation");
        setImpl(((Constraint) constraint).getChocoConstraint().getOpposite());
    }
}
