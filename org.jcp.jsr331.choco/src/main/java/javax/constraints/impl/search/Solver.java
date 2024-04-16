//*************************************************
//*  J A V A  C O M M U N I T Y  P R O C E S S    *
//*                                               *
//*              J S R  3 3 1                     *
//*                                               *
//*       CHOCO-BASED IMPLEMENTATION              *
//*                                               *
//* * * * * * * * * * * * * * * * * * * * * * * * *
//*          _       _                            *
//*         |   (..)  |                           *
//*         |_  J||L _|        CHOCO solver       *
//*                                               *
//*    Choco is a java library for constraint     *
//*    satisfaction problems (CSP), constraint    *
//*    programming (CP) and explanation-based     *
//*    constraint solving (e-CP). It is built     *
//*    on a event-based propagation mechanism     *
//*    with backtrackable structures.             *
//*                                               *
//*    Choco is an open-source software,          *
//*    distributed under a BSD licence            *
//*    and hosted by sourceforge.net              *
//*                                               *
//*    + website : http://choco.emn.fr            *
//*    + support : choco@emn.fr                   *
//*                                               *
//*    Copyright (C) F. Laburthe,                 *
//*                  N. Jussien    1999-2009      *
//* * * * * * * * * * * * * * * * * * * * * * * * *
package javax.constraints.impl.search; 

/**
 * An implementation of the interface "Solver" by extending 
 * the common class AbstractSolver
 * @author J.Feldman
 * Modified by: 
 * JF, Oct.3,2010: Added custom SearchStrategies
 */

import choco.cp.solver.CPSolver;
import choco.kernel.common.logging.ChocoLogging;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.branch.AbstractIntBranchingStrategy;
import choco.kernel.solver.variables.integer.IntDomainVar;

import javax.constraints.*;
import javax.constraints.SearchStrategy;
import javax.constraints.SearchStrategy.SearchStrategyType;

public class Solver extends AbstractSolver {
	
//	ContradictionException contradictionException;
	final CPSolver chocoSolver;
	boolean modelRead;
	boolean propagated;
	private long _max_occupied_memory;

	public Solver(Problem problem) {
		super(problem);
//		contradictionException = ContradictionException.build();
		chocoSolver = new CPSolver();
		modelRead = false;
		propagated = false;
		readChocoModel();
//		setSearchStrategy(newSearchStrategy());
	}

	public CPSolver getChocoSolver() {
		return chocoSolver;
	}
	
	/**
	 * This methods returns a new default search strategy 
	 * @return a new default search strategy
	 */
	public SearchStrategy newSearchStrategy() {
		readChocoModel();
		SearchStrategy strategy = new javax.constraints.impl.search.SearchStrategy(this);
		strategy.setVars(getProblem().getVars());
		return strategy;
	}
	
	public void readChocoModel() {
		if (! modelRead) {
			javax.constraints.impl.Problem p = (javax.constraints.impl.Problem) getProblem();
			chocoSolver.read(p.getChocoModel());
			modelRead = true;
			//log(chocoSolver.pretty());
		}
	}
	
	public boolean propagate() {
		if (propagated)
			return true;
		try {
			chocoSolver.propagate();
			//problem.log("After chocoSolver.propagate()");
			//problem.log(problem.getVars());
			propagated = true;
			return true;
		} 
		catch (ContradictionException e) {
			//e.printStackTrace();
			log("Constraint propagation failed: "+e);
			return false;
		}
	}

	/**
	 * This method attempts to find a solution of the problem, for which the solver was defined. 
	 * It uses the search strategy defined by the method setSearchStrategy(). 
	 * It returns the found solution (if any) or null. It also saves the solution and makes it 
	 * available through the method getSolution(). 
	 * If a solution is not found, the problem state is restored 
	 * to that of before the invocation of this method.
	 * If a solution is found, the problem state will be restored only if the parameter
	 * "restore" is true. Otherwise all problem variables will be instantiated with the solution
	 * values.
	 *
	 * The search can be limited by time, number of failed attempts, etc.
	 *
	 * @param restoreOrNot defines if the problem state should be restored after a solution is found
	 * @return a Solution if the search is successful or null 
	 */
	public Solution findSolution(ProblemState restoreOrNot) {
		
		readChocoModel();
		
//		log(chocoSolver.pretty());
		
		if (!propagate())
			return null;
		
		int worldIndex = 0;		
		if (restoreOrNot == ProblemState.RESTORE) {
			worldIndex = getChocoSolver().getWorldIndex(); // ??
			getChocoSolver().worldPush();
		}
		
//		SearchStrategy strategy = (SearchStrategy)getSearchStrategy();
//			//(SearchStrategy)getSearchStrategies().elementAt(0);
//		chocoSolver.attachGoal(strategy.getChocoStrategy());
//		for (int i = 1; i < getSearchStrategies().size(); i++) {
//			SearchStrategy addStrategy = (SearchStrategy)getSearchStrategies().elementAt(i);
//			chocoSolver.addGoal(addStrategy.getChocoStrategy());
//		}
		combineSearchStrategies();
        // CPRU: solve() combines calls to generateSearchStrategy() and launch().
        // calling generateSearchStrategy() twice has unexpected side effects.
		chocoSolver.solve();  // recommended by Charles
		ChocoLogging.flushLogs();
		
		Solution solution = null;
		if (chocoSolver.isFeasible().equals(Boolean.TRUE)) {
			solution = new BasicSolution(this,1);
		}

		if (restoreOrNot == ProblemState.RESTORE)
			getChocoSolver().worldPopUntil(worldIndex);
		
		return solution;
	
	}
	
