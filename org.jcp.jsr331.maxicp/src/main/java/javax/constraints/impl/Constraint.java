package javax.constraints.impl;

import javax.constraints.Problem;
import org.maxicp.cp.CPFactory;
import org.maxicp.cp.engine.core.CPBoolVar;
import org.maxicp.cp.engine.core.CPConstraint;

/** JSR-331 constraint backed by a MaxiCP constraint or truth variable. */
public class Constraint extends AbstractConstraint {
    private final CPConstraint constraint;
    private final CPBoolVar truth;

    public Constraint(Problem problem) { this(problem, (CPConstraint) null); }

    public Constraint(Problem problem, String name) {
        super(problem, name);
        this.constraint = null;
        this.truth = null;
    }

    public Constraint(Problem problem, CPConstraint constraint) {
        super(problem);
        this.constraint = constraint;
        this.truth = null;
        setImpl(constraint);
    }

    public Constraint(Problem problem, CPBoolVar truth) {
        super(problem);
        this.constraint = null;
        this.truth = truth;
        setImpl(truth);
    }

    public CPBoolVar getMaxiCPBoolVar() {
        if (truth == null)
            throw new UnsupportedOperationException(
                    "This MaxiCP constraint is not reified and cannot be used logically");
        return truth;
    }

    public CPConstraint getMaxiCPConstraint() { return constraint; }

    @Override
    public void post() {
        javax.constraints.impl.Problem problem =
                (javax.constraints.impl.Problem) getProblem();
        if (truth != null) problem.getMaxiCPSolver().post(truth);
        else if (constraint != null) problem.getMaxiCPSolver().post(constraint);
        else throw new IllegalStateException("Constraint has no MaxiCP implementation");
    }

    @Override
    public javax.constraints.VarBool asBool() {
        return new javax.constraints.impl.VarBool(
                getProblem(), getName() + ".asBool", getMaxiCPBoolVar());
    }

    @Override
    public javax.constraints.Constraint and(javax.constraints.Constraint other) {
        CPBoolVar result = CPFactory.not(CPFactory.isOr(
                CPFactory.not(getMaxiCPBoolVar()), CPFactory.not(unwrap(other))));
        return new javax.constraints.impl.Constraint(getProblem(), result);
    }

    @Override
    public javax.constraints.Constraint or(javax.constraints.Constraint other) {
        return new javax.constraints.impl.Constraint(
                getProblem(), CPFactory.isOr(getMaxiCPBoolVar(), unwrap(other)));
    }

    @Override
    public javax.constraints.Constraint negation() {
        return new javax.constraints.impl.Constraint(
                getProblem(), CPFactory.not(getMaxiCPBoolVar()));
    }

    @Override
    public javax.constraints.Constraint implies(javax.constraints.Constraint other) {
        return new javax.constraints.impl.Constraint(getProblem(),
                CPFactory.isOr(CPFactory.not(getMaxiCPBoolVar()), unwrap(other)));
    }

    private static CPBoolVar unwrap(javax.constraints.Constraint constraint) {
        if (!(constraint instanceof javax.constraints.impl.Constraint maxicp))
            throw new IllegalArgumentException(
                    "Constraint belongs to another JSR-331 implementation");
        return maxicp.getMaxiCPBoolVar();
    }
}
