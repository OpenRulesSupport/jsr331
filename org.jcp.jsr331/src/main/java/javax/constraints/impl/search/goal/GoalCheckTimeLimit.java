//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl.search.goal;

import javax.constraints.Problem;
import javax.constraints.Solver;

/**
 * This goal checks if time limit exceeded and if yes it forces solver to
 * backtrack.
 */

public class GoalCheckTimeLimit extends Goal {

	GoalCheckTimeLimit(Solver solver) {
		super(solver, "CheckTimeLimit");
	}

	public Goal execute() throws Exception {
		trace();
		SolverWithGoals solver = getSolver();
		Problem problem = solver.getProblem();
		if (solver.checkTimeLimit()) {
			if (solver.isTimeLimitExceeded() == false) {
				solver.setTimeLimitExceeded(true);
				String msg = "Time limit " + solver.getTimeLimit() + " has been exceeded";
				problem.log(msg);
			}
			solver.backtrack();
		}
		return null;
	}
}
