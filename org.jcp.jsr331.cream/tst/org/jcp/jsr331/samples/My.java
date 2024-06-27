package org.jcp.jsr331.samples;

import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;

public class My {
	
	Problem p = ProblemFactory.newProblem("TestXYZ");

	public void define() { // PROBLEM DEFINITION
		p.variable("M",1,5);
		p.log(p.getVars());
	}		
		
	public void solve() {	// PROBLEM RESOLUTION
		p.log("=== Find One solution:");
		Solver solver = p.getSolver(); 
		Solution solution = solver.findSolution(); 
		if (solution != null)
			solution.log();
		else
			p.log("No Solutions");
		solver.logStats();
	}
	
	public static void main(String[] args) {
		My t = new My();
		t.define();
		t.solve();
	}
}