package javax.constraints.impl.search.goal;

import java.util.Vector;

import javax.constraints.Constraint;
import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.ProblemState;
import javax.constraints.SearchStrategy;
import javax.constraints.SearchStrategy.SearchStrategyType;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarSet;
import javax.constraints.extra.PropagationEvent;
import javax.constraints.impl.BasicVarSet;
import javax.constraints.impl.search.AbstractSolver;
import javax.constraints.impl.search.goal.Dichotomize;

abstract public class SolverWithGoals extends AbstractSolver {
	
	public SolverWithGoals(Problem problem) {
		super(problem);
//		if (this.getProblem().getVars() != null ) {
//			setSearchStrategy(getSearchStrategy(SearchStrategyType.DEFAULT));
			//setSearchStrategy(newSearchStrategy());
//		}
//		else {
//			problem.log("There are no decision variables defined in the problem");
//		}
	}
	
	/**
	 * This methods returns a new default search strategy 
	 * @return a new default search strategy
	 */
	public SearchStrategy newSearchStrategy() {
	    return new GoalAssignValues(this);
//		if (getTimeLimit() <= 0)
//			return new GoalAssignValues(this);
//		else
//			return new GoalAssignValuesTimeLimit(this);
	}
	
//	/**
//	 * Returns a search strategy of the given type.  
//	 * Each Solver' implementation supports their own implementations of search strategies 
//	 * for the standard types.
//	 * @param strategyType
//	 */
//	public SearchStrategy getSearchStrategy(SearchStrategyType strategyType) {
//		switch (strategyType) {
//		case DEFAULT:
//			return new GoalAssignValues(this);
//
//		default:
//			return new GoalAssignValues(this);
//		}		
//	}
	
//	public Goal getGoal() {
//		SearchStrategyGoal searchStrategy = (SearchStrategyGoal)getSearchStrategy();
//		return searchStrategy.getGoal();
//	}
	
//	public void setSearchStrategy(Goal goal) {
//		SearchStrategy strategy = new SearchStrategyGoal(goal);
//		setSearchStrategy(strategy);
//	}
	
//	/**
//	 * This methods adds this strategy 
//	 * to the end of the strategy execution lists. 
//	 * @param strategy
//	 * @see {@link AbstractSolver#findSolution()} {@link AbstractSolver#findOptimalSolution(Var)}
//	 */
//	public void addSearchStrategy(SearchStrategy strategy) {
//		searchStrategies.add(strategy);
//		Goal prevGoal = (Goal)getSearchStrategy();
//		Goal goal = (Goal) strategy; 
//		setSearchStrategy(prevGoal.and(goal));
//	}

	/**
	 * To create a new goal on the javax.constraints level without necessity to go down to
	 * the underlying CP solver, one needs to use a goal that will serve as an
	 * implementation object. "goalThis" serves exactly these purposes and
	 * actually executes the method execute() from the Goal passed as a
	 * parameter.
	 *
	 * @param goal
	 */
	abstract public Object goalThis(Goal goal);
	
	abstract public boolean execute(Goal goal, ProblemState restoreOrNot);
	
	public Goal goalVarEqValue(Var var,int value) {
		Constraint assign = getProblem().linear(var, "=", value);
		Goal goalAssignValue = new GoalConstraint(assign);
		goalAssignValue.setName("Assign " + value + " to " + var);
		return goalAssignValue;
	}
	
	public Goal goalVarNeqValue(Var var,int value) {
		Constraint remove = getProblem().linear(var, "!=", value);
		Goal goalRemoveValue = new GoalConstraint(remove);
		goalRemoveValue.setName("Remove " + value + " from " + var);
		return goalRemoveValue;
	}
	
	public Goal goalVarLeValue(Var var,int value) {
		Constraint c = getProblem().linear(var, "<=", value);
		c.setName("" + var + " <= "+ value);
		Goal goalLeValue = new GoalConstraint(c);
		
		return goalLeValue;
	}
	
