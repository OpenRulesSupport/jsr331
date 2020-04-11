//*************************************************
//*  J A V A  C O M M U N I T Y  P R O C E S S    *
//*                                               *
//*              J S R  3 3 1                     *
//*                                               *
//*       CHOCO-BASED IMPLEMENTATION              *
//*                                               *
//* * * * * * * * * * * * * * * * * * * * * * * * *
//*          _       _                            *
//*         |   (..)  |                           *
//*         |_  J||L _|        CHOCO solver       *
//*                                               *
//*    Choco is a java library for constraint     *
//*    satisfaction problems (CSP), constraint    *
//*    programming (CP) and explanation-based     *
//*    constraint solving (e-CP). It is built     *
//*    on a event-based propagation mechanism     *
//*    with backtrackable structures.             *
//*                                               *
//*    Choco is an open-source software,          *
//*    distributed under a BSD licence            *
//*    and hosted by sourceforge.net              *
//*                                               *
//*    + website : http://choco.emn.fr            *
//*    + support : choco@emn.fr                   *
//*                                               *
//*    Copyright (C) F. Laburthe,                 *
//*                  N. Jussien    1999-2009      *
//* * * * * * * * * * * * * * * * * * * * * * * * *
package javax.constraints.impl.constraint;

/**
 * An implementation of the Constraint "Linear"
 * @author J.Feldman
 */


import javax.constraints.Var;
import javax.constraints.impl.Constraint;

import choco.Choco;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;


public class Linear extends Constraint {

	static final String name = "Linear";


	public Linear(Var var1, String oper, Var var2) {
		super(var1.getProblem(), name);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)var1.getProblem();
		javax.constraints.impl.Var cVar1 = (javax.constraints.impl.Var)var1;
		javax.constraints.impl.Var cVar2 = (javax.constraints.impl.Var)var2;
		choco.kernel.model.constraints.Constraint linear = 
			p.compare(cVar1.getChocoVar(),oper,cVar2.getChocoVar());
		setImpl(linear);
	}
	
	public Linear(Var var, String oper, int value) {
		super(var.getProblem(), name);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)var.getProblem();
		javax.constraints.impl.Var cVar = (javax.constraints.impl.Var)var;
		choco.kernel.model.constraints.Constraint linear = p.compare(cVar.getChocoVar(),oper,value);
		setImpl(linear);
	}
	
	public Linear(int[] values, Var[] vars, String oper, int value) {
		super(vars[0].getProblem(), name);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)vars[0].getProblem();
		IntegerVariable[] intVars = p.createIntVarArray(vars);
		IntegerExpressionVariable scalar = Choco.scalar(intVars,values); 
		choco.kernel.model.constraints.Constraint linear = p.compare(scalar,oper,value);
		setImpl(linear);
	}

	public Linear(Var[] vars, String oper, int value) {
		super(vars[0].getProblem(), name);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)vars[0].getProblem();
		IntegerVariable[] intVars = p.createIntVarArray(vars);
		IntegerExpressionVariable scalar = Choco.sum(intVars); 
		choco.kernel.model.constraints.Constraint linear = p.compare(scalar,oper,value);
		setImpl(linear);
	}

	public Linear(int[] values, Var[] vars, String oper, Var var) {
		super(vars[0].getProblem(), name);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)vars[0].getProblem();
		IntegerVariable[] intVars = p.createIntVarArray(vars);
		IntegerExpressionVariable scalar = Choco.scalar(intVars,values); 
		javax.constraints.impl.Var cVar = (javax.constraints.impl.Var)var;
		choco.kernel.model.constraints.Constraint linear = p.compare(scalar,oper,cVar.getChocoVar());
		setImpl(linear);
	}

	public Linear(Var[] vars, String oper, Var var) {
		super(vars[0].getProblem(), name);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)vars[0].getProblem();
		IntegerVariable[] intVars = p.createIntVarArray(vars);
		IntegerExpressionVariable scalar = Choco.sum(intVars); 
		javax.constraints.impl.Var cVar = (javax.constraints.impl.Var)var;
		choco.kernel.model.constraints.Constraint linear = p.compare(scalar,oper,cVar.getChocoVar());
		setImpl(linear);
	}
}
