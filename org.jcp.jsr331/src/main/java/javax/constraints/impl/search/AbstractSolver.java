//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Common Implementation
// 
//============================================= 
package javax.constraints.impl.search;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import javax.constraints.Objective;
import javax.constraints.OptimizationStrategy;
import javax.constraints.Problem;
import javax.constraints.ProblemState;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.SolutionIterator;
import javax.constraints.Solver;
import javax.constraints.ValueSelector;
import javax.constraints.Var;
import javax.constraints.VarReal;
import javax.constraints.VarSelector;
import javax.constraints.ValueSelectorType;
import javax.constraints.VarSelectorType;
import javax.constraints.extra.ReversibleAction;
import javax.constraints.impl.AbstractProblem;

/**
 * This class defines different solving methods for a given Problem
 * including different flavors of such methods as:
 * solve, solveAll, findSolution, minimize, maximize
 *
 */

abstract public class AbstractSolver implements Solver {
	
	static public int UNLIMITED = 0;
	
	Problem problem;
	protected Vector<SearchStrategy> searchStrategies;
	
	Vector<Solution> solutions;
	int 	maxNumberOfSolutions;
	int 	timeLimit;
	long 	timeLimitStart;
	boolean timeLimitExceeded;
	int 	globalTimeLimit;
	long 	startTime;
	int 	tolerance;
	long 	solverStartTime;
	boolean traceExecution;
	boolean traceSolutions;
	OptimizationStrategy optimizationStrategy;
	
//	/**
//	 * This method executes the searchStrategy passed as the first parameter. 
//	 * It returns true if the searchStrategy execution succeeds.  
//	 * If the execution fails, the problem state will be restored.   
//	 * If the execution succeeds, the problem state will be restored 
//	 * only if the parameter "restoreOrNot" is ProblemState.RESTORE. 
//	 * @param searchStrategy
//	 * @param restoreOrNot defines if the problem state should be restored after successful execution
//	 * @return true if the execution succeeds or false if it fails.
//	 */
//	abstract public boolean execute(SearchStrategy searchStrategy, ProblemState restoreOrNot);
	
	public Problem getProblem() {
		return problem;
	}
	
	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	
	public AbstractSolver() {
		this(null);
	}
	
	public AbstractSolver(Problem problem) {
		startTime = System.currentTimeMillis();
		this.problem = (AbstractProblem)problem;
		if (problem != null)
			problem.setSolver(this);
		searchStrategies = new Vector<SearchStrategy>();
		solverStartTime = System.currentTimeMillis();
		optimizationStrategy = OptimizationStrategy.BASIC;
		maxNumberOfSolutions = -1;
		timeLimit = UNLIMITED; 
		globalTimeLimit = UNLIMITED; 
		setTimeLimitStart();
		setTimeLimitExceeded(false);
		tolerance = 0;
		clearSolutions();
		traceExecution(false);
		traceSolutions(false);
//		setSearchStrategy(newSearchStrategy());
	}
	
	/**
	 * Creates a copy of the problem in its current state
	 */
	public void saveProblem() {
	}
	
	/**
	 * Restores a previously saved problem
	 */
	public void restoreProblem() {
	
	}
	
	public Vector<SearchStrategy> getSearchStrategies() {
		if (searchStrategies.isEmpty()) {
			SearchStrategy strategy = getSearchStrategy();
			searchStrategies.add(strategy);
		}
		return searchStrategies;
	}
	
	/**
	 * Returns the number of the last solution found, or 0 if no solutions have
	 * been found yet (or the search for a solution was not yet launched).
	 * 
	 * @return the number of the last solution found, or 0 if no solution have
	 *         been found yet (or the search for a solution was not yet
	 *         launched).
	 */
	public int getNumberOfSolutions() {
		return solutions.size();
	}

	/**
	 * Returns the maximal number of solutions any search can look for. A value
	 * of -1 indicates infinity, i.e. that there is no limitation on the maximal
	 * number of solutions. The default value is -1.
	 * 
	 * @return the maximal number of solutions any search can look for. A value
	 *         of -1 indicates infinity, i.e. that there is no limitation on the
	 *         maximal number of solutions. The default value is -1.
	 */
	public int getMaxNumberOfSolutions() {
		return maxNumberOfSolutions;
	}

	/**
	 * Sets the limit on the maximal number of solutions any search can look
	 * for. A value of -1 indicates infinity, i.e. that there is no limitation
	 * on the maximal number of solutions. The default value is -1.
	 * 
	 * @param maxNumberOfSolutions
	 *            the new limit for the maximal number of solutions any search
	 *            can look for.
	 */
	public void setMaxNumberOfSolutions(int maxNumberOfSolutions) {
		this.maxNumberOfSolutions = maxNumberOfSolutions;
	}
	
