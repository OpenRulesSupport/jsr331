package javax.constraints.impl.constraint;

import org.maxicp.cp.CPFactory;

public class Or extends javax.constraints.impl.Constraint {
    public Or(javax.constraints.Constraint left, javax.constraints.Constraint right) {
        super(left.getProblem(), CPFactory.isOr(unwrap(left), unwrap(right)));
    }

    private static org.maxicp.cp.engine.core.CPBoolVar unwrap(
            javax.constraints.Constraint constraint) {
        return ((javax.constraints.impl.Constraint) constraint).getMaxiCPBoolVar();
    }
}
