package javax.constraints.impl;

import javax.constraints.Problem;
import org.maxicp.cp.CPFactory;
import org.maxicp.cp.engine.core.CPBoolVar;

public class VarBool extends Var implements javax.constraints.VarBool {
    public VarBool(Problem problem, String name) {
        this(problem, name, CPFactory.makeBoolVar(
                ((javax.constraints.impl.Problem) problem).getMaxiCPSolver()));
    }

    public VarBool(Problem problem, String name, CPBoolVar variable) {
        super(problem, name, variable);
    }

    public CPBoolVar getMaxiCPBoolVar() { return (CPBoolVar) getMaxiCPVar(); }
}
