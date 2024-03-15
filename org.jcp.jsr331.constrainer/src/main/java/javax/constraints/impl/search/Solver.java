//================================================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1 
// 
// CONSTRAINER-BASED REFERENCE IMPLEMENTATION
//
// Copyright (c) Cork Constraint Computation Centre, 2010
// University College Cork, Cork, Ireland, www.4c.ucc.ie
// Constrainer is copyrighted by Exigen Group, USA.
// 
//================================================================
package javax.constraints.impl.search;

import javax.constraints.ProblemState;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.Objective;
import javax.constraints.VarReal;
import javax.constraints.extra.PropagationEvent;
import javax.constraints.extra.ReversibleAction;
import javax.constraints.impl.Problem;
import javax.constraints.impl.search.StrategyLogVar;
import javax.constraints.impl.search.StrategyLogVarReal;
//import javax.constraints.impl.goal.SolverDichotomize;
import javax.constraints.impl.search.goal.Goal;
import javax.constraints.impl.search.goal.ReversibleActionGoal;
import javax.constraints.impl.search.goal.SolverWithGoals;

import com.exigen.ie.constrainer.Constrainer;
import com.exigen.ie.constrainer.FloatExp;
import com.exigen.ie.constrainer.GoalAnd;
import com.exigen.ie.constrainer.GoalFastMinimize;
import com.exigen.ie.constrainer.GoalFloatFastMinimize;
//import com.exigen.ie.constrainer.GoalGenerate;
//import com.exigen.ie.constrainer.GoalPrintObject;
//import com.exigen.ie.constrainer.GoalPrintSolutionNumber;
import com.exigen.ie.constrainer.IntExp;
import com.exigen.ie.constrainer.IntExpArray;
//import com.exigen.ie.constrainer.IntVarSelectorMaxSize;
import com.exigen.ie.constrainer.TimeLimitException;

public class Solver extends SolverWithGoals {
	
	public Solver(Problem problem) {
		super(problem);
//		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
//		p.getConstrainer().traceExecution();
	}
	
	/**
	 * This method forces a solver to "backtrack". 
	 * It is used to emulate a failure, in particular to produce all solutions.
	 * 
	 * @throws Exception to backtrack
	 */
	public void backtrack() throws Exception {
		getConstrainer().fail();
	}
	
