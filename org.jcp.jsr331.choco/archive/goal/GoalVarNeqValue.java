//*************************************************
//*  J A V A  C O M M U N I T Y  P R O C E S S    *
//*                                               *
//*              J S R  3 3 1                     *
//*                                               *
//*       CHOCO-BASED IMPLEMENTATION              *
//*                                               *
//* * * * * * * * * * * * * * * * * * * * * * * * *
//*          _       _                            *
//*         |  °(..)  |                           *
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
package javax.constraints.impl.search.goal;

/**
 * This goal is used by the solver to provided a value 
 * removal goal for the common goal GoalAssignValue.
 * @author J.Feldman
 */

import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.impl.search.goal.Goal;

public class GoalVarNeqValue extends ChocoGoal {
	Var var;
	int value;
	
	public GoalVarNeqValue(Solver solver, Var var, int value) {
		super(solver,var.getName()+"!="+value);
		this.var = var;
		this.value = value;
	}
	
	/**
	 * Method that executes an underlying goal in the proper implementation.
	 * @return
	 * @throws
	 */
	public Goal execute() throws Exception {
		
		javax.constraints.impl.Var cVar = (javax.constraints.impl.Var)var;
		//cVar.getChocoVar().removeVal(value);
		cVar.getChocoDomainVar().remVal(value);
		return null;
	}
}
