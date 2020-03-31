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
import javax.constraints.Solution;
import javax.constraints.Solver;

public class GoalLogSolution extends Goal {
	
	Solution solution;
	boolean current;

	public GoalLogSolution(Solution solution) {
		super(solution.getSolver(), "logSolution");
		this.solution = solution;
		current = false;
	}
	
	public GoalLogSolution(Solver solver) {
		super(solver, "logSolution");
		this.solution = null;
		current = true;
	}

	public Goal execute() throws Exception {
		trace();
		StringBuffer buf = new StringBuffer();
		buf.append("\n");
		Solver solver = getSolver();
		Problem problem = solver.getProblem();
		if (current) {
			solution = solver.getSolution(solver.getNumberOfSolutions()-1);
		}
		if (solution == null) {
			buf.append("No solutions");
		}
		else {
			buf.append(solution.toString());
		}
		problem.log(buf.toString());
		return null;
	}
}
