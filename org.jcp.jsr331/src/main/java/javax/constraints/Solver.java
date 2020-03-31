//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  331
// 
// Specification
// 
//=============================================
package javax.constraints;


/**
 * The interface Solver is used for Problem Resolution and utilizes concepts 
 * of Search Strategies and Solutions.  
 * <p>The Solver provides a standardized way to implement the following functions:
 * <ul>
 * <li>Find a solution
 * <li>Find an optimal solution that minimizes/maximizes an objective variable
 * <li>Iterate through multiple solutions
 * <li>Limit search time and a number of solutions.
 * </ul>
 * <br> These functions use an ordered list of search strategies called "execution list".
 * Each implementation should provide at least one default search strategy that is available
 * by the Solver's method "getSearchStrategy". A user may get this strategy and apply
 * its methods "setVars", "setVarSelector", and "setValueSelector" to customize it.
 * This strategy is the first one to be executed. A user may also add another strategy using
 * the Solver's method "newSearchStrategy". It returns a new instance of the default
 * search strategy. A user may set its attributes and use the method "addStrategy" to add
 * a new customized strategy to the end of the execution list. For example, if a user want first to
 * instantiate all variables from the array "types" and then all variables from the array 
 * "counts", it can be done as follow:
 * <pre>
 * SearchStrategy s1 = solver.getSearchStrategy();
 * s1.setVars(types);
 * SearchStrategy s2 = solver.newSearchStrategy();
 * s2.setVars(counts);
 * s2.setVarSelectorType(VarSelectorType.RANDOM);
 * solver.addStrategy(s2);
 * </pre> 
 * 
 * 
 * @see Solution
 * @see SearchStrategy
 * @see VarSelector
 * @see ValueSelector
 */

public interface Solver {

	/**
	 * Returns the Problem for which this solver was created
	 * @return the Problem for which this solver was created
	 */
	public Problem getProblem();
	
	/**
	 * Set the Problem with which this solver should work
	 */
	public void setProblem(Problem problem);
	
	/**
	 * Creates a copy of the problem in its current state
	 */
	public void saveProblem();
	
	/**
	 * Restores a previously saved problem
	 */
	public void restoreProblem();

	/**
	 * Returns the number of the last solution found, or 0 if no solutions
	 *         have been found yet (or the search for a solution was not yet launched).
	 * @return the number of the last solution found, or 0 if no solution have
	 *         been found yet (or the search for a solution was not yet launched).
	 */
	public int getNumberOfSolutions();



	/**
	 * Returns the maximal number of solutions any search can look for. A value
	 *         of -1 indicates infinity, i.e. that there is no limitation on the maximal number of
	 *         solutions. The default value is -1.
	 * @return the maximal number of solutions any search can look for. A value
	 *         of -1 indicates infinity, i.e. that there is no limitation on the maximal number of
	 *         solutions. The default value is -1.
	 */
	public int getMaxNumberOfSolutions();

	/**
	 * Sets the limit on the maximal number of solutions any search can look for. A value
	 *      of -1 indicates infinity, i.e. that there is no limitation on the maximal number of
	 *      solutions. The default value is -1.
	 * @param maxNumberOfSolutions the new limit for the maximal number of solutions any search can
	 *                             look for.
	 */
	public void setMaxNumberOfSolutions(int maxNumberOfSolutions);

	/**
	 * Returns the duration, in milliseconds, of the imposed time limit on search of one solution.
	 * @return the duration, in milliseconds, of the imposed time limit on search of one solution.
	 */
	public int getTimeLimit();

	/**
	 * Sets the duration, in milliseconds, of the time limit on search of one solution.
	 * @param mills the new time limit in milliseconds
	 */
	public void setTimeLimit(int mills);
	
	/**
	 * Returns the duration, in milliseconds, of the imposed time limit 
	 * on global search of all solutions or an optimal solution.
	 * @return the duration, in milliseconds, of the imposed time limit
	 * on global search of all solutions or an optimal solution.
	 */
	public int getTimeLimitGlobal();

