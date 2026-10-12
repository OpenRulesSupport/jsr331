package javax.constraints.impl;

import org.chocosolver.solver.variables.IntVar;

public class VarBool extends Var implements javax.constraints.VarBool {
    public VarBool(javax.constraints.Problem problem, String name) {
        super(problem, ((Problem) problem).getChocoModel().boolVar(name));
    }
    public VarBool(javax.constraints.Problem problem) { this(problem, ""); }
    public VarBool(javax.constraints.Problem problem, IntVar expression) { super(problem, expression); }
}
