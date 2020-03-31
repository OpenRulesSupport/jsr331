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

import javax.constraints.Solution;
import javax.constraints.impl.search.BasicSolution;

import com.exigen.ie.constrainer.Failure;
import com.exigen.ie.constrainer.Goal;


public class GoalSaveSolution extends com.exigen.ie.constrainer.GoalImpl {
	Solver solver;
	
	public GoalSaveSolution(Solver solver) {
		super(solver.getConstrainer(),"GoalSaveSolution");
		this.solver = solver;
	}

	public Goal execute() throws Failure {
		solver.clearSolutions();
		Solution solution = new BasicSolution(solver, 1);
		solver.addSolution(solution);
//		getProblem().log("Saved solution: " + solution.toString());
		return null;
	}

}
