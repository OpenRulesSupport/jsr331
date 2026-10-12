package javax.constraints.impl.constraint;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

public class And extends Constraint {
    public And(javax.constraints.Constraint left, javax.constraints.Constraint right) {
        super(left.getProblem(), "And");
        Problem p = (Problem) left.getProblem();
        setImpl(p.getChocoModel().and(((Constraint) left).getChocoConstraint(),
                ((Constraint) right).getChocoConstraint()));
    }
}
