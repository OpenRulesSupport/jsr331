package org.jcp.jsr331.samples;

import javax.constraints.*;

public class Test5 {
	
	Problem p = ProblemFactory.newProblem("TestXYZ");

	public void define() {

		// ======= Define variables
		Var x = p.variable("X", 0, 10);
		Var y = p.variable("Y", 0, 10);
		Var z = p.variable("Z", 0, 10);
		Var cost = x.multiply(3).multiply(y).minus(z.multiply(4)); //Cost=3XY-4Z
		p.add("cost", cost);

		// ======= Define constraints
		p.post(x, "<", y); // X < Y
		p.post(x.plus(y), "=", z); // X + Y = Z
		p.post(y, ">", 5); // Y > 5
		p.post(cost, ">", 2); // cost > 2
		p.post(cost, "<=", 25); // cost <= 25
		p.log("After Constraint Posting");
		p.log(p.getVars());

		//p.post(cost,">",6); 

	}

	public void solve() {
		p.log("=== Find All Solutions:");
		Solver solver = p.getSolver();
//		solver.traceFailures(true);
//		solver.traceExecution(true);
//		solver.setMaxNumberOfSolutions(4);
		Solution[] solutions = solver.findAllSolutions();
		for (Solution solution : solutions)
			solution.log();
				
		p.log("After Search");
		p.log(p.getVars());
	}
	
	public void postCostConstraint() {
		p.log("post cost > 6");
		Var cost = p.getVar("cost");
		p.post(cost,">",6);
	}

	public static void main(String[] args) {
		Test5 t = new Test5();
		t.define();
		t.solve();
		t.postCostConstraint();
		t.solve();
	}
}