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
 * This is a native Choco's goal created based on a high-level Goal
 * passed to it as a parameter. GoalThis simple execute the method "execute"
 * of the high-level goal. It is used to implement high-level goals using a low-level
 * solver.
 * 
 * @author J.Feldman
 */

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.goals.Goal;
import choco.kernel.solver.goals.GoalType;

public class GoalThis implements Goal {
	javax.constraints.impl.search.goal.Goal goal;

	public GoalThis(javax.constraints.impl.search.goal.Goal goal) {
		this.goal = goal;
	}
	
	public Goal execute(Solver s) throws ContradictionException {
		try {
			javax.constraints.impl.search.goal.Goal subgoal = goal.execute();
			if (subgoal == null)
				return null;
			else {
				return (Goal)subgoal.getImpl();
			}
		} catch (Exception e) {
			goal.getProblem().log("Failure during execution of goal '"
					+ goal.getName()+"': " + e.getMessage());
			throw ContradictionException.build(); // ??
		}
	}

	public String pretty() {
    	return "GoalThis";
    }
	
	public GoalType getType() {
    	return GoalType.INST; //??
    }
}