	/**
	 * Returns the duration, in milliseconds, of the imposed time limit on
	 * searches.
	 * 
	 * @return the duration, in milliseconds, of the imposed time limit on
	 *         searches.
	 */
	public int getTimeLimit() {
		return timeLimit;
	}

	/**
	 * Sets the duration, in milliseconds, of the time limit on searches.
	 * 
	 * @param mills
	 *            the new time limit in milliseconds
	 */
	public void setTimeLimit(int mills) {
		this.timeLimit = mills;
	}

	/**
	 * Validates in the difference between the current time and the
	 * getTimeLimitStart() time is more than getTimeLimit()
	 * 
	 * @return true if yes, false if no.
	 */
	public boolean checkTimeLimit() {
		if (getTimeLimit() <= UNLIMITED)
			return false;
		long currTime = System.currentTimeMillis();
		return currTime - getTimeLimitStart() > getTimeLimit();
	}
	
	/**
	 * Returns the duration, in milliseconds, of the imposed time limit 
	 * on global search of all solutions or an optimal solution.
	 * @return the duration, in milliseconds, of the imposed time limit
	 * on global search of all solutions or an optimal solution.
	 */
	public int getTimeLimitGlobal() {
		return globalTimeLimit;
	}

	/**
	 * Sets the duration, in milliseconds, of the time limit 
	 * on global search of all solutions or an optimal solution.
	 * If  timeLimit is not set, it also calls setTimeLimit(mills)
	 * @param mills the new global time limit in milliseconds
	 */
	public void setTimeLimitGlobal(int mills) {
		globalTimeLimit = mills;
		if (timeLimit == UNLIMITED)
			setTimeLimit(mills);
	}
	
	/**
	 * Returns true if the time limit expired during a search, or false if not.
	 * 
	 * In the case of exceeding the time limit, the Solution returned by solve()
	 * is null. The boolean returned by this method can be used to distinguish
	 * between "no solution existed" and "time limit expired", when the Solution
	 * returned is null.
	 * 
	 * @return true if the time limit expired during a search, or false if not.
	 */
	public boolean isTimeLimitExceeded() {
		return timeLimitExceeded;
	}

	/**
	 * Sets whether or not the time limit has expired.
	 * 
	 * @param exceeded
	 *            true if the time limit exceeded, false otherwise.
	 */
	public void setTimeLimitExceeded(boolean exceeded) {
		timeLimitExceeded = exceeded;
	}

	/**
	 * Returns the time, in milliseconds, at which the time limit began
	 * counting.
	 * 
	 * @return the time, in milliseconds, at which the time limit began
	 *         counting.
	 */
	public long getTimeLimitStart() {
		return timeLimitStart;
	}

	/**
	 * Sets the start time as the system current time, for counting milliseconds
	 * to the time limit.
	 */
	public void setTimeLimitStart() {
		timeLimitStart = System.currentTimeMillis();
		timeLimitExceeded = false;
	}
	
	public void clearSolutions() {
		solutions = new Vector<Solution>();
	}
	
	/**
	 * Adds a solution to the current array of solutions
	 */
	public void addSolution(Solution solution) {
		solutions.add(solution);
	}
	

	/**
	 * Returns an array of Solutions, containing all found solutions.
	 * 
	 * @return an array of all found solutions 
	 */
	public Solution[] getSolutions() {
		if (solutions == null || solutions.size() == 0)
			return null;
		Solution[] array = new Solution[solutions.size()];
		for (int i = 0; i < solutions.size(); i++) {
			array[i] = solutions.elementAt(i);
		}
		return array;
	}

	/**
	 * Returns the i-th Solution
	 * 
	 * @return the i-th Solution 
	 * @see AbstractSolver#findAllSolutions()
	 */
	public Solution getSolution(int i) {
		if (solutions == null || solutions.size() == 0 || i < 0 || i >= solutions.size())
			return null;
		return solutions.get(i);
	}

	/**
	 * Returns the first (or only) solution 
	 * 
	 * @return the first (or only) solution
	 */
	public Solution getSolution() {
		return getSolution(0);
	}
	
	/**
	 * Define a searchStrategy that will be used by find solutions methods 
	 * @param strategy
	 * @see AbstractSolver#findSolution() 
	 * @see AbstractSolver#findOptimalSolution(Var)
	 */
	public void setSearchStrategy(SearchStrategy strategy) {
		searchStrategies.clear();
		searchStrategies.add(strategy);
	}
	
