package javax.constraints.impl;

import ilog.concert.IloIntVar;

/** Boolean variable wrapper for CP Optimizer. */
public class VarBool extends Var implements javax.constraints.VarBool {
    public VarBool(javax.constraints.Problem problem, String name) {
        super(problem, name, 0, 1);
    }

    public VarBool(javax.constraints.Problem problem) {
        this(problem, "");
    }

    public VarBool(javax.constraints.Problem problem, IloIntVar variable) {
        super(problem, variable, 0, 1, null);
    }
}