	/**
	 * Sets the duration, in milliseconds, of the time limit 
	 * on global search of all solutions or an optimal solution.
	 * If  a time limit (for one solution) is not set, it also calls setTimeLimit(mills)
	 * @param mills the new global time limit in milliseconds
	 */
	public void setTimeLimitGlobal(int mills);

//	/**
//	 * Validates in the difference between the current time and the
//	 * getTimeLimitStart() time is more than getTimeLimit()
//	 * @return true if yes, false if no.
//	 */
//	public boolean checkTimeLimit();

//	/**
//	 * Returns true if the time limit expired during a search, or false if not.
//	 *
//	 * In the case of exceeding the time limit, the Solution returned by search is null.
//	 * The boolean returned by this method can be used to distinguish between "no solution existed"
//	 * and "time limit expired", when the Solution returned is null.
//	 * @return true if the time limit expired during a search, or false if not.
//	 */
//	public boolean isTimeLimitExceeded();

//	/**
//	 * Sets whether or not the time limit has expired.
//	 * @param exceeded true if the time limit exceeded, false otherwise.
//	 */
//	public void setTimeLimitExceeded(boolean exceeded);

	/**
	 * Returns the time, in milliseconds, at which the time limit began counting.
	 * @return the time, in milliseconds, at which the time limit began counting.
	 */
	public long getTimeLimitStart();

	/**
	 * Sets the start time as the system current time, for counting milliseconds to the time limit.
	 */
	public void setTimeLimitStart();

//	/**
//	 * This method forces a solver to "backtrack". 
//	 * It is used to emulate a failure, e.g. to produce all solutions.
//	 * 
//	 * @throws Exception 
//	 */
//	public void backtrack() throws Exception;
	
//	/**
//	 * Adds a "reversible action" that will be executed during backtracking.
//	 * Such actions are used to represent reversible actions that can be added 
//	 * to the search and executed during backtracking. For example, an 
//	 * application may draw a square during the search and erase it during backtracking.
//	 * Reversible action also allow a user to animate the search.
//	 * @param action to be executed during backtracking.
//	 */
//	public void addReversibleAction(ReversibleAction action);
	
//	/**
//	 * This is an implementation specific method that execute
//	 * @param strategy
//	 * @param restoreOrNot
//	 * @return
//	 */
//	public Solution execute(SearchStrategy strategy, ProblemState restoreOrNot);

	/**
	 * Define a search strategy that will be used by the solver to solve the problem. 
	 * Each Solver' implementation should set a default search strategy in the
	 * its Solver constructor.
	 * @param strategy
	 */
	public void setSearchStrategy(SearchStrategy strategy);
	
	/**
	 * Define a searchStrategy that will be used by find solutions methods.
	 * This methods takes the default search strategy, and resets its
	 * vars, varSelector, and valueSelector. 
	 * @param vars
	 * @param varSelector
	 * @param valueSelector
	 */
	public void setSearchStrategy(Var[] vars, 
			                      VarSelector varSelector, 
			                      ValueSelector valueSelector);
	
	/**
	 * Define a searchStrategy that will be used by find solutions methods.
	 * This methods takes the default search strategy, and resets its
	 * vars and varSelector. 
	 * @param vars
	 * @param varSelector
	 */
	public void setSearchStrategy(Var[] vars, VarSelector varSelector);
	
	/**
	 * Define a searchStrategy that will be used by find solutions methods.
	 * This methods takes the default search strategy, and resets its
	 * vars. 
	 * @param vars
	 */
	public void setSearchStrategy(Var[] vars);
	
	/**
	 * Define a searchStrategy that will be used by find solutions methods.
	 * This methods takes the default search strategy, and resets its
	 * vars and valueSelector. 
	 * @param vars
	 * @param valueSelector
	 */
	public void setSearchStrategy(Var[] vars, ValueSelector valueSelector);
	