	public SearchStrategy getSearchStrategy() {
		if (searchStrategies.isEmpty()) {
			setSearchStrategy(newSearchStrategy());
		}
		return searchStrategies.firstElement();
	}
	
	public SearchStrategy getStrategyLogVariables() {
	   return new StrategyLogVariables(this);
	}
	
	/**
	 * This methods adds the strategy getStrategyLogVariables()
	 * to the end of the strategy execution lists. 
	 */
	public void addStrategyLogVariables() {
		addSearchStrategy(getStrategyLogVariables());
	}
	
	
	public SearchStrategy getLogStrategy(String text) {
	   return new StrategyLog(this,text);
	}
	
	/**
	 * This methods adds the strategy that logs the "text"
	 * to the end of the strategy execution lists. 
	 */
	public void addLogStrategy(String text) {
		addSearchStrategy(getLogStrategy(text));
	}
	
	/**
	 * Define a searchStrategy that will be used by find solutions methods.
	 * This methods takes the default search strategy, and resets its
	 * vars, varSelector, and valueSelector. 
	 * @param vars
	 * @param varSelector
	 * @param valueSelector
	 * @see AbstractSolver#findSolution()
	 * @see AbstractSolver#findOptimalSolution(Var)
	 */
	public void setSearchStrategy(Var[] vars, 
			                      VarSelector varSelector, 
			                      ValueSelector valueSelector) {
		SearchStrategy strategy = getSearchStrategy();
		strategy.setVars(vars);
		strategy.setVarSelector(varSelector);
		strategy.setValueSelector(valueSelector);
		setSearchStrategy(strategy);
	}
	
	/**
	 * Define a searchStrategy that will be used by find solutions methods.
	 * This methods takes the default search strategy, and resets its
	 * vars and varSelector. 
	 * @param vars
	 * @param varSelector
	 * @see AbstractSolver#findSolution()
	 * @see AbstractSolver#findOptimalSolution(Var)
	 */
	public void setSearchStrategy(Var[] vars, 
			                      VarSelector varSelector) {
		SearchStrategy strategy = getSearchStrategy();
		strategy.setVars(vars);
		strategy.setVarSelector(varSelector);
		setSearchStrategy(strategy);
	}
	
	/**
	 * Define a searchStrategy that will be used by find solutions methods.
	 * This methods takes the default search strategy, and resets its
	 * vars and valueSelector. 
	 * @param vars
	 * @param valueSelector
	 * @see AbstractSolver#findSolution() 
	 * @see AbstractSolver#findOptimalSolution(Var)
	 */
	public void setSearchStrategy(Var[] vars, 
			                      ValueSelector valueSelector) {
		SearchStrategy strategy = getSearchStrategy();
		strategy.setVars(vars);
		strategy.setValueSelector(valueSelector);
		setSearchStrategy(strategy);
	}
	
	/**
	 * Define a searchStrategy that will be used by find solutions methods.
	 * This methods takes the default search strategy, and resets its
	 * vars. 
	 * @param vars
	 * @see AbstractSolver#findSolution() 
	 * @see AbstractSolver#findOptimalSolution(Var)
	 */
	public void setSearchStrategy(Var[] vars) {
		SearchStrategy strategy = getSearchStrategy();
		strategy.setVars(vars);
		setSearchStrategy(strategy);
	}
	
//	/**
//	 * Define a searchStrategy that will be used by find solutions methods.
//	 * This methods takes the default search strategy, and resets its
//	 * varSelector. 
//	 * @param varSelector
//	 * @see AbstractSolver#findSolution() 
//	 * @see AbstractSolver#findOptimalSolution(Var)
//	 */
//	public void setSearchStrategy(VarSelector varSelector) {
//		SearchStrategy strategy = getSearchStrategy();
//		strategy.setVarSelector(varSelector);
//		setSearchStrategy(strategy);
//	}
	
//	/**
//	 * Define a searchStrategy that will be used by find solutions methods.
//	 * This methods takes the default search strategy, and resets its
//	 * valueSelector. 
//	 * @param valueSelector
//	 * @see AbstractSolver#findSolution() 
//	 * @see AbstractSolver#findOptimalSolution(Var)
//	 */
//	public void setSearchStrategy(ValueSelector valueSelector) {
//		SearchStrategy strategy = getSearchStrategy();
//		strategy.setValueSelector(valueSelector);
//		setSearchStrategy(strategy);
//	}
	
