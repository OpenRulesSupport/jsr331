package org.jcp.jsr331.samples;

import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.Solver;

public class TestPercent {
	
	Problem p = ProblemFactory.newProblem("TestXYZ");

	public void define() { // PROBLEM DEFINITION
		//======= Define variables
		Var x = p.variable("X", 20,200);
		Var y = x.multiply(25).divide(100);
		p.add("25percent",y);
		
		Var percent = p.variable("P",0,5);
		Var z = x.multiply(percent).divide(100);
		p.add("Z",z);
		//======= Define and post constraints 
	}		
		
	public void solve() {	// PROBLEM RESOLUTION
		Solver solver = p.getSolver();
		Solution[] solutions = solver.findAllSolutions();
		for (Solution solution : solutions)
			solution.log();
	}
	
	public static void main(String[] args) {
		TestPercent t = new TestPercent();
		t.define();
		t.solve();
	}
}