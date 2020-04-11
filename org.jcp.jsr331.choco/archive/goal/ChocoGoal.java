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
 * This class represent a goal that is a building block for different
 * high level search strategies. Several goals are predefined on the interface level:
 * GoalAssignValues, GoalAssignValue, GoalFail, GoalPrint, and more. <br>
 * Goals ConstrainerGoalAnd and GoalOr allow a developer to combine different
 * goals in a problem specific way.  The resulting goal is a parameter of
 * the main search method Problem.solve(Goal goal).
 *
 * @author J.Feldman
 */

import javax.constraints.Solver;
import javax.constraints.impl.search.goal.Goal;
import javax.constraints.impl.search.goal.GoalThis;

public class ChocoGoal extends Goal {
	
	public ChocoGoal(Solver solver) {
		this(solver,"");
	}
	
	public ChocoGoal(Solver solver, String name) {
		super(solver);
		setName(name);
	}
	
	public ChocoGoal(Solver solver, choco.kernel.solver.goals.Goal goal) {
		super(solver);
		setImpl(goal);
	}
	
	/**
	 * To create a new goal on the javax.constraints level without necessity to
	 * go down to the underlying CP solver, one needs to use a goal that will
	 * serve as an implementation object. "goalThis" serves exactly these
	 * purposes and actually executes the method execute() from the Goal passed
	 * as a parameter.
	 * 
	 * @param goal
	 */
	public Object goalThis(Goal goal) {
		return new GoalThis(goal);
	}

	/**
	 * Method that executes an underlying goal in the proper implementation.
	 * @return
	 * @throws
	 */
	public Goal execute() throws Exception {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		choco.kernel.solver.goals.Goal goal = (choco.kernel.solver.goals.Goal) getImpl();
		choco.kernel.solver.goals.Goal subgoal = goal.execute(p.getChocoSolver());
		if (subgoal == null) // ??
			return null;
		else
			return new ChocoGoal(getSolver(),subgoal);
	}
	
	public String pretty() {
		return "ChocoGoal";
	}

}
