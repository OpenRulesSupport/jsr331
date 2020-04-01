package org.jcp.jsr331.samples;

import javax.constraints.Solver;
import javax.constraints.Solver.ProblemState;
import javax.constraints.extra.Reversible;
import javax.constraints.impl.ConstrainerGoal;
import javax.constraints.impl.Problem;
import javax.constraints.impl.search.goal.SolverWithGoals;

public class TestReversible {
	
	public static class GoalRev extends ConstrainerGoal {
		Reversible rev;
		int notRev;

		/**
		 * Constructor with a given values.
		 */
		public GoalRev(Solver s, Reversible rev, int notRev) {
			super(s, "GoalRev");
			this.rev = rev;
			this.notRev = notRev;
		}

		/**
		 * Increments the values and print them
		 */
		public ConstrainerGoal execute() {
			rev.setValue(rev.getValue() + 5);
			notRev = notRev + 5;
//			System.out.println(this);
			return null;
		}

		/**
		 * Returns the String representation for this goal.
		 */
		public String toString() {
			return rev + ", " + "notRev[" + notRev + "]";
		}

	}

	public static void main(String[] args) throws Exception {
		Problem p = new Problem("TestReversible");
		SolverWithGoals solver = (SolverWithGoals)p.getSolver();

		// Create the goal for reversible 0
//		Reversible rev = p.addReversible("rev", 0);
		Reversible rev = new javax.constraints.impl.Reversible(p,"rev", 0);
		GoalRev g = new GoalRev(solver, rev, 0);

		// Print the goal: initial state.
		p.log("Initial state: " + g);

		// Execute and print the goal: don't restore the value
		solver.execute(g, ProblemState.DO_NOT_RESTORE);
		p.log("Executed with ProblemState.DO_NOT_RESTORE: " + g);

		// Execute and print the goal: restore the value
		solver.execute(g, ProblemState.RESTORE);
		p.log("Executed with ProblemState.RESTORE: " + g);
	}

}

// Results: 
// Initial state: rev[0], notRev[0]
// rev[5], notRev[5]
// After goal execution with ProblemState.DO_NOT_RESTORE: rev[5], notRev[5]
// rev[10], notRev[10]
// After goal execution with ProblemState.RESTORE: rev[5], notRev[10]
 