	/**
	 * This methods returns a new default search strategy 
	 * @return a new default search strategy
	 */
	abstract public SearchStrategy newSearchStrategy();
	
	/**
	 * This methods adds this strategy 
	 * to the end of the strategy execution lists. 
	 * @param strategy
	 */
	public void addSearchStrategy(SearchStrategy strategy);
	
	/**
	 * This methods takes the default search strategy, resets its
	 * vars, varSelector, and valueSelector, and then adds this strategy 
	 * to the end of the strategy execution lists. 
	 * @param vars
	 * @param varSelector
	 * @param valueSelector
	 */
	public void addSearchStrategy(Var[] vars, 
			                      VarSelector varSelector, 
			                      ValueSelector valueSelector);
	
	/**
	 * This methods takes the default search strategy, resets its
	 * vars, varSelector, and valueSelector, and then adds this strategy 
	 * to the end of the strategy execution lists. 
	 * @param vars
	 * @param varSelectorType
	 * @param valueSelectorType
	 */
	public void addSearchStrategy(Var[] vars, 
			                      VarSelectorType varSelectorType, 
			                      ValueSelectorType valueSelectorType);
	
	/**
	 * This methods takes the default search strategy, resets its
	 * vars and varSelector, and adds this strategy to the end of the strategy execution lists. 
	 * @param vars
	 * @param varSelector
	 */
	public void addSearchStrategy(Var[] vars, VarSelector varSelector);
	
	/**
	 * This methods takes the default search strategy, resets its
	 * vars and varSelector, and adds this strategy to the end of the strategy execution lists. 
	 * @param vars
	 * @param varSelectorType
	 */
	public void addSearchStrategy(Var[] vars, VarSelectorType varSelectorType);
	
	
	/**
	 * This methods takes the default search strategy, resets its
	 * vars and valueSelector, and adds this strategy to the end of the strategy execution lists. 
	 * @param vars
	 * @param valueSelector
	 */
	public void addSearchStrategy(Var[] vars, ValueSelector valueSelector);
	
	/**
	 * This methods takes the default search strategy, resets its
	 * vars and valueSelector, and adds this strategy to the end of the strategy execution lists. 
	 * @param vars
	 * @param valueSelectorType
	 */
	public void addSearchStrategy(Var[] vars, ValueSelectorType valueSelectorType);
	
	/**
	 * This methods takes the default search strategy, resets its
	 * vars, and adds this strategy to the end of the strategy execution lists. 
	 * @param vars
	 */
	public void addSearchStrategy(Var[] vars);
	
	/**
	 * This methods is equivalent to addSearchStrategy(vars)
	 * where the array "vars" contains only one variable "var" 
	 * @param var
	 */
	public void addSearchStrategy(Var var);
	
//	/**
//	 * Returns a search strategy of the given type.  
//	 * Each Solver' implementation supports its own implementations of search strategies 
//	 * for the standard types. If a strategy of the requested type is not supported
//	 * by a particular implementation, this method return null.
//	 * @param strategyType that is define by the enum "SearchStrategyType"
//	 * @return strategy of the type "strategyType" or null if this type is not supported by 
//	 * the implementation.
//	 */
//	public SearchStrategy getSearchStrategy(SearchStrategyType strategyType);

	/**
	 * Returns a SearchStrategy that is currently used by the solver
	 * @return a SearchStrategy that is currently used by the solver to solve the problem
	 */
	public SearchStrategy getSearchStrategy();
	
	/**
	 * Returns a search strategy that logs all variables 
	 * @return a a search strategy that logs all variables
	 */
	public SearchStrategy getStrategyLogVariables();
	
	/**
	 * This methods adds the strategy getStrategyLogVariables()
	 * to the end of the strategy execution lists. 
	 */
	public void addStrategyLogVariables();
	
	/**
	 * This methods adds the strategy that logs the "text"
	 * to the end of the strategy execution lists. 
	 */
	public void addLogStrategy(String text);
	
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
	public Solution findSolution(ProblemState restoreOrNot);
	
