package org.jcp.jsr331.samples;

import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.Solver;

public class Test0 {
	
	Problem p = ProblemFactory.newProblem("TestXYZ");

	public void define() { // PROBLEM DEFINITION
		//======= Define variables
		int[] domain = new int[] {1,2,4,7,9};
		Var x = p.variable("X", domain);
		Var y = p.variable("Y", domain);
		Var z = p.variable("Z", domain);
		//======= Define and post constraints 
		try {
			p.post(x,"<",y); 		// X < Y
			p.post(z,">",7); 		// Z > 7
			p.post(x.plus(y), "=", z);  // X + Y = Z
			Var[] vars   = { x, y, z };
			int[] values = { 3, 4, -7 };
			p.post(p.scalProd(values,vars),"<", 16); // 3X + 4Y - 7Z < 16
			
			p.postAllDiff(vars);
			
		} catch (Exception e) {
			p.log("Error posting constraints: " + e);
			System.exit(-1);
		}
	}		
		
	public void solve() {	// PROBLEM RESOLUTION
		p.log("=== Find One solution:");
		Solver solver = p.getSolver(); //new Solver(partition);
		Solution solution = solver.findSolution(); 
		if (solution != null)
			solution.log();
		else
			p.log("No Solutions");
		solver.logStats();
	}
	
	public static void main(String[] args) {
		Test0 t = new Test0();
		t.define();
		t.solve();
	}
}