	/**
	 * This method returns a new default search strategy 
	 * @return a new default search strategy
	 */
	abstract public SearchStrategy newSearchStrategy();
	
	/**
	 * This method adds this strategy 
	 * to the end of the strategy execution list. 
	 * @param strategy
	 * @see AbstractSolver#findSolution() 
	 * @see AbstractSolver#findOptimalSolution(Var)
	 */
	public void addSearchStrategy(SearchStrategy strategy) {
		getSearchStrategy(); // to make sure that at least one search strategy is already defined
		searchStrategies.add(strategy);
	}
	
	/**
	 * This method creates a new instance of the default search strategy, resets its
	 * vars, varSelector, and valueSelector, and then adds this strategy 
	 * to the end of the strategy execution list. 
	 * @param vars
	 * @param varSelector
	 * @param valueSelector
	 * @see AbstractSolver#findSolution() 
 	 * @see AbstractSolver#findOptimalSolution(Var)
	 */
	public void addSearchStrategy(Var[] vars, 
			                      VarSelector varSelector, 
			                      ValueSelector valueSelector) {
		SearchStrategy strategy = newSearchStrategy();
		strategy.setVars(vars);
		strategy.setVarSelector(varSelector);
		strategy.setValueSelector(valueSelector);
		addSearchStrategy(strategy);
	}
	
	/**
	 * This method creates a new instance of the default search strategy, resets its
	 * vars, varSelector, and valueSelector, and then adds this strategy 
	 * to the end of the strategy execution list. 
	 * @param vars
	 * @param varSelectorType
	 * @param valueSelectorType
	 * @see AbstractSolver#findSolution() 
	 * @see AbstractSolver#findOptimalSolution(Var)
	 */
	public void addSearchStrategy(Var[] vars, 
			                      VarSelectorType varSelectorType, 
			                      ValueSelectorType valueSelectorType) {
		SearchStrategy strategy = newSearchStrategy();
		strategy.setVars(vars);
		strategy.setVarSelectorType(varSelectorType);
		strategy.setValueSelectorType(valueSelectorType);
		addSearchStrategy(strategy);
	}
	
	/**
	 * This method creates a new instance of the default search strategy, resets its
	 * vars and varSelector, and adds this strategy to the end of the strategy execution lists. 
	 * @param vars
	 * @param varSelector
	 * @see AbstractSolver#findSolution() 
	 * @see AbstractSolver#findOptimalSolution(Var)
	 */
	public void addSearchStrategy(Var[] vars, 
			                      VarSelector varSelector) {
		SearchStrategy strategy = newSearchStrategy();
		strategy.setVars(vars);
		strategy.setVarSelector(varSelector);
		addSearchStrategy(strategy);
	}
	
	/**
	 * This method creates a new instance of the default search strategy, resets its
	 * vars and varSelector, and adds this strategy to the end of the strategy execution lists. 
	 * @param vars
	 * @param varSelectorType
	 * @see AbstractSolver#findSolution() 
	 * @see AbstractSolver#findOptimalSolution(Var)
	 */
	public void addSearchStrategy(Var[] vars, 
			                      VarSelectorType varSelectorType) {
		SearchStrategy strategy = newSearchStrategy();
		strategy.setVars(vars);
		strategy.setVarSelectorType(varSelectorType);
		addSearchStrategy(strategy);
	}
	
	/**
	 * This method creates a new instance of the default search strategy, resets its
	 * vars and valueSelector, and adds this strategy to the end of the strategy execution lists. 
	 * @param vars
	 * @param valueSelector
	 * @see AbstractSolver#findSolution() 
	 * @see AbstractSolver#findOptimalSolution(Var)
	 */
	public void addSearchStrategy(Var[] vars, 
			                      ValueSelector valueSelector) {
		SearchStrategy strategy = newSearchStrategy();
		strategy.setVars(vars);
		strategy.setValueSelector(valueSelector);
		addSearchStrategy(strategy);
	}
	
	/**
	 * This method creates a new instance of the default search strategy, resets its
	 * vars and valueSelector, and adds this strategy to the end of the strategy execution lists. 
	 * @param vars
	 * @param valueSelectorType
	 * @see AbstractSolver#findSolution() 
	 * @see AbstractSolver#findOptimalSolution(Var)
	 */
	public void addSearchStrategy(Var[] vars, 
			                      ValueSelectorType valueSelectorType) {
		SearchStrategy strategy = newSearchStrategy();
		strategy.setVars(vars);
		strategy.setValueSelectorType(valueSelectorType);
		addSearchStrategy(strategy);
	}
	
