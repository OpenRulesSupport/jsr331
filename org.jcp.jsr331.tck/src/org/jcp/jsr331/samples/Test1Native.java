package org.jcp.jsr331.samples;

import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.impl.Problem;
import javax.constraints.impl.search.GoalAssignValuesNative;
import javax.constraints.impl.search.Solver;

public class Test1Native {

	public static void main(String[] args) {
		Problem problem = new Problem("Test");

		//======= Define variables
		Var x = problem.variable("X", 0, 10);
		Var y = problem.variable("Y", 0, 10);
		Var z = problem.variable("Z", 0, 10);
		Var cost = x.multiply(3).multiply(y).minus(z.multiply(4)); // Cost = 3XY - 4Z
		problem.add("cost",cost);
		problem.log("Before Constraint Posting",problem.getVars());
		
		//======= Define and post constraints 
		try {
			problem.post(x,"<",y); 		// X < Y
			problem.post(x.plus(y),"=",z); 	// X + Y = Z
			problem.post(y,">",5); 		// Y > 5
			problem.post(cost,">",6); 		// cost > 6
			problem.post(cost,"<=",25);    	// cost <= 25
			problem.post(cost,"<",8);
		} 
		catch (Exception e) {
			problem.log("Error to post constraint: " + e.getMessage());
			System.exit(-1);
		}
		
		
		problem.log("After Constraint Posting",problem.getVars());
			
		//======= Find a solution
		problem.log("=== Find One solution:");
		Solver solver = new Solver(problem);//problem.getSolver(); //
		Var[] vars = { x, y, z };
		solver.trace(vars);
		solver.setSearchStrategy(new GoalAssignValuesNative(solver,vars));
		Solution solution = solver.findSolution(); //ProblemState.RESTORE);
		if (solution != null)
			solution.log();
		else
			problem.log("No Solutions");
			
		problem.log("After Search",problem.getVars());
	}
}