package javax.constraints.impl.constraint;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

public class Or extends Constraint {
    public Or(javax.constraints.Constraint left, javax.constraints.Constraint right) {
        super(left.getProblem(), "Or");
        Problem p = (Problem) left.getProblem();
        setImpl(p.getChocoModel().or(((Constraint) left).getChocoConstraint(),
                ((Constraint) right).getChocoConstraint()));
    }
}
