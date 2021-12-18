package javax.constraints.impl.search;

import javax.constraints.ProblemState;
import javax.constraints.impl.search.Solution;
import javax.constraints.impl.search.Solver;

import JSetL.Failure;


/**
 * Implement the JSR331 solution iterator extending the 
 * interface SolutionIterator. The implementation is base on the solver 
 * JSetL. The class allows a user to search and to iterate through multiple 
 * solutions.
 * 
 * @author Fabio Biselli
 *
 */
public class SolutionIterator implements javax.constraints.SolutionIterator {
	
	Solver solver;
	Solution solution;
	int solutionNumber;
	Boolean firstcall = true;
	Boolean checked = false;
	
	/**
	 * Build a new SolutionIterator bound to a given Solver.
	 * 
	 * @param s the Solver.
	 */
	public SolutionIterator(javax.constraints.Solver s) {
		solver = (Solver) s;
		solution = null;
		solutionNumber = 0;
	}

	/**
	 * Returns a boolean values, true if the Problem which the Solver is bound
	 * has a next solution, false otherwise.
	 * 
	 */
	public boolean hasNext() {
		checked = true;
		if (firstcall) {
			firstcall = false;
			solution = solver.findSolution(ProblemState.RESTORE);
			if (solution == null)
				return false;
			return true;
		}
		return solver.hasNext();
	}

	/**
	 * This method must to be called after check that <code>hasNext()</code> 
	 * return true. Once <code>next()</code> is called, the flag 
	 * <code>checked</code> is restored as false,
	 * than the <code>hasNext()</code> must be called again, each time.
	 * 
	 * @return a new Solution.
	 */
	public Solution next() {
		if (!checked)
			throw new RuntimeException("Cannot use SolutionIterator.next() " +
					"before checking the hasNext() returned true");
		try {
			solution = new Solution(solver, solutionNumber);
			solution.setSolutionNumber(solutionNumber);
			solutionNumber++;
		} catch (Failure e) {
			e.printStackTrace();
			return null;
		}
		checked = false;
		return solution;
	}

}

