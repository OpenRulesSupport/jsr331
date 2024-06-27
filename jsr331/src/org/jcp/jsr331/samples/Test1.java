package org.jcp.jsr331.samples;

import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.Solver.ProblemState;
import javax.constraints.ValueSelector.ValueSelectorType;
import javax.constraints.VarSelector.VarSelectorType;
import javax.constraints.impl.Problem;

public class Test1 extends Problem {
	
	Var[] vars;

	public void define() { // PROBLEM DEFINITION
		// ======= Define variables
		Var x = variable("X", 0, 10);
		Var y = variable("Y", 0, 10);
		Var z = variable("Z", 0, 10);
		vars = new Var[]{ x, y, z };
		
		Var cost = x.multiply(3).multiply(y).minus(z.multiply(4)); // Cost = 3XY-4Z
		add("cost", cost);
		log("Before Constraint Posting", getVars());

		// ======= Define and post constraints
		try {
			post(x, "<", y); // X < Y
			post(x.plus(y), "=", z); // X + Y = Z
			post(y, ">", 5); // Y > 5
			post(cost, ">", 2); // cost > 2
			post(cost, "<=", 25); // cost <= 25
		} catch (Exception e) {
			log("Error posting constraints: " + e.getMessage());
			System.exit(-1);
		}
		log("After Constraint Posting", getVars());
	}

	public void solve() { // PROBLEM RESOLUTION
		log("=== Find One solution:");
		Solver solver = getSolver(); // new Solver(problem);
		SearchStrategy strategy = solver.getSearchStrategy();
		strategy.setVars(vars);
		strategy.setVarSelectorType(VarSelectorType.MAX_VALUE);
		strategy.setValueSelectorType(ValueSelectorType.MAX);
		Solution solution = solver.findSolution(ProblemState.RESTORE);
		if (solution != null)
			solution.log();
		else
			log("No Solutions");

		log("After Search", getVars());
	}

	public static void main(String[] args) {
		Test1 p = new Test1();
		p.define();
		p.solve();
	}
}