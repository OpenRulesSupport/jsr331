package javax.constraints.impl.constraint;

import javax.constraints.ConsistencyLevel;
import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

public class AllDifferent extends Constraint {
    public AllDifferent(Var[] vars) {
        super(vars[0].getProblem(), "AllDiff");
        Problem p = (Problem) vars[0].getProblem();
        setImpl(p.getChocoModel().allDifferent(p.createIntVarArray(vars)));
    }

    @Override public void post(ConsistencyLevel consistencyLevel) {
        post();
    }
}
