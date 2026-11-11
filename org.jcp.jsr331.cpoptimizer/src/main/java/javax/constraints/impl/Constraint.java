package javax.constraints.impl;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;

/** JSR 331 constraint backed by a CP Optimizer constraint. */
public class Constraint extends AbstractConstraint {
    private boolean posted;

    public Constraint(javax.constraints.Problem problem) {
        this(problem, "Constraint", trueConstraint((Problem) problem));
    }

    public Constraint(javax.constraints.Problem problem, String name) {
        this(problem, name, trueConstraint((Problem) problem));
    }

    public Constraint(javax.constraints.Problem problem, IloConstraint constraint) {
        this(problem, "Constraint", constraint);
    }

    public Constraint(javax.constraints.Problem problem, String name,
            IloConstraint constraint) {
        super(problem, name);
        setImpl(constraint);
    }

    public IloConstraint getCpConstraint() {
        return (IloConstraint) getImpl();
    }

    @Override
    public void post() {
        if (!posted) {
            try {
                ((Problem) getProblem()).getCp().add(getCpConstraint());
                posted = true;
            } catch (IloException exception) {
                throw Problem.failure("post constraint " + getName(), exception);
            }
        }
    }

    @Override
    public javax.constraints.VarBool asBool() {
        Problem problem = (Problem) getProblem();
        return new VarBool(problem, problem.reify(getCpConstraint()));
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

    private static IloConstraint trueConstraint(Problem problem) {
        try {
            return problem.getCp().trueConstraint();
        } catch (IloException exception) {
            throw Problem.failure("create true constraint", exception);
        }
    }
}
