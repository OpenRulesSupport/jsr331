//*************************************************
//*  J A V A  C O M M U N I T Y  P R O C E S S    *
//*                                               *
//*              J S R  3 3 1                     *
//*                                               *
//*       CHOCO-BASED IMPLEMENTATION              *
//*                                               *
//* * * * * * * * * * * * * * * * * * * * * * * * *
//*          _       _                            *
//*         |  °(..)  |                           *
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
package javax.constraints.impl.search.goal;

/**
 * An implementation of the interface "Solver"
 * @author J.Feldman
 * Modified by: 
 * 
 */

import javax.constraints.Constraint;

import javax.constraints.Var;
import javax.constraints.extra.ReversibleAction;
import javax.constraints.Problem;
import javax.constraints.impl.search.goal.Goal;
import javax.constraints.impl.search.goal.GoalConstraint;
import javax.constraints.impl.search.goal.ReversibleActionGoal;
import javax.constraints.impl.search.goal.SolverWithGoals;

import choco.cp.solver.CPSolver;
import choco.cp.solver.goals.GoalHelper;
import choco.kernel.solver.ContradictionException;

import static choco.Choco.*;

public class Solver extends SolverWithGoals {
	ContradictionException contradictionException;
	final CPSolver chocoSolver;
	boolean modelRead;

	public Solver(Problem problem) {
		super(problem);
		contradictionException = ContradictionException.build();
		chocoSolver = new CPSolver();
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem)problem;
		modelRead = false;
	}
	
	public boolean isModelRead() {
		return modelRead;
	}

	public void setModelRead(boolean modelRead) {
		this.modelRead = modelRead;
	}


	public CPSolver getChocoSolver() {
		return chocoSolver;
	}

	/**
	 * This method forces a solver to "backtrack". It is used to emulate a
	 * failure, in particular to produce all solutions.
	 * 
	 * @throws Exception
	 */
	public void backtrack() throws Exception {
		// javax.constraints.impl.Problem p =
		// (javax.constraints.impl.Problem)getProblem();
		// p.getChocoSolver().
		log("backtrack");
		throw contradictionException; // ??
	}

	/**
	 * Returns an "AND" Goal. The Goal "AND" succeeds if both of the Goals "g1"
	 * and "g2" succeed. The Goal "AND" fails if at least one of the Goals "g1"
	 * or "g2" fail. It is implementation specific.
	 * 
	 * @param g1
	 *            the first Goal which is part of the new "AND" Goal.
	 * @param g2
	 *            the other Goal which is part of the new "AND" Goal.
	 * @return a Goal "AND" between the Goals "g1" and "g2".
	 */
	public Goal and(Goal g1, Goal g2) {
		String name = "and(" + g1.getName() + "," + g2.getName() + ")";
		choco.kernel.solver.goals.Goal goal1 = (choco.kernel.solver.goals.Goal) g1
				.getImpl();
		if (goal1 == null)
			throw new RuntimeException("Goal " + g1.getName()
					+ " cannot be used inside solver.and()");
		choco.kernel.solver.goals.Goal goal2 = (choco.kernel.solver.goals.Goal) g2
				.getImpl();
		if (goal2 == null)
			throw new RuntimeException("Goal " + g2.getName()
					+ " cannot be used inside solver.and()");
		choco.kernel.solver.goals.Goal andGoal = GoalHelper.and(goal1, goal2);
		Goal goal = new ChocoGoal(this, andGoal);
		goal.setName(name);
		return goal;
	}

	/**
	 * Returns an "OR" Goal. The goal "OR" succeeds if at least one of the Goals
	 * "g1" or "g2" succeeds. The Goal "OR" fails if both Goals "g1" and "g2"
	 * fail. It is implementation specific.
	 * 
	 * @param g1
	 *            the first Goal which is part of the new "OR" Goal.
	 * @param g2
	 *            the second Goal which is part of the new "OR" Goal.
	 * @return a Goal "OR" between the Goals "g1" and "g2".
	 */
	public Goal or(Goal g1, Goal g2) {
		String name = "or(" + g1.getName() + "," + g2.getName() + ")";
		choco.kernel.solver.goals.Goal goal1 = (choco.kernel.solver.goals.Goal) g1
				.getImpl();
		if (goal1 == null)
			throw new RuntimeException("Goal " + g1.getName()
					+ " cannot be used inside solver.or()");
		choco.kernel.solver.goals.Goal goal2 = (choco.kernel.solver.goals.Goal) g2
				.getImpl();
		if (goal2 == null)
			throw new RuntimeException("Goal " + g2.getName()
					+ " cannot be used inside solver.or()");
		choco.kernel.solver.goals.Goal orGoal = GoalHelper.or(goal1, goal2);
		Goal goal = new ChocoGoal(this, orGoal);
		goal.setName(name);
		return goal;
	}

	public boolean execute(Goal goal, ProblemState restoreOrNot) {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem) getProblem();

		if (!isModelRead()) {
			chocoSolver.read(p.getChocoModel());
			setModelRead(true);
		}
		
