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
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.impl.search.BasicSolution;

/**
 * This goal is trying to resolve the problem using a solution
 * passed as a parameter.
 */
public class GoalApplySolution extends Goal {

	Solution solution;

	public GoalApplySolution(Solution solution) {
		super(solution.getSolver(), "ApplySolution");
		this.solution = solution;
	}

	/**
	 * Adds a solution to the array of the current problem solutions.
	 * Increases the total number of solutions. If it has reaches
	 *  getMaxNumberOfSolutions() then after adding the solution the goal fails.
	 */
	public Goal execute() throws Exception {
		Problem p = getProblem();
		trace();
		Var[] problemVars = p.getVars();
		BasicSolution sol = (BasicSolution)solution;
		for(int i=0; i < solution.getNumberOfVars(); i++) {
			p.post(problemVars[i], ">=", sol.getMin(i));
			p.post(problemVars[i], "<=", sol.getMax(i));
		}
		
		VarReal[] problemVarReals = p.getVarReals();
		for(int i=0; i < solution.getNumberOfVarReals(); i++) {
			p.post(problemVarReals[i], ">=", sol.getMinReal(i));
			p.post(problemVarReals[i], "<=", sol.getMaxReal(i));
		}
		return null; 
	}
}