	public Constrainer getConstrainer() {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		return p.getConstrainer();
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
	public Goal and(Goal g1, Goal g2) {
		String name = "and("+g1.getName()+","+g2.getName()+")";
		com.exigen.ie.constrainer.Goal goal1 = (com.exigen.ie.constrainer.Goal)g1.getImpl();
		if (goal1 == null)
			throw new RuntimeException("Goal "+g1.getName() + 
					" cannot be used inside solver.and()");
		com.exigen.ie.constrainer.Goal goal2 = (com.exigen.ie.constrainer.Goal)g2.getImpl();
		if (goal2 == null)
			throw new RuntimeException("Goal "+g2.getName() + 
					" cannot be used inside solver.and()");
		com.exigen.ie.constrainer.Goal andGoal = new com.exigen.ie.constrainer.GoalAnd(goal1,goal2);
		andGoal.name(name);
		Goal goal = new javax.constraints.impl.search.ConstrainerGoal(this,andGoal);
		goal.setName(name);
		return goal;
	}
	
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
	public Goal or(Goal g1, Goal g2) {
		String name = "or("+g1.getName()+","+g2.getName()+")";
		com.exigen.ie.constrainer.Goal goal1 = (com.exigen.ie.constrainer.Goal)g1.getImpl();
		if (goal1 == null)
			throw new RuntimeException("Goal "+g1.getName() + " cannot be used inside solver.or()");
		com.exigen.ie.constrainer.Goal goal2 = (com.exigen.ie.constrainer.Goal)g2.getImpl();
		if (goal2 == null)
			throw new RuntimeException("Goal "+g2.getName() + " cannot be used inside solver.or()");
		com.exigen.ie.constrainer.Goal orGoal = new com.exigen.ie.constrainer.GoalOr(goal1,goal2);
		orGoal.name(name);
		Goal goal = new javax.constraints.impl.search.ConstrainerGoal(this,orGoal);
		goal.setName(name);
		return goal;
	}
	
	public boolean execute(Goal goal, ProblemState restoreOrNot) {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		//p.getConstrainer().traceExecution();
		//p.getConstrainer().traceFailures();
		com.exigen.ie.constrainer.Goal myGoal = (com.exigen.ie.constrainer.Goal) goal.getImpl();
		if (myGoal == null) {
			throw new RuntimeException("Critical Error: goal" +
					goal.getName() + " does not have an implementation");
		}
		boolean restore = (restoreOrNot == ProblemState.RESTORE)? true : false;
		return p.getConstrainer().execute(myGoal, restore);
	}
	
	/**
	 * To create a new goal on the javax.constraints level without necessity to
	 * go down to the underlying CP solver, one needs to use a goal that will
	 * serve as an implementation object. "goalThis" serves exactly these
	 * purposes and actually executes the method execute() from the Goal passed
	 * as a parameter.
	 * 
	 * @param goal a goal
	 */
	public Object goalThis(Goal goal) {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		return new GoalThis(p.getConstrainer(), goal);
	}
	
	/**
	 * Adds an application-specific action "goal" that will be executed during backtracking.
	 * @param action the action (goal) to be executed during backtracking.
	 */
	public void addReversibleAction(ReversibleAction action) {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		Goal goal = ((ReversibleActionGoal)action).getGoal();
		com.exigen.ie.constrainer.Goal myGoal = (com.exigen.ie.constrainer.Goal) goal.getImpl();
		if (myGoal == null) {
			throw new RuntimeException("Critical Error: goal" +
					goal.getName() + " does not have an implementation");
		}
		p.getConstrainer().addUndoableAction(myGoal);
	}
	
	/**
	 * If flag is true, all failures will be traced (logged)
	 * @param flag a boolean
	 */
	public void traceFailures(boolean flag) {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem) getProblem();
		if (flag)
			p.getConstrainer().traceFailures(1);
		else
			p.getConstrainer().traceFailures(0);
	}
	
	/**
	 * This method attempts to find the solution that minimizes/maximizes the objective variable.
	 * It uses the search goal defined by the method setGoal(). 
	 * The optimization process can be also controlled by:
	 * <ul>
	 * <li> OptimalPrecision that is a difference between optimal solutions - see setOptimalPrecision()
	 * <li> MaxNumberOfSolutions that is the total number of considered solutions - may be limited by the method
	 * setMaxNumberOfSolutions()
	 * <li> TotalTimeLimit that is the number of seconds allocated for the entire optimization process.
	 * </ul>
	 * <br> At the same time the time for one iteration inside
	 * optimization loop (a search of one solution) can be also limited by the use of the
	 * special type of search goals such as GoalAssignValuesTimeLimit.
	 * <br> The problem state after the execution of this method is always restored. All variables
	 * that were added to the problems (plus the obejctiveVar) will have their assigned values 
	 * saved inside the optimal solution. 
	 * 
	 * @param objective Objective.MINIMIZE or Objective.MAXIMIZE
	 * @param objectiveVar the variable that is being minimized/maximized
	 * @return Solution if a solution is found,
	 *         null if there are no solutions.
	 */
//	public Solution findOptimalSolutionNative(Objective objective, Var objectiveVar) {
//		Problem problem = (Problem) getProblem();
//		// Add objective to problem if it was not yet added
//		if (objectiveVar.getName() == null || objectiveVar.getName().isEmpty())
//			objectiveVar.setName("_OBJECTIVE_");
//		if (problem.getVar(objectiveVar.getName()) == null) {
//			problem.add(objectiveVar);
//		}
//		Var objVar = objectiveVar;
//		if (objective == Objective.MAXIMIZE) {
//			objVar = objectiveVar.mul(-1);
//			objVar.setName("negation_of_" + objectiveVar.getName());
//			problem.add(objVar);
//		}
//			
//		Constrainer constrainer = problem.getConstrainer();
//		Var[] vars = problem.getVars();
//		IntExpArray intvars = new IntExpArray(constrainer,vars.length);
//		for(int i=0; i<vars.length; i++) {
//			IntExp var = (IntExp)vars[i].getImpl();
//			intvars.set(var, i);
//		}
//		
//		IntExp myObj = (IntExp)objVar.getImpl();
//		com.exigen.ie.constrainer.Goal minimize = new GoalFastMinimize(new GoalGenerate(intvars), myObj);
//	    boolean flag = constrainer.execute(minimize);
//		
//		SolverDichotomize dichomize = new SolverDichotomize(this, objVar);
//		Solution solution = dichomize.execute();
//		return solution;
//	}
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
		addObjective(objectiveVar);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		Constrainer constrainer = p.getConstrainer();
		
