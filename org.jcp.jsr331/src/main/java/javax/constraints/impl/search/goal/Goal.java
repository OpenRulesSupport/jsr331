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
import javax.constraints.impl.search.AbstractSearchStrategy;

/**
 * This is a base implementation for a goal. 
 */

abstract public class Goal extends AbstractSearchStrategy {
	
	SolverWithGoals solver; 
	
	public Goal(Solver solver) {
		this(solver,"");
	}
	
	public Goal(Solver solver, String name) {
		super(solver);
		setType(SearchStrategyType.GOAL);
		setName(name);
		this.solver = (SolverWithGoals)solver;
		setImpl(this.solver.goalThis(this));
	}
	
	/**
	 * Method that executes an underlying goal in the proper implementation.
	 * @return goal or null
	 * @throws Exception exception
	 */
	abstract public Goal execute() throws Exception;
	
	/**
	 * Returns a solver to which this goal belongs.
	 * @return a solver to which this goal belongs.
	 */
	public SolverWithGoals getSolver() {
		
		return solver;
	}

//	/**
//	 * Returns the problem to which this goal belongs.
//	 * @return the problem to which this goal belongs.
//	 */
//	public Problem getProblem() {
//		return solver.getProblem();
//	}

	/**
	 * Returns an "AND" GoalI. The GoalI "AND" succeeds if both of the Goals 'this' and
	 *         the parameter 'goal' succeed. The GoalI "AND" fails if at least one of
	 *         the Goals 'this' or the parameter 'goal' fail.
	 * @param goal the other GoalI which is part of the new "AND" GoalI.
	 * @return solver.and(this, goal)
	 */
	public Goal and(Goal goal) {
		return solver.and(this,goal);
	}

	/**
	 * Returns an "OR" GoalI. The goal "OR" succeeds if at least one of the Goals 'this'
	 *         or the parameter 'goal' succeeds. The GoalI "OR" fails if both Goals 'this'
	 *         and the parameter 'goal' fail.
	 * @param goal the other GoalI which is part of the new "OR" GoalI.
	 * @return solver.or(this, goal)
	 */
	public Goal or(Goal goal) {
		return solver.or(this,goal);
	}

	public final void trace() {
		if (solver.isTraceExecution()) {
			getProblem().log("Goal: "+getName());
		}
	}

}
