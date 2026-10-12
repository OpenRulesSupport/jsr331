package javax.constraints.impl;

import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.constraints.Constraint.Status;

/** Constraint wrapper for Choco Solver 6. */
public class Constraint extends AbstractConstraint {
    public Constraint(javax.constraints.Problem problem) { super(problem); }
    public Constraint(javax.constraints.Problem problem, String name) { super(problem, name); }
    public Constraint(javax.constraints.Problem problem,
                      org.chocosolver.solver.constraints.Constraint constraint) {
        super(problem); setImpl(constraint);
    }

    public org.chocosolver.solver.constraints.Constraint getChocoConstraint() {
        return (org.chocosolver.solver.constraints.Constraint) getImpl();
    }

    @Override public void post() {
        org.chocosolver.solver.constraints.Constraint constraint = getChocoConstraint();
        if (constraint.getStatus() == Status.POSTED) return;
        if (constraint.getStatus() == Status.REIFIED) {
            BoolVar truth = constraint.reify();
            truth.getModel().arithm(truth, "=", 1).post();
            return;
        }
        constraint.post();
    }
    @Override public javax.constraints.VarBool asBool() {
        BoolVar bool = getChocoConstraint().reify();
        return new VarBool(getProblem(), bool);
    }
    @Override public javax.constraints.Constraint and(javax.constraints.Constraint other) {
        return new javax.constraints.impl.constraint.And(this, other);
    }
    @Override public javax.constraints.Constraint or(javax.constraints.Constraint other) {
        return new javax.constraints.impl.constraint.Or(this, other);
    }
    @Override public javax.constraints.Constraint negation() {
        return new javax.constraints.impl.constraint.Neg(this);
    }
    @Override public javax.constraints.Constraint implies(javax.constraints.Constraint other) {
        return new javax.constraints.impl.constraint.IfThen(this, other);
    }
}