		SearchStrategy searchStrategy = getSearchStrategy();
		if (isTraceSolutions()) {
			addSearchStrategy(new StrategyLogVar("Found a solution with ", objectiveVar));
		}
		
		Goal strategy = makeGoal(searchStrategy);
		com.exigen.ie.constrainer.Goal goal = (com.exigen.ie.constrainer.Goal)strategy.getImpl();
		for (int i = 1; i < getSearchStrategies().size(); i++) {
			strategy = makeGoal(getSearchStrategies().elementAt(i));
			com.exigen.ie.constrainer.Goal nextGoal = (com.exigen.ie.constrainer.Goal)strategy.getImpl();
			goal = new GoalAnd(goal,nextGoal);
		}
		
		//clearSolutions();
		com.exigen.ie.constrainer.Goal saveGoal = new GoalSaveSolution(this);
		com.exigen.ie.constrainer.Goal totalGoal = new GoalAnd(goal,saveGoal);
		
		IntExp cObj = (IntExp)objectiveVar.getImpl();
		if ( objective.equals(Objective.MAXIMIZE) ) {
			cObj = cObj.mul(-1);
		}
//		if (solver.getMaxNumberOfSolutions() > 0)
//			constrainer.set
		//boolean trace = true;
		boolean trace = false;
		boolean goal_saves_solution = true;
		GoalFastMinimize optimizationGoal = new GoalFastMinimize(totalGoal, cObj, trace, goal_saves_solution);
		Solution solution = null;
		try {
			if (constrainer.execute(optimizationGoal)) {
//				solution = new BasicSolution(this,1);
//				solution.setSolutionNumber(optimizationGoal.numberOfSolutions());
				solution = this.getSolution();
				solution.setSolutionNumber(optimizationGoal.numberOfSolutions());
			}
		} catch (Exception e) {
			// TODO: handle exception TimeLimitException
			if (e instanceof TimeLimitException) {
				solution = this.getSolution();
//				if (optimizationGoal.numberOfSolutions() > 0) {
//					solution = new BasicSolution(this,1);
//					solution.setSolutionNumber(optimizationGoal.numberOfSolutions());
//				}
			}
		}
		return solution;
	}
	
	public Solution findOptimalSolution(Objective objective, VarReal objectiveVar) {
		addObjective(objectiveVar);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		Constrainer constrainer = p.getConstrainer();
		
		SearchStrategy searchStrategy = getSearchStrategy();
		if (isTraceSolutions()) {
			addSearchStrategy(new StrategyLogVarReal("Found a solution with ", objectiveVar));
		}
		
		Goal strategy = makeGoal(searchStrategy);
		com.exigen.ie.constrainer.Goal goal = (com.exigen.ie.constrainer.Goal)strategy.getImpl();
		for (int i = 1; i < getSearchStrategies().size(); i++) {
			strategy = makeGoal(getSearchStrategies().elementAt(i));
			com.exigen.ie.constrainer.Goal nextGoal = (com.exigen.ie.constrainer.Goal)strategy.getImpl();
			goal = new GoalAnd(goal,nextGoal);
		}
		
		//clearSolutions();
		com.exigen.ie.constrainer.Goal saveGoal = new GoalSaveSolution(this);
		com.exigen.ie.constrainer.Goal totalGoal = new GoalAnd(goal,saveGoal);
		
		FloatExp cObj = (FloatExp)objectiveVar.getImpl();
		if ( objective.equals(Objective.MAXIMIZE) ) {
			cObj = cObj.mul(-1);
		}
//		if (solver.getMaxNumberOfSolutions() > 0)
//			constrainer.set
		boolean trace = false;
		boolean goal_saves_solution = true;
		//GoalFastMinimize optimizationGoal = new GoalFastMinimize(totalGoal, cObj, trace, goal_saves_solution);
		double costStep = 0.1;
		GoalFloatFastMinimize optimizationGoal = 
				//new GoalFloatFastMinimize(totalGoal, cObj, trace, goal_saves_solution);
				new GoalFloatFastMinimize(totalGoal, cObj, costStep);
		
		Solution solution = null;
		try {
			if (constrainer.execute(optimizationGoal)) {
//				solution = new BasicSolution(this,1);
//				solution.setSolutionNumber(optimizationGoal.numberOfSolutions());
				solution = this.getSolution();
			}
		} catch (Exception e) {
			// TODO: handle exception TimeLimitException
			if (e instanceof TimeLimitException) {
				solution = this.getSolution();
//				if (optimizationGoal.numberOfSolutions() > 0) {
//					solution = new BasicSolution(this,1);
//					solution.setSolutionNumber(optimizationGoal.numberOfSolutions());
//				}
			}
		}
		return solution;
	}
	
	public IntExpArray createConstrainerVars(Var[] vars) {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		Constrainer constrainer = p.getConstrainer();
		IntExpArray intvars = new IntExpArray(constrainer,vars.length);
		for(int i=0; i<vars.length; i++) {
			IntExp var = (IntExp)vars[i].getImpl();
			intvars.set(var, i);
		}
		return intvars;
	}
	
	@Override
	public void logStats() {
		log("*** Execution Profile ***");
		Problem p = (Problem)getProblem();
		int ncp = p.getConstrainer().numberOfChoicePoints();
		if (ncp >= 0)
			log("Number of Choice Points: " + ncp);
		int nf = p.getConstrainer().numberOfFailures();
		if (nf >= 0)
			log("Number of Failures: " + nf);
		
//		long occupied_memory = Runtime.getRuntime().totalMemory()
//		- Runtime.getRuntime().freeMemory();
//		log("Occupied memory: " + occupied_memory);
		long executionTime = System.currentTimeMillis() - getSolverStartTime();
		log("Execution time: " + executionTime + " msec");
	}
	
	public void trace(Var[] vars, PropagationEvent event) {
		for (int i = 0; i < vars.length; i++) {
			trace(vars[i],event);
		}
		
		Problem p = (Problem)getProblem(); 
		Constrainer constrainer = p.getConstrainer();
		IntExpArray intvars = new IntExpArray(constrainer,vars.length);
		for(int i=0; i<vars.length; i++) {
			IntExp var = (IntExp)vars[i].getImpl();
			intvars.set(var, i);
		}
		constrainer.trace(intvars);
	}
	
	/**
	 * Sets the duration, in milliseconds, of the time limit on searches.
	 * 
	 * @param milliseconds
	 *            the new time limit in milliseconds
	 */
	public void setTimeLimit(int milliseconds) {
		super.setTimeLimit(milliseconds);
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)getProblem();
		p.getConstrainer().setTimeLimit(milliseconds);
	}


}