	/**
	 * This method creates a new instance of the default search strategy, resets its
	 * vars, and adds this strategy to the end of the strategy execution lists. 
	 * @param vars
	 * @see AbstractSolver#findSolution() 
	 * @see AbstractSolver#findOptimalSolution(Var)
	 */
	public void addSearchStrategy(Var[] vars) {
		SearchStrategy strategy = newSearchStrategy();
		strategy.setVars(vars);
		addSearchStrategy(strategy);
	}
	
	public void addSearchStrategy(Var var) {
		Var[] vars = new Var[] { var };
		addSearchStrategy(vars);
	}
	
	public void addSearchStrategy(VarReal var) {
		SearchStrategy strategy = getSearchStrategy();
		VarReal[] vars = new VarReal[] { var };
		strategy.setVarReals(vars);
		addSearchStrategy(strategy);
	}
	
//	/**
//	 * This method takes the default search strategy,  resets its
//	 * varSelector, and adds this strategy to the end of the strategy execution lists. 
//	 * @param varSelector
//	 * @see AbstractSolver#findSolution() 
//	 * @see AbstractSolver#findOptimalSolution(Var)
//	 */
//	public void addSearchStrategy(VarSelector varSelector) {
//		SearchStrategy strategy = newSearchStrategy();
//		strategy.setVarSelector(varSelector);
//		addSearchStrategy(strategy);
//	}
	
	/**
	 * This method creates a new instance of the default search strategy, resets its
	 * valueSelector, and adds this strategy to the end of the strategy execution lists. 
	 * @param valueSelector
	 * @see AbstractSolver#findSolution() 
	 * @see AbstractSolver#findOptimalSolution(Var)
	 */
	public void addSearchStrategy(ValueSelector valueSelector) {
		SearchStrategy strategy = newSearchStrategy();
		strategy.setValueSelector(valueSelector);
		addSearchStrategy(strategy);
	}
	
//	/**
//	 * Returns a search strategy of the given type.  
//	 * Each Solver' implementation supports their own implementations of search strategies 
//	 * for the standard types.
//	 * @param strategyType
//	 */
//	abstract public SearchStrategy getSearchStrategy(SearchStrategyType strategyType);
	
	/**
	 * Creates a solution iterator that allows a user to search and navigate
	 * through multiple solutions.
	 * 
	 * @return a solution iterator
	 */
	public SolutionIterator solutionIterator() {
//		AbstractProblem p = (AbstractProblem)getProblem();
//		p.notImplementedException("SolutionIterator");
//		return null;
		return new BasicSolutionIterator(this);
	}

	/**
	 * 
	 * @return an OptimizationStrategy used by the method "findOptimalSolution"
	 */
	public OptimizationStrategy getOptimizationStrategy() {
        return optimizationStrategy;
    }

	/**
     * Sets an OptimizationStrategy used by the method "findOptimalSolution"
     */
    public void setOptimizationStrategy(OptimizationStrategy optimizationStrategy) {
        this.optimizationStrategy = optimizationStrategy;
    }
    
    /**
     * Sets an OptimizationStrategy by its name used by the method "findOptimalSolution"
     */
    public void setOptimizationStrategy(String optimizationStrategyName) {
        switch(optimizationStrategyName.toUpperCase()) {
            case "BASIC": setOptimizationStrategy(OptimizationStrategy.BASIC); break;
            case "DICHOTOMIZE": setOptimizationStrategy(OptimizationStrategy.DICHOTOMIZE); break;
            default: 
                log("Unknown OptimizationStrategy '" + optimizationStrategyName +"'. Use OptimizationStrategy.BASIC");
                setOptimizationStrategy(OptimizationStrategy.BASIC); break;
        }
    }
    