	public Goal goalVarGeValue(Var var,int value) {
		Constraint c = getProblem().linear(var, ">=", value);
		c.setName("" + var + " >= "+ value);
		Goal goalGeValue = new GoalConstraint(c);
		return goalGeValue;
	}
	
	/**
	 * Returns an "AND" Goal. The Goal "AND" succeeds if both of the Goals
	 * "g1" and "g2" succeed. The Goal "AND" fails if at least one of the Goals
	 * "g1" or "g2" fail. It is implementation specific.
	 * 
	 * @param g1
	 *            the first Goal which is part of the new "AND" Goal.
	 * @param g2
	 *            the other Goal which is part of the new "AND" Goal.
	 * @return a Goal "AND" between the Goals "g1" and "g2".
	 */
	abstract public Goal and(Goal g1, Goal g2);
	
	/**
	 * Returns an "OR" Goal. The goal "OR" succeeds if at least one of the
	 * Goals "g1" or "g2" succeeds. The Goal "OR" fails if both Goals "g1" and
	 * "g2" fail. It is implementation specific.
	 * 
	 * @param g1
	 *            the first Goal which is part of the new "OR" Goal.
	 * @param g2
	 *            the second Goal which is part of the new "OR" Goal.
	 * @return a Goal "OR" between the Goals "g1" and "g2".
	 */
	abstract public Goal or(Goal g1, Goal g2);
	
	/**
	 * This methods is equivalent to execute(goal,ProblemState.DO_NOT_RESTORE);
	 * @param goal
	 * @return true if the goal execution succeeds or false if it fails.
	 */
	public boolean execute(Goal goal) {
		return execute(goal, ProblemState.DO_NOT_RESTORE);
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
		clearSolutions();
		Solution solution = null;
		
//		Goal searchStrategy = (Goal)getSearchStrategies().elementAt(0);
//		for (int i = 1; i < getSearchStrategies().size(); i++) {
//			Goal nextGoal = (Goal)getSearchStrategies().elementAt(i);
//			searchStrategy = searchStrategy.and(nextGoal);
//		}
		Goal searchGoal = combineSearchStrategies();
		Goal goalSearchAndSave = searchGoal.and(new GoalAddSolution(this));
		
		if (execute(goalSearchAndSave,restoreOrNot))
			solution = getSolution();
	
		if(solution != null && !solution.isBound()) {
			if(searchGoal instanceof GoalAssignValuesTimeLimit) {
				setTimeLimitExceeded(true);
				getProblem().log("Solver exceeded Time Limit " + getTimeLimit() + " milliseconds");
				solution = null;
			}
		}
		return solution;
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
		clearSolutions();
		Goal searchGoal = combineSearchStrategies();
		Goal addGoal = new GoalAddSolution(this,-1);
		//Goal logGoal = new GoalLogSolution(this);
		Goal maxGoal = new GoalCheckMaxNumberOfSolutions(this);
		Goal backtrackGoal = new GoalBacktrack(this);
		//Goal goal1 = searchStrategy.and(addGoal).and(logGoal);
		Goal goal1 = searchGoal.and(addGoal);
		Goal goal2 = maxGoal.or(backtrackGoal);
		
		Goal goal = goal1.and(goal2);
		//execute(goal,ProblemState.DO_NOT_RESTORE); 
		execute(goal,ProblemState.RESTORE); 
		return getSolutions();
	}
	
	public Goal makeGoal(SearchStrategy strategy) {
		Goal goal;
		if (strategy.getType().equals(SearchStrategyType.CUSTOM))
			goal = new StrategyAsGoal(this, strategy);
		else
			goal = (Goal) strategy;
		return goal;
	}
	
