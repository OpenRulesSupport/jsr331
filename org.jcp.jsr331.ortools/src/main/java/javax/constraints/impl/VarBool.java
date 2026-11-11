package javax.constraints.impl;

import com.google.ortools.sat.BoolVar;

/** Boolean variable wrapper for OR-Tools CP-SAT. */
public class VarBool extends Var implements javax.constraints.VarBool {
    public VarBool(javax.constraints.Problem problem, String name) {
        this(problem, ((Problem) problem).getCpModel().newBoolVar(
                name == null || name.isBlank() ? ((Problem) problem).nextName("bool") : name));
    }

    public VarBool(javax.constraints.Problem problem) {
        this(problem, "");
    }

    public VarBool(javax.constraints.Problem problem, BoolVar variable) {
        super(problem, variable);
    }

    public BoolVar getCpBoolVar() {
        return (BoolVar) getCpVar();
    }
}
