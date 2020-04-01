package org.jcp.jsr331.samples;

import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.impl.Problem;
import javax.constraints.impl.search.Solver;
import javax.constraints.impl.search.goal.GoalAssignValuesTimeLimit;
import javax.constraints.impl.search.selectors.VarSelectorMinDomainMinValue;

public class QueensWithGoals {

	public static void main(String[] args) {

		//========= Problem Representation ==================
		Problem problem = new Problem("Queens");
		String arg = (args.length < 1) ? "20" : args[0];
		int size = Integer.parseInt(arg);
		problem.log("Queens " + size + ". ");

		// create 3 arrays of variables
		Var[] x = problem.variableArray("x",0, size-1, size);
		Var[] x1 = new Var[size];
		Var[] x2 = new Var[size];		
		for (int i = 0; i < size; i++) {
			x1[i] = x[i].plus(i);
			x2[i] = x[i].minus(i);
		}

		// post "all different" constraints
		problem.postAllDifferent(x);
		problem.postAllDifferent(x1);
		problem.postAllDifferent(x2);

		//========= Problem Resolution ==================
		// Find a solution
		Solver solver = new Solver(problem);
		solver.setTimeLimit(600000); // milliseconds
		SearchStrategy strategy = solver.getSearchStrategy();
		solver.setSearchStrategy(new GoalAssignValuesTimeLimit(x, 
				                 new VarSelectorMinDomainMinValue(strategy), // new VarSelectorMinSize(x),  - SLOW!
				                 new javax.constraints.impl.search.selectors.ValueSelectorMin()));
		Solution solution = solver.findSolution();
		if(solution == null)
			problem.log("no solutions found");
		else{
			solution.log();
		}
		solver.logStats();
	}
}
