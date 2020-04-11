package javax.constraints.impl.search;

import javax.constraints.SearchStrategy;

import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractIntBranchingStrategy;
import choco.kernel.solver.search.IntBranchingDecision;

public class StrategyAsBranching extends AbstractIntBranchingStrategy {
	
	SearchStrategy strategy;

	public StrategyAsBranching(SearchStrategy strategy)	{
		this.strategy = strategy;
	}
	
	/**
	 * selecting the object under scrutiny (that object on which an alternative will be set)
	 *
	 * @return the object on which an alternative will be set (often  a variable)
	 */
	public Object selectBranchingObject() throws ContradictionException {
		strategy.run();
		return null;
	}
	
	/**
	   * Performs the action, 
	   * so that we go down a branch from the current choice point.
	   * @param decision the decision to apply.
	   * @throws choco.kernel.solver.ContradictionException if a domain empties or a contradiction is
	   * infered
	   */
	  public void goDownBranch(IntBranchingDecision decision) throws ContradictionException {
	  }

	  /**
	   * Performs the action,
	   * so that we go up the current branch to the father choice point.
	   * @param decision the decision that has been set at the father choice point
	   * @throws choco.kernel.solver.ContradictionException if a domain empties or a contradiction is
	   * infered
	   */
	  public void goUpBranch(IntBranchingDecision decision) throws ContradictionException {}

	  /**
	   * compute the first decision by setting a branching value or modifying the branching object
	   * @param decision the current decision
	   */
	  public void setFirstBranch(IntBranchingDecision decision) {
	  }

	  /**
	   * compute the next decision by setting a branching value or modifying the branching object
	   * @param decision the current decision
	   */
	  public void setNextBranch(IntBranchingDecision decision) {}

	  /**
	   * Checks whether all branches have already been explored at the
	   * current choice point.
	   * @param decision the last decision applied
	   * @return true if no more branches can be generated
	   */
	  public boolean finishedBranching(IntBranchingDecision decision) { return true; }
	  
	  
	  /**
	   * The logging message associated with the current decision.
	   * @param decision current decision
	   * @return logging message.
	   */
	  public String getDecisionLogMessage(IntBranchingDecision decision) { return "Done "+strategy.getName(); }

}
