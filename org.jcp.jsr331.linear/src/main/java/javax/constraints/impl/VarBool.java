//*************************************************
//*  J A V A  C O M M U N I T Y  P R O C E S S    *
//*                                               *
//*              J S R  3 3 1                     *
//* * * * * * * * * * * * * * * * * * * * * * * * *
package javax.constraints.impl;

/**
 * An implementation of the interface "VarBool"
 * @author J.Feldman
 */

import javax.constraints.Problem;


public class VarBool extends Var implements javax.constraints.VarBool {
	
	public VarBool(Problem problem, String name) {
		super(problem,name);
		setMin(0);
		setMax(1);
	}
	
	public VarBool(Problem problem) {
		this(problem,"");
	}
	
}
