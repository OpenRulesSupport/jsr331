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

import javax.constraints.Problem;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.Goal;

/**
 * This is a native Constrainer's goal created based on a high-level Goal
 * passed to it as a parameter. GoalThis simple execute the method "execute"
 * of the high-level goal. It is used to implement high-level goals using a low-level
 * solver.
 *
 */

public class GoalThis extends com.exigen.ie.constrainer.GoalImpl {
	javax.constraints.impl.search.goal.Goal goal;

	public GoalThis(Constrainer c, javax.constraints.impl.search.goal.Goal goal) {
		super(c,goal.getName());
		this.goal = goal;
	}

	public Goal execute() throws Failure {
		//goal.trace();
		Problem problem = goal.getProblem();
		try {
			javax.constraints.impl.search.goal.Goal newGoal = goal.execute();
			if (newGoal == null)
				return null;
			else
				return (Goal)newGoal.getImpl();
		} catch (Failure f) {
			//problem.debug("Failure during execution of goal '" + goal.getName()+"'");
			throw new Failure();
		}
		catch (Exception e) {
			String msg = "Unexpected exception during execution of goal '" + goal.getName()+"'";
			problem.log(msg);
			problem.log(e.toString());
			e.printStackTrace();
			throw new RuntimeException(msg);
		}

	}
}
