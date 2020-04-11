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
 * An implementation of the Constraint "Element"
 * @author J.Feldman
 */


import javax.constraints.Var;
import javax.constraints.impl.Constraint;

import choco.Choco;
import choco.kernel.model.variables.integer.IntegerVariable;

public class Element extends Constraint {
	
	static final String name = "Element";
	
	public Element(int[] array, Var indexVar, String oper, int value) {
		super(indexVar.getProblem(),name);
		if (indexVar.getMin() > array.length - 1 || indexVar.getMax() < 0)
			throw new RuntimeException("elementAt: invalid index variable");
		IntegerVariable elementVar = Choco.makeIntVar("elementVar", array);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		javax.constraints.impl.Var cIndexVar = (javax.constraints.impl.Var)indexVar;
		p.addChocoConstraint(Choco.nth(cIndexVar.getChocoVar(),array,elementVar)); 
		setImpl(p.compare(elementVar,oper,value));
	}
	
	public Element(int[] array, Var indexVar, String oper, Var var) {
		super(indexVar.getProblem(),name);
		if (indexVar.getMin() > array.length - 1 || indexVar.getMax() < 0)
			throw new RuntimeException("elementAt: invalid index variable");
		IntegerVariable elementVar = Choco.makeIntVar("elementVar", array);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		javax.constraints.impl.Var cIndexVar = (javax.constraints.impl.Var)indexVar;
		p.addChocoConstraint(Choco.nth(cIndexVar.getChocoVar(),array,elementVar)); 
		javax.constraints.impl.Var cVar = (javax.constraints.impl.Var)var;
		setImpl(p.compare(elementVar,oper,cVar.getChocoVar()));
	}
	
	public Element(Var[] vars, Var indexVar, String oper, int value) {
		super(indexVar.getProblem(),name);
		if (indexVar.getMin() > vars.length - 1 || indexVar.getMax() < 0)
			throw new RuntimeException("elementAt: invalid index variable");
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		IntegerVariable[] intVars = p.createIntVarArray(vars);
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < intVars.length; i++) {
			if (intVars[i].getLowB() < min)
				min = intVars[i].getLowB();
			if (intVars[i].getUppB() > max)
				max = intVars[i].getUppB();
		}
		IntegerVariable elementVar = Choco.makeIntVar("elementAt",	min, max);
		javax.constraints.impl.Var cIndexVar = (javax.constraints.impl.Var)indexVar;
		p.addChocoConstraint(Choco.nth(cIndexVar.getChocoVar(),intVars,elementVar)); 
		setImpl(p.compare(elementVar,oper,value));
	}
	
	public Element(Var[] vars, Var indexVar, String oper, Var var) {
		super(indexVar.getProblem(),name);
		if (indexVar.getMin() > vars.length - 1 || indexVar.getMax() < 0)
			throw new RuntimeException("elementAt: invalid index variable");
		if (indexVar.getMin() > vars.length - 1 || indexVar.getMax() < 0)
			throw new RuntimeException("elementAt: invalid index variable");
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		IntegerVariable[] intVars = p.createIntVarArray(vars);
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < intVars.length; i++) {
			if (intVars[i].getLowB() < min)
				min = intVars[i].getLowB();
			if (intVars[i].getUppB() > max)
				max = intVars[i].getUppB();
		}
		IntegerVariable elementVar = Choco.makeIntVar("elementAt",	min, max);
		javax.constraints.impl.Var cIndexVar = (javax.constraints.impl.Var)indexVar;
		p.addChocoConstraint(Choco.nth(cIndexVar.getChocoVar(),intVars,elementVar)); 
		javax.constraints.impl.Var cVar = (javax.constraints.impl.Var)var;
		setImpl(p.compare(elementVar,oper,cVar.getChocoVar()));
	}
}