	/**
	 * This method is equivalent to findSolution(DO_NOT_RESTORE);
	 * It means that the problem state will always be restored even when a solution is found.
	 * @return a Solution if the search is successful or null. 
	 */
	public Solution findSolution();

	
	/**
	 * This method attempts to find the solution that minimizes/maximizes the objective variable.
	 * It uses the search strategy defined by the method setSearchStrategy(strategy). 
	 * The optimization process can be also controlled by:
	 * <ul>
	 * <li> OptimizationTolerance that is a difference between optimal solutions - see setOptimizationTolarance()
	 * <li> MaxNumberOfSolutions that is the total number of considered solutions - may be limited by the method
	 * setMaxNumberOfSolutions()
	 * <li> TimeLimitGlobal that is the number of seconds allocated for the entire optimization process.
	 * </ul>
	 * <br> At the same time the time for one iteration inside
	 * optimization loop (a search of one solution) can be also limited by the Solver's attribute TimeLimit
	 * or by the use of a special search strategy specified by a particular implementation. 
	 * <br> The problem state after the execution of this method is always restored. All variables
	 * that were added to the problems (plus the objectiveVar) will have their assigned values 
	 * saved inside the optimal solution. 
	 * 
	 * @param objective Objective.MINIMIZE or Objective.MAXIMIZE
	 * @param objectiveVar the variable that is being minimized/maximized
	 * @param optStrategy OptimizationStrategy (BASIC, DICHOTOMIZE, or NATIVE)
	 * @return Solution if a solution is found,
	 *         null if there are no solutions.
	 */
	public Solution findOptimalSolution(Objective objective, Var objectiveVar, OptimizationStrategy optStrategy);
	
	public Solution findOptimalSolution(Objective objective, VarReal objectiveVar, OptimizationStrategy optStrategy);
	
	
	/**
	 * This method is equivalent to 
	 * findOptimalSolution(Objective.MINIMIZE,objectiveVar,OptimizationStrategy.NATIVE)
	 * It is usually overridden by an implementation
	 * @param objectiveVar
	 * @return Solution if a solution is found,
	 *         null if there are no solutions.
	 */
	public Solution findOptimalSolution(Objective objective, Var objectiveVar);
	
	/**
	 * This method is equivalent to 
	 * findOptimalSolution(Objective.MINIMIZE,objectiveVar,OptimizationStrategy.NATIVE)
	 * It is usually overridden by an implementation
	 * @param objectiveVar
	 * @return Solution if a solution is found,
	 *         null if there are no solutions.
	 */
	public Solution findOptimalSolution(Objective objective, VarReal objectiveVar);
	
	/**
	 * This method is equivalent to findOptimalSolution(Objective.MINIMIZE,objectiveVar)
	 * @param objectiveVar
	 * @return Solution if a solution is found,
	 *         null if there are no solutions.
	 */
	public Solution findOptimalSolution(Var objectiveVar);
	
	/**
	 * This method is equivalent to findOptimalSolution(Objective.MINIMIZE,objectiveVar)
	 * @param objectiveVar
	 * @return Solution if a solution is found,
	 *         null if there are no solutions.
	 */
	public Solution findOptimalSolution(VarReal objectiveVar);
	
	/**
	 * Specifies a tolerance for the method findOptimalSolution(). 
	 * If the difference between newly found solution and a previous one is less
	 * or equal "tolerance" then the last solution is considered to be the optimal one.
	 * By default, the optimization tolerance is 0.
	 * @param tolerance
	 */
	public void setOptimizationTolerance(int tolerance);
	
	/**
	 * Returns the current tolerance for the method findOptimalSolution()
	 * @return the current tolerance for the method findOptimalSolution()
	 */
	public int getOptimizationTolerance();
	
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
	 * The problem state after the execution of this method is always restored.
	 * @return Solution[]
	 */
	public Solution[] findAllSolutions();
	