//		p.getChocoSolver().read(p.getChocoModel()); // ??
		// p.log("solveAndRestore with goal: " + goal.getName());
		int worldIndex = 0;		
		if (restoreOrNot == ProblemState.RESTORE)
			worldIndex = p.getChocoSolver().getWorldIndex(); // ??
		
		try {
			p.getChocoSolver().propagate();
		} catch (ContradictionException e) {
		}
		
		if (restoreOrNot == ProblemState.RESTORE)
			p.getChocoSolver().worldPush();

		// Goal g = (choco.kernel.solver.goals.Goal)goal.getImpl();
		choco.kernel.solver.goals.Goal g = (choco.kernel.solver.goals.Goal) goal
				.getImpl();
		if (g == null) {
			throw new RuntimeException("Critical Error: goal" + goal.getName()
					+ " does not have an implementation");
		}
		boolean found = false;
		try {
			p.getChocoSolver().setIlogGoal(g);
			found = p.getChocoSolver().solve();
			// if (!found)
			// p.log("Choco failed");
			// else
			// p.log("Choco succeeded");
		} catch (Exception e) {
			e.printStackTrace();
			p.log(e.toString());
			throw new RuntimeException(e);
		}
		if (restoreOrNot == ProblemState.RESTORE)
			p.getChocoSolver().worldPopUntil(worldIndex);
		return found;
	}

	/**
	 * To create a new goal on the javax.constraints level without necessity to
	 * go down to the underlying CP solver, one needs to use a goal that will
	 * serve as an implementation object. "goalThis" serves exactly these
	 * purposes and actually executes the method execute() from the Goal passed
	 * as a parameter.
	 * 
	 * @param goal
	 */
	public Object goalThis(Goal goal) {
		return new GoalThis(goal);
	}
	
	public Goal goalVarEqValue(Var var,int value) {
		return new GoalVarEqValue(this, var, value);
	}
	
	public Goal goalVarNeqValue(Var var,int value) {
		return new GoalVarNeqValue(this, var, value);
	}

	/**
	 * Adds an application-specific action "goal" that will be executed during
	 * backtracking.
	 * 
	 * @param action
	 *            the action (goal) to be executed during backtracking.
	 */
	public void addReversibleAction(ReversibleAction action) {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem) getProblem();
		Goal goal = ((ReversibleActionGoal) action).getGoal();
		choco.kernel.solver.goals.Goal myGoal = (choco.kernel.solver.goals.Goal) goal
				.getImpl();
		if (myGoal == null) {
			throw new RuntimeException("Critical Error: goal" + goal.getName()
					+ " does not have an implementation");
		}
		// TODO
		//p.getChocoSolver().addUndoableAction(myGoal); ??
	}

	/**
	 * If flag is true, all failures will be traced (logged)
	 * 
	 * @param flag
	 */
	public void traceFailures(boolean flag) {
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem) getProblem();
//		if (flag)
//			p.getChocoSolver().setVerbosity(CPSolver.SEARCH); ??
//		else
//			p.getChocoSolver().setVerbosity(CPSolver.FALSE);
	}

	@Override
	public void logStats() {
		log("*** Execution Profile ***");
		javax.constraints.impl.Problem p = (javax.constraints.impl.Problem) getProblem();
		p.getChocoSolver().flushLogs(); // ??

		long occupied_memory = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		log("Occupied memory: " + occupied_memory);
		long executionTime = System.currentTimeMillis() - getSolverStartTime();
		log("Execution time: " + executionTime + " msec");
	}

//	public void trace(Var[] vars, PropagationEvent event) {
//		for (int i = 0; i < vars.length; i++) {
//			trace(vars[i], event);
//		}
//
//		Problem p = (Problem) getProblem();
//		Constrainer constrainer = p.getConstrainer();
//		IntExpArray intvars = new IntExpArray(constrainer, vars.length);
//		for (int i = 0; i < vars.length; i++) {
//			IntExp var = (IntExp) vars[i].getImpl();
//			intvars.set(var, i);
//		}
//		constrainer.trace(intvars);
//	}

}
