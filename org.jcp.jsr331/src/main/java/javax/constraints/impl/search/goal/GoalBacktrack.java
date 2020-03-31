//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl.search.goal;

import javax.constraints.Solver;

/**
 * This goal forces the solver to backtrack
 */

public class GoalBacktrack extends Goal {

	public GoalBacktrack(Solver solver) {
		super(solver);
		setName("backtrack");
	}

	public Goal execute() throws Exception {
		trace();
		getSolver().backtrack();
		return null;
	}
}
