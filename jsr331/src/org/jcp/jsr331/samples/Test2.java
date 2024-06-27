package org.jcp.jsr331.samples;

import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.Solver.Objective;
import javax.constraints.impl.Problem;
import javax.constraints.impl.search.StrategyLogVariables;

public class Test2 extends Problem {
	
	public void define() { // PROBLEM RESOLUTION
		//======= Define variables
		Var x = variable("X", 0, 10);
		Var y = variable("Y", 0, 10);
		Var z = variable("Z", 0, 10);
		Var cost = x.multiply(3).multiply(y).minus(z.multiply(4)); // Cost = 3XY - 4Z
		add("cost",cost);
		
		//======= Define constraints
		post(x,"<",y); 			// X < Y
		post(x.plus(y),"=",z); 	// X + Y = Z
		post(y,">",5); 			// Y > 5
		post(cost,">",2);		// cost > 2
		post(cost,"<=",25);    	// cost <= 25
		log("After Constraint Posting",getVars());
		
		//cost.eq(30).post();
	}
	
	public void solve() { // PROBLEM RESOLUTION
		//======= Find an optimal solution
		log("=== SOLVE:");
		Solver solver = getSolver(); 
		//solver.traceExecution(true);
		solver.addSearchStrategy(new StrategyLogVariables(solver)); 
		Solution solution = 
			solver.findOptimalSolution(Objective.MAXIMIZE, getVar("cost")); 
		if (solution != null)
			solution.log();
		else
			log("No Solutions");
			
		log("After Search",getVars());
	}
	
	public static void main(String[] args) {
		Test2 p = new Test2();
		p.define();
		p.solve();
	}
}