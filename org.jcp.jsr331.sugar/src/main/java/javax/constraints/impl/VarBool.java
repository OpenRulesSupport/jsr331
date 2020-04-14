package javax.constraints.impl;

/**
 * An implementation of the interface "VarBool"
 */
public class VarBool extends Var implements javax.constraints.VarBool {
    public VarBool(javax.constraints.Problem problem, String name) {
        super(problem, name, 0, 1);
    }
    
    public VarBool(javax.constraints.Problem problem) {
        this(problem, "");
    }
}
