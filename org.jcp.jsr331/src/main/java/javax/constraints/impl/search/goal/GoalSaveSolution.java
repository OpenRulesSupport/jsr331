//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//=============================================
package javax.constraints.impl.search.goal;

import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.impl.search.BasicSolution;

public class GoalSaveSolution extends Goal {

	public GoalSaveSolution(Solver solver) {
		super(solver);
	}

	public Goal execute() throws Exception {
		solver.clearSolutions();
		Solution solution = new BasicSolution(solver, 1);
		solver.addSolution(solution);
//		getProblem().log("Saved solution: " + solution.toString());
		return null;
	}
}
