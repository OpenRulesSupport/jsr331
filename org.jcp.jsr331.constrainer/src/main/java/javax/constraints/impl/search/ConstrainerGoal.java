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
package javax.constraints.impl.search;

import javax.constraints.Solver;
import javax.constraints.impl.search.goal.Goal;

import com.exigen.ie.constrainer.Constrainer;

/**
 * This interface represent a goal that is a building block for different
 * high level search strategies. Several goals are predefined on the interface level:
 * GoalAssignValues, GoalAssignValue, GoalFail, GoalPrint, and more. <br>
 * Goals ConstrainerGoalAnd and GoalOr allow a developer to combine different
 * goals in a problem specific way.  The resulting goal is a parameter of
 * the main search method Problem.solve(Goal goal).
 * <br>
 * All goals have various implementations in different CP solvers.
 * More goals can be added as their implementations become available and standardized. <br>
 *
 *
 */

public class ConstrainerGoal extends Goal {
	
	public ConstrainerGoal(Solver solver) {
		this(solver,"");
	}
	
	public ConstrainerGoal(Solver solver, String name) {
		super(solver);
		setName(name);
	}
	
	public ConstrainerGoal(Solver solver, com.exigen.ie.constrainer.Goal goal) {
		super(solver);
		setImpl(goal);
		setName(goal.name());
	}
	
	public Constrainer getConstrainer() {
		com.exigen.ie.constrainer.Goal goal = (com.exigen.ie.constrainer.Goal)getImpl();
		return goal.constrainer();
	}
	
	/**
	 * To create a new goal on the javax.constraints level without necessity to
	 * go down to the underlying CP solver, one needs to use a goal that will
	 * serve as an implementation object. "goalThis" serves exactly these
	 * purposes and actually executes the method execute() from the Goal passed
	 * as a parameter.
	 * @param goal a goal
	 * @return a new goal
	 */
	public Object goalThis(Goal goal) {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		return new GoalThis(p.getConstrainer(), goal);
	}

	/**
	 * Method that executes an underlying goal in the proper implementation.
	 * @return a goal
	 * @throws Exception if fails
	 */
	public Goal execute() throws Exception {
		com.exigen.ie.constrainer.Goal goal = (com.exigen.ie.constrainer.Goal) getImpl();
		com.exigen.ie.constrainer.Goal subgoal = goal.execute();
		if (subgoal == null)
			return null;
		else
			return new ConstrainerGoal(getSolver(),subgoal); // goal??
	}

}
