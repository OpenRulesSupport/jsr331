//================================================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1 
// 
// CONSTRAINER-BASED REFERENCE IMPLEMENTATION
//
// Copyright (c) Cork Constraint Computation Centre, 2010
// University College Cork, Cork, Ireland, www.4c.ucc.ie
// Constrainer is copyrighted by Exigen Group, USA.
// 
//================================================================
package javax.constraints.impl.constraint;


import javax.constraints.Var;
import javax.constraints.impl.Constraint;
import javax.constraints.impl.Problem;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;

public class Linear extends Constraint {

	static final String name = "Linear";


	public Linear(Var var1, String oper, Var var2) {
		super(var1.getProblem(), var1.getName()+" "+oper+" "+var2.getName());
		IntExp cVar1 = (IntExp) var1.getImpl();
		Problem problem = (Problem) var1.getProblem();
		problem.defineConstraintImpl(this, cVar1, oper, var2);
	}
	
	public Linear(Var var, String oper, int value) {
		super(var.getProblem(), var.getName()+" "+oper+" "+value);
		IntExp cVar = (IntExp) var.getImpl();
		Problem problem = (Problem) var.getProblem();
		problem.defineConstraintImpl(this, cVar, oper, value);
	}

	public Linear(int[] values, Var[] vars, String oper, int value) {
		super(vars[0].getProblem(), name);
		Problem problem = (Problem) vars[0].getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntExpArray intvars = new IntExpArray(constrainer, vars.length);
		for (int i = 0; i < vars.length; i++) {
			IntExp cvar = (IntExp) vars[i].getImpl();
			intvars.set(cvar, i);
		}
		IntExp scalProd = constrainer.scalarProduct(intvars, values);
		problem.defineConstraintImpl(this, scalProd, oper, value);
	}

	public Linear(Var[] vars, String oper, int value) {
		super(vars[0].getProblem(), name);
		Problem problem = (Problem) vars[0].getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntExpArray intvars = new IntExpArray(constrainer, vars.length);
		for (int i = 0; i < vars.length; i++) {
			IntExp cvar = (IntExp) vars[i].getImpl();
			intvars.set(cvar, i);
		}
		IntExp sum = constrainer.sum(intvars);
		problem.defineConstraintImpl(this, sum, oper, value);
	}

	public Linear(int[] values, Var[] vars, String oper, Var var) {
		super(vars[0].getProblem(), name);
		Problem problem = (Problem) vars[0].getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntExpArray intvars = new IntExpArray(constrainer, vars.length);
		for (int i = 0; i < vars.length; i++) {
			IntExp cvar = (IntExp) vars[i].getImpl();
			intvars.set(cvar, i);
		}
		IntExp scalProd = constrainer.scalarProduct(intvars, values);
		problem.defineConstraintImpl(this, scalProd, oper, var);
	}

	public Linear(Var[] vars, String oper, Var var) {
		super(vars[0].getProblem(), name);
		Problem problem = (Problem) vars[0].getProblem();
		Constrainer constrainer = problem.getConstrainer();
		IntExpArray intvars = new IntExpArray(constrainer, vars.length);
		for (int i = 0; i < vars.length; i++) {
			IntExp cvar = (IntExp) vars[i].getImpl();
			intvars.set(cvar, i);
		}
		IntExp sum = constrainer.sum(intvars);
		problem.defineConstraintImpl(this, sum, oper, var);
	}
}
