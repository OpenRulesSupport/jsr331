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
 * This goal checks if total time limit (in seconds) for solveAll() is exceeded
 * and fails if yes.
 */

public class GoalCheckTotalTimeLimit extends Goal {
	int totalTimeLimit;
	long startTime;

	public GoalCheckTotalTimeLimit(Solver solver) {
		super(solver,"CheckTotalTimeLimit");
		startTime = System.currentTimeMillis();
	}

	@Override
	public Goal execute() throws Exception {
		trace();
		SolverWithGoals solver = getSolver();
		Problem problem = solver.getProblem();
		long currTime = System.currentTimeMillis();
		int timeLimit = solver.getTimeLimit();
		//if ( timeLimit > 0 && timeLimit*1000 < currTime - startTime) {
		if ( timeLimit > 0 && timeLimit < currTime - startTime) {
			problem.log("The search is interrupted by TimeLimit "+timeLimit+ " milliseconds");
			solver.backtrack();
		}
		return null;
	}
}
