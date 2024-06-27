//*************************************************
//*  J A V A  C O M M U N I T Y  P R O C E S S    *
//*                                               *
//*              J S R  3 3 1                     *
//*                                               *
//*       CHOCO-BASED IMPLEMENTATION              *
//*                                               *
//* * * * * * * * * * * * * * * * * * * * * * * * *
//*          _       _                            *
//*         |  �(..)  |                           *
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
 * An implementation of the Constraint "AllDifferent"
 * @author J.Feldman
 * Modified by:
 */

import javax.constraints.ConsistencyLevel;
import javax.constraints.impl.Constraint;

import choco.Choco;
import choco.cp.solver.constraints.global.BoundAllDiff;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.variables.integer.IntDomainVar;


/**
 * This is one of the most popular Constraint that
 * states that all of the elements within the array of variables "vars" 
 * must take different values from each other.
 * The RI should overload the method "defineNativeImpl" and 
 * may add its own additional constructors.
 */

public class AllDifferent extends Constraint {
	
	IntegerVariable[] intVars;
	Object chocoAllDiff;

	public AllDifferent(javax.constraints.Var[] vars) {
		super(vars[0].getProblem(),"AllDiff");
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)vars[0].getProblem();
		intVars = p.createIntVarArray(vars);
		chocoAllDiff = Choco.allDifferent(intVars);
		setImpl(chocoAllDiff);
	}
	
	/**
	 * This method is used to post the constraint. Additionally to post() 
	 * this methods specifies a particular level of consistency that will
	 * be selected by an implementation to control the propagation strength of
	 * this constraint. If this method is not overloaded by an implementation, it will work as a post(). 
	 * @param consistencyLevel
	 * @throws RuntimeException if a failure happened during the posting
	 */
	public void post(ConsistencyLevel consistencyLevel) {
		if (consistencyLevel.equals(ConsistencyLevel.BOUND)) {
//			javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
//			IntDomainVar[] intDomVars = p.getChocoSolver().getVar(vars);
//			chocoAllDiff = new BoundAllDiff(intDomVars, true);
//			setImpl(chocoAllDiff);
			choco.kernel.model.constraints.Constraint c = (choco.kernel.model.constraints.Constraint)getImpl();
			c.addOption("cp:bc");
		}
		post();
	}
}