	/**
	 * Removes all solutions
	 */
//	public void clearSolutions();
	
	/**
	 * Returns an array of Solutions
	 *
	 * @return an array of all found Solutions
	 * @see Solver#findAllSolutions()
	 */
//	public Solution[] getSolutions();

	/**
	 * Adds a solution to the current array of solutions
	 */
//	public void addSolution(Solution solution);
	
	/**
	 * Returns the i-th Solution found by the latest search
	 * @return the i-th Solution found by the latest search
	 * @see Solver#findAllSolutions()
	 */
	public Solution getSolution(int i);

	/**
	 * Returns the first (or only) solution found by the latest search
	 * @return the first (or only) solution found by the latest search
	 */
//	public Solution getSolution();

	/**
	 * This method tries to resolve the problem by applying the Solution "solution"
	 * to the problem, and returns true if "solution" satisfies all of the posted
	 * constraints, false otherwise.
	 *
	 * @param solution the Solution object being tested on the problem.
	 * @return true if the Solution satisfies all posted constraints, false otherwise.
	 */
//	public boolean applySolution(Solution solution);

//	/**
//	 * This method tries to resolve the problem by applying the existing Solution
//	 * at the "solutionNumber"-th index of the array of solutions (accessable by
//	 * getSolutions();). Equivalent to calling applySolution(getSolution(solutionNumber));
//	 * Returns true if the Solution satisfies all of the posted
//	 * constraints, false otherwise.
//	 *
//	 * @param solutionNumber the Solution object being tested on the problem.
//	 * @return true if the "solutionNumber"-th Solution satisfies all posted constraints, false otherwise.
//	 * @see Solver#getSolutions()
//	 * @see Solver#getSolution(int)
//	 * @see Solver#applySolution(Solution)
//	 */
//	public boolean applySolution(int solutionNumber);

	/**
	 * Creates a solution iterator that allows a user to find and navigate
	 * through solutions using the default search strategy.
	 * @return a solution iterator
	 */
	public SolutionIterator solutionIterator();

	/**
	 * This method logs  execution statistics such as a number of choice points,
	 * number of failures, used memory, etc. 
	 * This method is expected to be 
	 * specific for different reference implementations
	 */
	public void logStats();

	/**
	 * This method forces to trace (to log) all modification events for
	 * the variable "var". 
	 * @param var
	 */
	public void trace(Var var);

//	/**
//	 * This method forces to trace (to log) the modification "event" for
//	 * the variable "var". 
//	 * @param var
//	 */
//	public void trace(Var var, PropagationEvent event);

	/**
	 * This method forces to trace (to log) all modification events for
	 * all variables "vars"
	 * @param vars
	 */
	public void trace(Var[] vars);

//	/**
//	 * This method forces to trace (to log) the modification "event"
//	 * for all variables "var". 
//	 * @param vars
//	 * @param event
//	 */
//	public void trace(Var[] vars, PropagationEvent event);

	/**
	 * If flag is true, all failures will be traced (logged)
	 * @param flag
	 */
	public void traceFailures(boolean flag);

	/**
	 * This method forces to trace (to log) all modification events for
	 * the variable "setVar". 
	 * @param setVar
	 */
	public void trace(VarSet setVar);

//	/**
//	 * This method forces to trace (to log) the modification "event" for
//	 * the variable "setVar". All modification events are described by {@link PropagationEvent}
//	 * @param setVar
//	 * @param propagationEvent
//	 */
//	public void trace(VarSet setVar, PropagationEvent propagationEvent);
	
	/**
	 * This method forces the execution to
	 * log important execution points 
	 * @param trueOrFalse boolean
	 */
	public void traceExecution(boolean trueOrFalse);
	
	/**
	 * This method forces the execution to
	 * log intermediate solutions during optimization search
	 * @param trueOrFalse boolean
	 */
	public void traceSolutions(boolean trueOrFalse);
	
}
