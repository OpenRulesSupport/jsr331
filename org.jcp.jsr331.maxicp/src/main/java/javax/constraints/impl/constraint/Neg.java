package javax.constraints.impl.constraint;

import org.maxicp.cp.CPFactory;

public class Neg extends javax.constraints.impl.Constraint {
    public Neg(javax.constraints.Constraint constraint) {
        super(constraint.getProblem(), CPFactory.not(
                ((javax.constraints.impl.Constraint) constraint).getMaxiCPBoolVar()));
    }
}
