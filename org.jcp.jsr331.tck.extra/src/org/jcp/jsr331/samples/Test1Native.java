package org.jcp.jsr331.samples;

import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.impl.search.GoalAssignValuesNative;

public class Test1Native {

	public static void main(String[] args) {
		Problem p = ProblemFactory.newProblem("TestXYZ");

		//======= Define variables
		Var x = p.variable("X", 0, 10);
		Var y = p.variable("Y", 0, 10);
		Var z = p.variable("Z", 0, 10);
		Var cost = x.multiply(3).multiply(y).minus(z.multiply(4)); // Cost = 3XY - 4Z
		p.add("cost",cost);
		p.log("Before Constraint Posting");
		p.log(p.getVars());
		
		//======= Define and post constraints 
		try {
			p.post(x,"<",y); 			// X < Y
			p.post(x.plus(y),"=",z); 	// X + Y = Z
			p.post(y,">",5); 			// Y > 5
			p.post(cost,">",2);		// cost > 2
			p.post(cost,"<=",25);    	// cost <= 25
		} 
		catch (Exception e) {
			p.log("Error to post constraint: " + e.getMessage());
			System.exit(-1);
		}
		
		
		p.log("After Constraint Posting");
		p.log(p.getVars());
			
		//======= Find a solution
		p.log("=== Find One solution:");
		javax.constraints.impl.search.Solver solver = 
				(javax.constraints.impl.search.Solver)p.getSolver(); 
		Var[] vars = { x, y, z };
		solver.trace(vars);
		solver.setSearchStrategy(new GoalAssignValuesNative(solver,vars));
		Solution solution = solver.findSolution(); //ProblemState.RESTORE);
		if (solution != null)
			solution.log();
		else
			p.log("No Solutions");
			
		p.log("After Search");
		p.log(p.getVars());
	}
}