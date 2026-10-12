package javax.constraints.impl.constraint;

import org.maxicp.cp.CPFactory;

public class IfThen extends javax.constraints.impl.Constraint {
    public IfThen(javax.constraints.Constraint antecedent,
                  javax.constraints.Constraint consequent) {
        super(antecedent.getProblem(), CPFactory.isOr(
                CPFactory.not(unwrap(antecedent)), unwrap(consequent)));
    }

    private static org.maxicp.cp.engine.core.CPBoolVar unwrap(
            javax.constraints.Constraint constraint) {
        return ((javax.constraints.impl.Constraint) constraint).getMaxiCPBoolVar();
    }
}
