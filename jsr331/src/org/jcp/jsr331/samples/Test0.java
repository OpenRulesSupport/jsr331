package org.jcp.jsr331.samples;

import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.impl.Problem;
import javax.constraints.Solver;

public class Test0 extends Problem {

	public void define() { // PROBLEM DEFINITION
		//======= Define variables
		int[] domain = new int[] {1,2,4,7,9};
		Var x = variable("X", domain);
		Var y = variable("Y", domain);
		Var z = variable("Z", domain);
		//======= Define and post constraints 
		try {
			post(x,"<",y); 		// X < Y
			post(z,">",7); 		// Z > 7
			post(x.plus(y), "=", z);  // X + Y = Z
			Var[] vars   = { x, y, z };
			int[] values = { 3, 4, -7 };
			post(values,vars,"<", 16); // 3X + 4Y - 7Z < 16
			
			postAllDiff(vars);
			
		} catch (Exception e) {
			log("Error posting constraints: " + e);
			System.exit(-1);
		}
	}		
		
	public void solve() {	// PROBLEM RESOLUTION
		log("=== Find One solution:");
		Solver solver = getSolver(); //new Solver(p);
		Solution solution = solver.findSolution(); 
		if (solution != null)
			solution.log();
		else
			log("No Solutions");
		solver.logStats();
	}
	
	public static void main(String[] args) {
		Test0 p = new Test0();
		p.define();
		p.solve();
	}
}