	public Goal combineSearchStrategies() {
		Vector<SearchStrategy> searchStrategies = getSearchStrategies();
		Goal combinedGoal = makeGoal(searchStrategies.elementAt(0));
		for (int i = 1; i < searchStrategies.size(); i++) {
			Goal nextGoal = makeGoal(searchStrategies.elementAt(i));
			combinedGoal = combinedGoal.and(nextGoal);
		}
		return combinedGoal;
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
		Dichotomize dichotomize = new Dichotomize(this, obj);
		Solution solution = dichotomize.execute();
		if (solution != null)
			log("Optimal solution is found. Objective: "
					+solution.getValue(obj.getName()));
		return solution;
	}
	
//	/**
//	 * Define a goal that will be used by find solutions methods 
//	 * @param goal
//	 * @see {@link AbstractSolver#findSolution()} {@link AbstractSolver#findOptimalSolution(Var)}
//	 * {@link AbstractSolver#findAllSolutions()}
//	 */
//	public void setGoal(Goal goal) {
//		this.goal = goal;
//	}
//
//	/**
//	 * If the goal is not defined by default the solver will use
//	 * GoalAssignValues(this,problem.getVars());
//	 * @return a goal to be used with find solutions methods
//	 */
//	public Goal getGoal() {
//		if (goal == null)
//			return new GoalAssignValues(problem.getVars());
//		return goal;
//	}
	
//	/**
//	 * Returns an "AND" Goal. The Goal "AND" succeeds if all three of the
//	 * Goals "g1", "g2" and "g3" succeed. The Goal "AND" fails if at least one
//	 * of the Goals "g1", "g2" or "g3" fail. It is implementation specific.
//	 * 
//	 * @param g1
//	 *            the first Goal which is part of the new "AND" Goal.
//	 * @param g2
//	 *            the second Goal which is part of the new "AND" Goal.
//	 * @param g3
//	 *            the third Goal which is part of the new "AND" Goal.
//	 * @return a Goal "AND" between the Goals "g1", "g2" and "g3".
//	 */
//	public Goal and(Goal g1, Goal g2, Goal g3) {
//		Goal g1g2 =  and(g1,g2);
//		return and(g1g2,g3);
//	}
	
//	/**
//	 * Returns an "AND" Goal. The Goal "AND" succeeds if both of the Goals "g1" and
//	 *         "g2" succeed. The Goal "AND" fails if at least one of
//	 *         the Goals "g1" or "g2" fail. It is implementation specific.
//	 * @param g1 the first Goal which is part of the new "AND" Goal.
//	 * @param g2 the other Goal which is part of the new "AND" Goal.
//	 * @return a Goal "AND" between the Goals "g1" and "g2".
//	 */
//	abstract public Goal and(Goal g1, Goal g2);
	
//	/**
//	 * Returns an "OR" Goal. The goal "OR" succeeds if at least one of the Goals "g1"
//	 *         or "g2" succeeds. The Goal "OR" fails if both Goals "g1"
//	 *         and "g2" fail. It is implementation specific.
//	 * @param g1 the first Goal which is part of the new "OR" Goal.
//	 * @param g2 the second Goal which is part of the new "OR" Goal.
//	 * @return a Goal "OR" between the Goals "g1" and "g2".
//	 */
//	abstract public Goal or(Goal g1, Goal g2);

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
//	public Solution findOptimalSolution(Objective objective, Var objectiveVar) {
//		// Add objective to problem if it was not yet added
//		if (objectiveVar.getName() == null || objectiveVar.getName().isEmpty())
//			objectiveVar.setName("_OBJECTIVE_");
//		if (getProblem().getVar(objectiveVar.getName()) == null) {
//			getProblem().add(objectiveVar);
//		}
//		Var objVar = objectiveVar;
//		if (objective == Objective.MAXIMIZE) {
//			objVar = objectiveVar.mul(-1);
//			objVar.setName("negation_of_" + objectiveVar.getName());
//			getProblem().add(objVar);
//		}
//			
//		SolverDichotomize dichomize = new SolverDichotomize(this, objVar);
//		Solution solution = dichomize.execute();
//		return solution;
//	}

	
//	/**
//	 * This method attempts to find all solutions for the Problem using the solving
//	 * algorithm as defined by the method setGoal(goal). 
//	 * It returns an array of found solutions of null if there are no solutions.
//	 * One have to be careful not to overload an available memory
//	 * because a number of found solutions could be huge. 
//	 * 
//	 * The process of finding all solutions can be also controlled by:
//	 * <ul>
//	 * <li> Adding a special goal that exclude some found solutions if they are
//	 * out of the interests. Such a goal can be added to the search goal using the method "and",
//	 * e.g. solver.setGoal(searchStrategy.and(filterGoal)). 
//	 * <li> MaxNumberOfSolutions that is the total number of considered solutions - may be limited by the method
//	 * setMaxNumberOfSolutions()
//	 * <li> TotalTimeLimit that is the number of seconds allocated for the entire optimization process.
//	 * </ul>
//	 * <br> At the same time the time for one iteration inside
//	 * optimization loop (a search of one solution) can be also limited by the use of the
//	 * special type of search goals such as GoalAssignValuesTimeLimit.
//	 * <br> The problem state after the execution of this method is always restored.  
//	 *
//	 * @return an array of solutions found or null	 
//	 */
//	public Solution[] findAllSolutions() {
//		clearSolutions();
//		Goal goalCheckMaxNumberOfSolutions = new GoalCheckMaxNumberOfSolutions(this);
//		Goal goalCheckTotalTimeLimit = new GoalCheckTotalTimeLimit(this);
//		Goal goalCheckLimits = goalCheckMaxNumberOfSolutions.and(goalCheckTotalTimeLimit);
//		Goal goal = and(getGoal(), new GoalAddSolution(this).and(goalCheckLimits));
//		execute(goal, ProblemState.RESTORE);
//		return getSolutions();
//	}
	
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
	public boolean applySolution(Solution solution)	{
		return execute(new GoalApplySolution(solution));
	}
	
