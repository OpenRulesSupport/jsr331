package javax.constraints.impl.search;

import javax.constraints.SearchStrategy;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.goals.Goal;
import choco.kernel.solver.goals.GoalType;

/**
 * This class represents a generic SearchStrategy (e.g. for logging solutions)
 * as Choco goal to be used during the SearchStrategyList execution - 
 * see findSolution, findOptimalSolution
 * 
 */
public class StrategyAsGoal implements Goal {

	SearchStrategy strategy;

	public StrategyAsGoal(SearchStrategy strategy)	{
		this.strategy = strategy;
	}

	public Goal execute(Solver s) throws ContradictionException {
		strategy.run();
		return null;
	}
	
	public GoalType getType() {
		return GoalType.INST;
	}
	
	public String pretty() {
		return "ChocoGoal: "+strategy.getName();
	}
	
}