    /**
     * Logs the OptimizationStrategy used by the method "findOptimalSolution"
     */
    public void logOptimizationStrategy() {
        log("OptimizationStrategy: " + optimizationStrategy.name());
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
	abstract public Solution findSolution(ProblemState restoreOrNot); 

	/**
	 * This method is equivalent to findSolution(DO_NOT_RESTORE);
	 * It means that the problem state will always be restored even when a solution is found.
	 * @return a Solution if the search is successful or null. 
	 */
	public Solution findSolution() {
		return findSolution(ProblemState.DO_NOT_RESTORE);
	}
	

//	/**
//	 * This method attempts to find the solution that minimizes/maximizes the objective variable.
//	 * It uses the search strategy defined by the method setSearchStrategy(strategy). 
//	 * The optimization process can be also controlled by:
//	 * <ul>
//	 * <li> OptimizationTolarance that is a difference between optimal solutions - see setOptimizationTolarance()
//	 * <li> MaxNumberOfSolutions that is the total number of considered solutions - may be limited by the method
//	 * setMaxNumberOfSolutions()
//	 * <li> TotalTimeLimit that is the number of seconds allocated for the entire optimization process.
//	 * </ul>
//	 * <br> At the same time the time for one iteration inside
//	 * optimization loop (a search of one solution) can be also limited by the use of the
//	 * special type of search strategy. 
//	 * <br> The problem state after the execution of this method is always restored. All variables
//	 * that were added to the problems (plus the objectiveVar) will have their assigned values 
//	 * saved inside the optimal solution. 
//	 * 
//	 * @param objective Objective.MINIMIZE or Objective.MAXIMIZE
//	 * @param objectiveVar the variable that is being minimized/maximized
//	 * @param optStrategy OptimizationStrategy 
//	 * @return Solution if a solution is found,
//	 *         null if there are no solutions.
//	 */
//	public Solution findOptimalSolution(Objective objective, Var objectiveVar, OptimizationStrategy optStrategy) {
//		if (optStrategy.equals(OptimizationStrategy.DICHOTOMIZE))
//			return findOptimalSolutionDichotomize(objective, objectiveVar);	
//		if (optStrategy.equals(OptimizationStrategy.BASIC))
//			return findOptimalSolutionBasic(objective, objectiveVar);
//		// NATIVE
//		return findOptimalSolution(objective, objectiveVar);
//	}
//	
//	public Solution findOptimalSolution(Objective objective, VarReal objectiveVar, OptimizationStrategy optStrategy) {
//		throw new RuntimeException("There is no implementation for findOptimalSolution(Objective objective, VarReal objectiveVar, OptimizationStrategy optStrategy)");
//	}
	
	/**
	 * This method is usually overridden by an implementation. If not, this implementation uses
	 * OptimizationStrategy.BASIC.
	 * @param objectiveVar
	 * @return Solution if a solution is found,
	 *         null if there are no solutions.
	 */
	public Solution findOptimalSolution(Objective objective, Var objectiveVar) {
		return findOptimalSolutionBasic(objective, objectiveVar);
	}
	
	public Solution findOptimalSolution(Objective objective, VarReal objectiveVar) {
	    throw new RuntimeException("There is no implementation for findOptimalSolutionBasic(Objective objective, VarReal objectiveVar)");
    }
	
	/**
	 * This method is equivalent to findOptimalSolution(Objective.MINIMIZE,objectiveVar)
	 * @param objectiveVar
	 * @return Solution if a solution is found,
	 *         null if there are no solutions.
	 */
	public Solution findOptimalSolution(Var objectiveVar) {
		return findOptimalSolution(Objective.MINIMIZE,objectiveVar);
	}
	
	public Solution findOptimalSolution(VarReal objectiveVar) {
		return findOptimalSolution(Objective.MINIMIZE,objectiveVar);
	}
	
	/**
     * This method attempts to find the solution that minimizes/maximizes the objective variable.
     * It uses the search strategy defined by the method setSearchStrategy(strategy). 
     * The optimization process can be also controlled by:
     * <ul>
     * <li> OptimizationTolerance that is a difference between optimal solutions - see setOptimizationTolerance()
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
	public Solution findOptimalSolutionBasic(Objective objective, Var objectiveVar) {
	    addObjective(objectiveVar);
		long startTime = System.currentTimeMillis();
		if (objectiveVar.getName().isEmpty())
			objectiveVar.setName("Objective"); 
		if (getProblem().getVar(objectiveVar.getName()) == null) {
			getProblem().add(objectiveVar);
		}
		Var obj = objectiveVar;
		if (objective.equals(Objective.MAXIMIZE)) {
			obj = objectiveVar.multiply(-1);
			obj.setName("-"+objectiveVar.getName());
			getProblem().add(obj);
		}
		addObjective(obj);
		int bestValue = Integer.MAX_VALUE;
		Solution solution = null;
		int n = 0;
		SolutionIterator iter = solutionIterator();
		while(iter.hasNext()) {
			solution = iter.next();
			int newValue = solution.getValue(obj.getName()); // RS
            if (isTraceSolutions()) {
                log("Found a solution #" + solution.getSolutionNumber() 
                        + " with objective " + newValue 
                        + ". " + Calendar.getInstance().getTime() );
            }
			n++;
			if (getMaxNumberOfSolutions() > 0 && 
					n == getMaxNumberOfSolutions()) {
			    log("The search is interrupted: MaxNumberOfSolutions " + n + " has been reached.");
			    //backtrack();
				break;
			}
			if (getTimeLimitGlobal() > 0) {
				if (System.currentTimeMillis() - startTime > getTimeLimitGlobal()) {
					log("Global time limit " + getTimeLimitGlobal() + " mills has been exceeded.");
					break;		
				}
			}
			try {
				if (isTraceExecution())
					solution.log();
				if (bestValue > newValue)
					bestValue = newValue;
				getProblem().post(obj,"<",newValue); // may fail
			} catch (Exception e) {
				//log("Optimal solution is found. Best objective: "+bestValue);
				break;
			}
		}
		if (solution != null)
			log("Optimal solution is found. Objective: "
					+solution.getValue(objectiveVar.getName()));
		return solution;
	}
	
	/**
	 * This method is equivalent to findOptimalSolutionCommon(Objective.MINIMIZE,objectiveVar)
	 * @param objectiveVar
	 * @return Solution if a solution is found,
	 *         null if there are no solutions.
	 */
	public Solution findOptimalSolutionDichotomize(Var objectiveVar) {
		return findOptimalSolutionDichotomize(Objective.MINIMIZE,objectiveVar);
	}
	
	/**
	 * The actual minimization algorithm executes a dichotomized search. During
	 * the search it modifies an interval [objectiveMin; objectiveMax]. First it
	 * is trying to find a solution in the [objectiveMin; objectiveMid]. If it
	 * fails, it is looking at [objectiveMid+1; objectiveMax]. During this
	 * process it switches the search target: one time in looks at in the upper
	 * half of the selected interval, another time - to the lower half.
	 * Successful search stops when (objectiveMax - objectiveMin) is less or equal to tolerance.
	 */
	public Solution findOptimalSolutionDichotomize(Objective objective, Var objectiveVar) {
		
		log("The method 'findOptimalSolutionDichotomize' should be implemented by a solver implementation.");
		log("The default method 'findOptimalSolutionBasic' has been used.");
		return findOptimalSolutionBasic(objective, objectiveVar);
	}
	
	public int getOptimizationTolerance() {
		return tolerance;
	}

	public void setOptimizationTolerance(int tolerance) {
		this.tolerance = tolerance;
	}
	
	protected void addObjective(Var objectiveVar) {
		for (int i = 0; i < searchStrategies.size(); i++) {
			SearchStrategy strategy = searchStrategies.elementAt(i);
			Var[] vars = strategy.getVars();
			for (int j = 0; j < vars.length; j++) {
				if (vars[j] == objectiveVar)
					return;
			}			
		}
		// add to the first strategy
//		SearchStrategy strategy = getSearchStrategy();
//		Var[] vars = strategy.getVars();
//		Var[] newVars = new Var[vars.length+1];
//		for (int i = 0; i < vars.length; i++) {
//			newVars[i] = vars[i];
//		}
//		newVars[vars.length] = objectiveVar;
//		strategy.setVars(newVars);
		addSearchStrategy(objectiveVar);
	}
	
	protected void addObjective(VarReal objectiveVar) {
		for (int i = 0; i < searchStrategies.size(); i++) {
			SearchStrategy strategy = searchStrategies.elementAt(i);
			VarReal[] vars = strategy.getVarReals();
			for (int j = 0; j < vars.length; j++) {
				if (vars[j] == objectiveVar)
					return;
			}			
		}
		// add to the first strategy
//		SearchStrategy strategy = getSearchStrategy();
//		VarReal[] vars = strategy.getVarReals();
//		VarReal[] newVars = new VarReal[vars.length+1];
//		for (int i = 0; i < vars.length; i++) {
//			newVars[i] = vars[i];
//		}
//		newVars[vars.length] = objectiveVar;
//		strategy.setVarReals(newVars);
		addSearchStrategy(objectiveVar);
	}
	
	/**
	 * This method attempts to find all solutions for the Problem. 
	 * It uses the default search strategy or the strategy defined by the latest 
	 * method setSearchStrategy(). It returns an array of found solutions 
	 * or null if there are no solutions.  A user has to be careful not to 
	 * overload the available memory because the number of found solutions could be huge.  
	 * The process of finding all solutions can be also controlled by:
	 * <ul>
	 * <li>MaxNumberOfSolutions that is the total number of considered solutions
	 * that may be limited by the method setMaxNumberOfSolutions();
	 * <li>TotalTimeLimit that is the number of seconds allocated for the entire optimization process.
	 * </ul>
	 * The common implementation is based on the SolutionIterator.
	 * @return Solution[]
	 */
	public Solution[] findAllSolutions() {
		SolutionIterator iter = solutionIterator();
		long startTime = System.currentTimeMillis();
		ArrayList<Solution> solutions = new ArrayList<Solution>();
		int n = 0;
		while(iter.hasNext()) {
			Solution solution = iter.next();
			solutions.add(solution);
			if (getTimeLimit() > 0) {
				if (System.currentTimeMillis() - startTime > getTimeLimit()) {
					log("Reached TimeLimit=" + getTimeLimit() + " mills");
					break;				
				}
			}
			n++;
			if (getMaxNumberOfSolutions() > 0) {
				if (n == getMaxNumberOfSolutions()) {
					log("Found MaxNumberOfSolutions=" + getMaxNumberOfSolutions());
					break;				
				}
			}
		}
		Solution[] array = new Solution[solutions.size()];
		for (int i = 0; i < array.length; i++) {
			array[i] = solutions.get(i);
		}
		return array;		
	}
	

	
	/**
	 * This method tries to resolve the problem by applying the Solution
	 * "solution" to the problem, and returns true if "solution" satisfies all
	 * of the posted constraints, false otherwise.
	 * 
	 * @param solution
	 *            the Solution object being tested on the problem.
	 * @return true if the Solution satisfies all posted constraints, false
	 *         otherwise.
	 */
	abstract public boolean applySolution(Solution solution); 
	
	/**
	 * This method tries to resolve the problem by applying the existing
	 * Solution at the "solutionNumber"-th index of the array of solutions
	 * (Accessible by getSolutions();). Equivalent to calling
	 * applySolution(getSolution(solutionNumber)); Returns true if the Solution
	 * satisfies all of the posted constraints, false otherwise.
	 * 
	 * @param solutionNumber
	 *            the Solution object being tested on the problem.
	 * @return true if the solution with "solutionNumber" satisfies all posted
	 *         constraints, false otherwise.
	 */
	public boolean applySolution(int solutionNumber) {
		Solution solution = getSolutions()[solutionNumber];
		return applySolution(solution);
	}


	
	/**
	 * 
	 * @return true if the search strategies are set to be traced
	 */
	public boolean isTraceExecution() {
		return traceExecution;
	}
	
	/**
	 * 
	 * @return true if intermediate solutions are set to be traced during optimization
	 */
	public boolean isTraceSolutions() {
		return traceSolutions;
	}
	
	/**
	 * If flag is true, all failures will be traced (logged)
	 * 
	 * @param flag
	 */
	public void traceFailures(boolean flag) {
		log("Solver method traceFailures() is not implemented");
	}

	public long getSolverStartTime() {
		return solverStartTime;
	}

	public void setSolverStartTime(long solverStartTime) {
		this.solverStartTime = solverStartTime;
	}
	
	/**
	 * This method forces each search strategy to
	 * log itself each time when solver calls its method execute
	 * @param flag boolean
	 */
	public void traceExecution(boolean flag) {
		traceExecution = flag;
	}
	
	/**
	 * This method forces the execution to
	 * log intermediate solutions during optimization search
	 * @param trueOrFalse boolean
	 */
	public void traceSolutions(boolean trueOrFalse) {
		traceSolutions = trueOrFalse;
	}

	/**
	 * This method logs  execution statistics such as a number of choice points,
	 * number of failures, used memory, etc. 
	 * This method is expected to be 
	 * specific for different reference implementations
	 */
	public void logStats() {
		log("*** Execution Profile ***");
		long executionTime = System.currentTimeMillis() - solverStartTime;
		log("Execution time: " + executionTime + " msec");

//		long occupied_memory = Runtime.getRuntime().totalMemory()
//				- Runtime.getRuntime().freeMemory();
//		log("Occupied memory: " + occupied_memory);

	}
	
	public void log(String text) {
		problem.log(text);
	}
	
	/**
	 * Adds an application-specific action that will be executed during backtracking.
	 * @param action the action to be executed during backtracking.
	 */
	public void addReversibleAction(ReversibleAction action) {
		throw new RuntimeException("This RI does not implement Revesible Actions");
	}
	
//	/**
//	 * This method forces a solver to "backtrack". 
//	 * It is used to emulate a failure, e.g. to produce all solutions.
//	 * 
//	 * @throws Exception 
//	 */
//	abstract public void backtrack() throws Exception;


}
