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
 * This goal is used inside solveAll() to stop or continue the search.
 * It checks if the maximal number of solutions has been reached.
 * If yes, it returns null ("do nothing") and search will be stopped.
 * If no, it returns goalFail() and the search will be continued.
 */
public class GoalCheckMaxNumberOfSolutions extends Goal {

	public GoalCheckMaxNumberOfSolutions(Solver solver) {
		super(solver, "check max solutions");
	}

	public Goal execute() throws Exception {
		trace();
		SolverWithGoals solver = getSolver();
		int max = solver.getMaxNumberOfSolutions();
		if (max <= 0) {
			//solver.backtrack();
			return null;
		}
		if (solver.getSolutions() == null || solver.getNumberOfSolutions() == max) {
		    String msg = "Search interrupted by the MaxNumberOfSolutions: " + max;
			solver.log(msg);
			solver.addExplanation(msg);
			solver.backtrack();
			return null; // finish search
		}
		//solver.backtrack();
		return null;
	}
}
