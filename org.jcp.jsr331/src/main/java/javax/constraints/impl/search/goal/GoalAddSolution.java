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
import javax.constraints.Var;
import javax.constraints.impl.search.BasicSolution;

/**
 * This goal adds a newly found solution to the array of solutions
 * available via getSolutions()
 */
public class GoalAddSolution extends Goal {

	int solutionNumber;
	Var objective;

	public GoalAddSolution(Solver solver) {
		this(solver,0);
	}
	
	public GoalAddSolution(Solver solver, Var objective) {
		this(solver,0,objective);
	}
	
	public GoalAddSolution(Solver solver, int solutionNumber) {
		this(solver,solutionNumber,null);
	}

	public GoalAddSolution(Solver solver, int solutionNumber, Var objective) {
		super(solver, "addSolution");
		this.solutionNumber = solutionNumber;
		this.objective = objective;
	}

	public Goal execute() throws Exception {
		trace();
		int total = 0;
		if (solver.getSolutions() != null)
			total = solver.getNumberOfSolutions();
		total += 1;
		Solution solution = null;
		if (solutionNumber <= 0 || total == solutionNumber) {
			solution = new BasicSolution(solver, total);
		}
		solver.addSolution(solution);
		return null;
	}
}
