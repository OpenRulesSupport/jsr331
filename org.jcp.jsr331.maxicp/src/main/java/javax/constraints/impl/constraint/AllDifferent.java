package javax.constraints.impl.constraint;

import javax.constraints.Var;
import org.maxicp.cp.CPFactory;

public class AllDifferent extends javax.constraints.impl.Constraint {
    public AllDifferent(Var[] vars) {
        super(vars[0].getProblem(), CPFactory.allDifferentDC(
                ((javax.constraints.impl.Problem) vars[0].getProblem()).toMaxiCP(vars)));
    }
}
