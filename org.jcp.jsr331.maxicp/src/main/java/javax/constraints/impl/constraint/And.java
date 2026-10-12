package javax.constraints.impl.constraint;

import org.maxicp.cp.CPFactory;

public class And extends javax.constraints.impl.Constraint {
    public And(javax.constraints.Constraint left, javax.constraints.Constraint right) {
        super(left.getProblem(), CPFactory.not(CPFactory.isOr(
                CPFactory.not(unwrap(left)), CPFactory.not(unwrap(right)))));
    }

    private static org.maxicp.cp.engine.core.CPBoolVar unwrap(
            javax.constraints.Constraint constraint) {
        return ((javax.constraints.impl.Constraint) constraint).getMaxiCPBoolVar();
    }
}
