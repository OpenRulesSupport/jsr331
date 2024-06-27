package org.jcp.jsr331.samples;

import javax.constraints.*;

public class Test4 {
	
	Problem p = ProblemFactory.newProblem("TestXYZ");
	
	Var[] vars;

	public void define() { // PROBLEM DEFINITION
		// ======= Define variables
		Var x = p.variable("X", 0, 10);
		Var y = p.variable("Y", 0, 10);
		Var z = p.variable("Z", 0, 10);
				 
		Var cost = x.multiply(3).multiply(y).minus(z.multiply(4)); // Cost = 3XY-4Z
		p.add("cost", cost);
		p.log("Before Constraint Posting");
		p.log(p.getVars());
		
		vars = new Var[]{ x, y, z, cost };

		// ======= Define and post constraints
		try {
			p.post(x, "<", y); // X < Y
			p.post(x.plus(y), "=", z); // X + Y = Z
			p.post(y, ">", 5); // Y > 5
			p.post(cost, ">", 2); // cost > 2
			p.post(cost, "<=", 25); // cost <= 25
		} catch (Exception e) {
			p.log("Error posting constraints: " + e.getMessage());
			System.exit(-1);
		}
		p.log("After Constraint Posting");
		p.log(p.getVars());
	}

	public void solve() { // PROBLEM RESOLUTION
		p.log("=== Find One solution:");
		Solver solver = p.getSolver(); // new Solver(problem);
		SearchStrategy strategy = solver.getSearchStrategy();
		strategy.setVars(vars);
		strategy.setVarSelectorType(VarSelectorType.MAX_VALUE);
		strategy.setValueSelectorType(ValueSelectorType.MAX);
		Solution solution = solver.findSolution(ProblemState.RESTORE);
		if (solution != null) {
			solution.log();
		}
		else
			p.log("No Solutions");

		p.log("After Search");
		p.log(p.getVars());
		solver.logStats();
	}
	
	public void postCostConstraint() {
		Var cost = p.getVar("cost");
		p.post(cost,"<",20);
	}

	public static void main(String[] args) {
		Test4 t = new Test4();
		t.define();
		t.solve();
		t.postCostConstraint();
		t.solve();
	}
}