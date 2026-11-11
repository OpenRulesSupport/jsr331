package javax.constraints.impl.constraint;

import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

public class And extends Constraint {
    public And(javax.constraints.Constraint left, javax.constraints.Constraint right) {
        super(left.getProblem(), "And", ((Problem) left.getProblem()).andTruth(
                ((Constraint) left).getTruthVar(), ((Constraint) right).getTruthVar()));
    }
}