	/**
	 * This method forces a solver to "backtrack". 
	 * It is used to emulate a failure, e.g. to produce all solutions.
	 * 
	 * @throws Exception 
	 */
	abstract public void backtrack() throws Exception;
	
	/**
	 * This method forces to trace (to log) all modification events for
	 * the variable "var". All modification events are described at the
	 * interface {@link Problem}
	 * @param var
	 */
	public void trace(Var var) {
		trace(var, PropagationEvent.ANY);
	}

	/**
	 * This method forces to trace (to log) the modification "event" for
	 * the variable "var". All modification events are described at the
	 * interface {@link Problem}
	 * @param var
	 */
	public void trace(Var var, PropagationEvent event) {
		if (event == PropagationEvent.ANY) {
			trace(var, PropagationEvent.MIN);
			trace(var, PropagationEvent.MAX);
			trace(var, PropagationEvent.REMOVE);
			trace(var, PropagationEvent.VALUE);
		}
		else
		if (event == PropagationEvent.RANGE) {
			trace(var, PropagationEvent.MIN);
			trace(var, PropagationEvent.MAX);
		}
		else
			(new javax.constraints.extra.ConstraintTraceVar(var, event)).post();
	}

	/**
	 * This method forces to trace (to log) all modification events for
	 * all variables "vars"
	 * @param vars
	 */
	public void trace(Var[] vars) {
		trace(vars, PropagationEvent.ANY);
	}

	/**
	 * This method forces solver to trace (to log) the modification "event"
	 * for all variables "var". All modification events are described at the
	 * interface {@link Problem}
	 * @param vars
	 * @param event
	 */
	public void trace(Var[] vars, PropagationEvent event) {
		for (int i = 0; i < vars.length; i++) {
			trace(vars[i],event);
		}
	}

	/**
	 * This method forces to trace (to log) all modification events for
	 * the variable "setVar". 
	 * @param setVar
	 */
	public void trace(VarSet setVar) {
		trace(setVar, PropagationEvent.ANY);
	}

	/**
	 * This method forces to trace (to log) the modification "event" for
	 * the variable "setVar". All modification events are described at the
	 * interface {@link Problem}
	 * @param setVar
	 * @param event
	 */
	public void trace(VarSet setVar, PropagationEvent event) {
		javax.constraints.Var indexVar = setVar.getCardinality();
		trace(indexVar,event);
		BasicVarSet basicSetVar = (BasicVarSet)setVar;
		javax.constraints.Var[] requiredVars = basicSetVar.getRequiredVars();
		trace(requiredVars,event);
	}
	

	
	

	

}
