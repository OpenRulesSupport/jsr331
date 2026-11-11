package javax.constraints.impl;

import com.google.ortools.sat.BoolVar;

/** Boolean-reified JSR 331 constraint backed by OR-Tools CP-SAT. */
public class Constraint extends AbstractConstraint {
    private boolean posted;

    public Constraint(javax.constraints.Problem problem) {
        this(problem, "Constraint", ((Problem) problem).fixedTruth(true));
    }

    public Constraint(javax.constraints.Problem problem, String name) {
        this(problem, name, ((Problem) problem).fixedTruth(true));
    }

    public Constraint(javax.constraints.Problem problem, BoolVar truth) {
        this(problem, "Constraint", truth);
    }

    public Constraint(javax.constraints.Problem problem, String name, BoolVar truth) {
        super(problem, name);
        setImpl(truth);
    }

    public BoolVar getTruthVar() {
        return (BoolVar) getImpl();
    }

    @Override
    public void post() {
        if (!posted) {
            ((Problem) getProblem()).getCpModel().addEquality(getTruthVar(), 1);
            posted = true;
        }
    }

    @Override
    public javax.constraints.VarBool asBool() {
        return new VarBool(getProblem(), getTruthVar());
    }

    @Override
    public javax.constraints.Constraint and(javax.constraints.Constraint other) {
        return new javax.constraints.impl.constraint.And(this, other);
    }

    @Override
    public javax.constraints.Constraint or(javax.constraints.Constraint other) {
        return new javax.constraints.impl.constraint.Or(this, other);
    }

    @Override
    public javax.constraints.Constraint negation() {
        return new javax.constraints.impl.constraint.Neg(this);
    }

    @Override
    public javax.constraints.Constraint implies(javax.constraints.Constraint other) {
        return new javax.constraints.impl.constraint.IfThen(this, other);
    }
}
