package javax.constraints.impl.constraint;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

public class IfThen extends Constraint {
    public IfThen(javax.constraints.Constraint antecedent, javax.constraints.Constraint consequent) {
        super(antecedent.getProblem(), "IfThen");
        Problem p = (Problem) antecedent.getProblem();
        setImpl(p.getChocoModel().or(((Constraint) antecedent).getChocoConstraint().getOpposite(),
                ((Constraint) consequent).getChocoConstraint()));
    }
}
