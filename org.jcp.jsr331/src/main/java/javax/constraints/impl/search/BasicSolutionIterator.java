//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl.search;

/**
 * This is a simple implementation of the SolutionIterator interface 
 * that allows a user to search and to iterate through multiple solutions.
 * The expected use:
 * <br>
 * <pre>
 * SolutionIterator iter = solver.solutionIterator();
 * while(iter.hasNext()) {
 *   Solution solution = iter.next();
 *     ...
 * }
 *
 * </pre>
 */

import java.util.Vector;

import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.SolutionIterator;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.ProblemState;
import javax.constraints.impl.constraint.ConstraintNotAllEqual;

public class BasicSolutionIterator implements SolutionIterator {
	
	AbstractSolver solver;
	Solution solution;
	int solutionNumber;
	boolean noSolutions;
	long startTime;
	
	public BasicSolutionIterator(AbstractSolver solver) {
		this.solver = solver;
		solution = null;
		noSolutions = false;
		solutionNumber = 0;
		startTime = System.currentTimeMillis();
	}

	@Override
	public boolean hasNext() {
		if (noSolutions)
			return false;
		solver.setTimeLimitStart(); // reset TimeLimit for one solution search
		solution = null;
		long solutionTimeLimit = solver.getTimeLimit();
        long globalTimeLimit = solver.getTimeLimitGlobal();
		try {
			
			if ( globalTimeLimit > 0) {
				long spentTime = System.currentTimeMillis() - startTime;
				if (spentTime > globalTimeLimit) {
					solver.log("Global time limit " + globalTimeLimit + " millis has been exceeded.");
					return false;		
				}
				if (spentTime+solutionTimeLimit>globalTimeLimit) 
					solver.setTimeLimit((int)(globalTimeLimit-spentTime));
			}
			solution = solver.findSolution(ProblemState.RESTORE);			
		} catch (Exception e) {
			if (solutionTimeLimit > 0) {
				solver.log("Time limit " + solutionTimeLimit + " mills for one solution search has been exceeded");
			}
//			else {
//				solver.log("ERROR: Unexpected search interruption!");
//			}
		}
		
		if (solution == null)
			return false;
		else 
			return true;
	}

	@Override
	public Solution next() {
		if (solution == null)
			throw new RuntimeException("Cannot use SolutionIterator.next() before checking the hasNext() returned true");
		solution.setSolutionNumber(++solutionNumber);
		
		Vector<SearchStrategy> searchStrategies = 
			((AbstractSolver)solver).getSearchStrategies();
		Vector<Var> strategyVars = new Vector<Var>();
		for (SearchStrategy strategy : searchStrategies) {
			Var[] vars = strategy.getVars();
			for (Var var : vars) {
				if (!strategyVars.contains(var))
					strategyVars.add(var);
			}
		}
		
		Var[] vars = ((BasicSolution)solution).getVars();
		int[] values = new int[vars.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = solution.getValue(vars[i].getName());
		}
		try {
			new ConstraintNotAllEqual(vars, values).post();
		} catch (Exception e) {
			noSolutions = true;
		}
		return solution;
	}

}
