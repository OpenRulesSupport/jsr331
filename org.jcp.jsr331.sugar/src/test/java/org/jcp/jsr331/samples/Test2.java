package org.jcp.jsr331.samples;

import javax.constraints.*;

public class Test2 {
	
	Problem p = ProblemFactory.newProblem("TestXYZ");
	
	public void define() { // PROBLEM RESOLUTION
		//======= Define partition.variables
		Var x = p.variable("X", 0, 10);
		Var y = p.variable("Y", 0, 10);
		Var z = p.variable("Z", 0, 10);
		Var cost = x.multiply(3).multiply(y).minus(z.multiply(4)); // Cost = 3XY - 4Z
		p.add("cost",cost);
		
		//======= Define constraints
		p.post(x,"<",y); 			// X < Y
		p.post(x.plus(y),"=",z); 	// X + Y = Z
		p.post(y,">",5); 			// Y > 5
		p.post(cost,">",2);		    // cost > 2
		p.post(cost,"<=",25);    	// cost <= 25
		p.log("After ConstraintClass Posting");
		p.log(p.getVars());
		
		//cost.eq(30).p.post();
	}
	
	public void solve() { // PROBLEM RESOLUTION
		//======= Find an optimal solution
		p.log("=== SOLVE:");
		Solver solver = p.getSolver(); 
		solver.setTimeLimit(1000);
		//solver.traceExecution(true);
		solver.traceSolutions(true);
		//solver.addStrategyLogVariables(); 
		Solution solution = 
			solver.findOptimalSolution(Objective.MAXIMIZE, p.getVar("cost")); 
		if (solution != null)
			solution.log();
		else
			p.log("No Solutions");
			
		p.log("After Search");
		p.log(p.getVars());
		solver.logStats();
	}
	
	public static void main(String[] args) {
		Test2 t = new Test2();
		t.define();
		t.solve();
	}
}