package javax.constraints.impl;

import javax.constraints.impl.Var;

import JSetL.IntLVar;


/**
 * This class implement the JSR331 constrained boolean variable "VarBool",
 * extending the JSetL implementation Var. The implementation is
 * based on the solver JSetL.
 * 
 * <p>The construction of the variable is simply based on the narrowing of 
 * the domain of Var to [0,1]. If a built-in implementation of boolean 
 * variable will be added to JSetL solver this class should be modified.
 * 
 * @author Fabio Biselli
 */
public class VarBool extends Var implements javax.constraints.VarBool {

	/**
	 * Build a new VarBool that is a Var with domain [0,1], named
	 * <code>name</code>.
	 * 
	 * @param problem the problem which the variable is bound
	 * @param name the name of the built variable.
	 */
	public VarBool(Problem problem, String name) {
		super(problem, name);
		setImpl(new IntLVar(name, 0, 1));
	}
	
	/**
	 * Build a new VarBool that is a Var with domain [0,1].
	 * 
	 * @param problem the problem which the variable is bound.
	 */
	public VarBool(Problem problem) {
		super(problem);
		String name = super.getName();
		setImpl(new IntLVar(name, 0, 1));
	}

}