	public AbstractIntBranchingStrategy makeBranching(SearchStrategy strategy) {
		
		AbstractIntBranchingStrategy branching;
		if (strategy.getType().equals(SearchStrategyType.CUSTOM)) {
			branching = new StrategyAsBranching(strategy);
		}
		else {
			javax.constraints.impl.search.SearchStrategy myStrategy =
				(javax.constraints.impl.search.SearchStrategy)strategy;
			branching = myStrategy.getChocoStrategy();
		}
		return branching;
	}
	
	public void combineSearchStrategies() {
		AbstractIntBranchingStrategy branching = makeBranching(getSearchStrategy());
		chocoSolver.addGoal(branching);
		for (int i = 1; i < getSearchStrategies().size(); i++) {
			AbstractIntBranchingStrategy nextBranching = makeBranching(getSearchStrategies().elementAt(i));
			chocoSolver.addGoal(nextBranching);
		}
	}
	
	/**
	 * This method attempts to find the solution that minimizes/maximizes the objective variable.
	 * It uses the search strategy defined by the method setSearchStrategy(strategy). 
	 * The optimization process can be also controlled by:
	 * <ul>
	 * <li> OptimizationTolarance that is a difference between optimal solutions - see setOptimizationTolarance()
	 * <li> MaxNumberOfSolutions that is the total number of considered solutions - may be limited by the method
	 * setMaxNumberOfSolutions()
	 * <li> TotalTimeLimit that is the number of seconds allocated for the entire optimization process.
	 * </ul>
	 * <br> At the same time the time for one iteration inside
	 * optimization loop (a search of one solution) can be also limited by the use of the
	 * special type of search strategy. 
	 * <br> The problem state after the execution of this method is always restored. All variables
	 * that were added to the problems (plus the objectiveVar) will have their assigned values 
	 * saved inside the optimal solution. 
	 * 
	 * @param objective Objective.MINIMIZE or Objective.MAXIMIZE
	 * @param objectiveVar the variable that is being minimized/maximized
	 * @return Solution if a solution is found,
	 *         null if there are no solutions.
	 */
	public Solution findOptimalSolution(Objective objective, Var objectiveVar) {
	    OptimizationStrategy optimizationStrategy = getOptimizationStrategy();
        if (optimizationStrategy.equals(OptimizationStrategy.DICHOTOMIZE))
            return findOptimalSolutionDichotomize(objective, objectiveVar); 
        if (optimizationStrategy.equals(OptimizationStrategy.BASIC))
            return findOptimalSolutionBasic(objective, objectiveVar);
        // OptimizationStrategy.NATIVE
		IntDomainVar chocoObjective = ((javax.constraints.impl.Var)objectiveVar).getChocoDomainVar();
		getChocoSolver().setObjective(chocoObjective);
		addObjective(objectiveVar);
		combineSearchStrategies();
		Solution solution = null;
		Boolean b = false;
		if ( objective.equals(Objective.MAXIMIZE) ) {
			b = getChocoSolver().maximize(true);
		}
		else {
			b = getChocoSolver().minimize(true);
		}
		
		if (b == Boolean.TRUE)
			solution = new BasicSolution(this,1);
		
		return solution;
	}
	
	
	/**
	 * Creates a solution iterator that allows a user to search and navigate
	 * through multiple solutions.
	 * 
	 * @return a solution iterator
	 */
	public SolutionIterator solutionIterator() {
		return new SolutionIterator(this);
	}
	
	
//	public Solution createSolution() {
//		Solution solution = new BasicSolution(this);
//		for (int i = 0; i < chocoSolver.getNbIntVars(); i++) {
//			final IntVar v = chocoSolver.getIntVar(i);
//			if (v.isInstantiated()) {
//				solution.setValue(i,v.getVal());
//			}
//		}
//		for (int j = 0; j < chocoSolver.getNbRealVars(); j++) {
//			final RealVar v = chocoSolver.getRealVar(j);
//			if (v.isInstantiated()) {
//				solution.setRealValue(j,v.getValue());
//			}
//		}
//
//		for (int k = 0; k < chocoSolver.getNbSetVars(); k++) {
//			final SetVar v = chocoSolver.getSetVar(k);
//			if (v.isInstantiated()) {
//				solution.setSetValue(k,v.getValue());
//			}
//		}
//		return solution;
//	}
	
	@Override
	public boolean applySolution(Solution solution) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void trace(Var var) {
		// TODO Auto-generated method stub

	}

	@Override
	public void trace(Var[] vars) {
		// TODO Auto-generated method stub

	}

	@Override
	public void trace(VarSet setVar) {
		// TODO Auto-generated method stub

	}


	/**
	 * If flag is true, all failures will be traced (logged)
	 * 
	 * @param flag boolean
	 */
	public void traceFailures(boolean flag) {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem) getProblem();
		if (flag)
            ChocoLogging.toSearch();
        else
            ChocoLogging.toSilent();
	}

	@Override
	public void logStats() {
		log("*** Execution Profile ***");
		ChocoLogging.flushLogs(); // ??

//		long occupied_memory = Runtime.getRuntime().totalMemory()
//				- Runtime.getRuntime().freeMemory();
//		log("Occupied memory: " + occupied_memory);
		long executionTime = System.currentTimeMillis() - getSolverStartTime();
		log("Execution time: " + executionTime + " msec");
	}

}
