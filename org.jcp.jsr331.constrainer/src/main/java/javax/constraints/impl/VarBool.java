package javax.constraints.impl;

public class VarBool extends Var implements javax.constraints.VarBool {
	
	public VarBool(Problem problem, String name) {
		super(problem,name,0,1);
	}
	
	public VarBool(Problem problem) {
		this(problem,"");
	}

}
