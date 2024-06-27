package org.jcp.jsr331.samples;

import javax.constraints.Solution;
import javax.constraints.SolutionIterator;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.impl.Problem;
import javax.constraints.impl.search.goal.GoalAssignValues;

public class Test3 extends Problem {

	public void define() {

		// ======= Define variables
		Var x = variable("X", 0, 10);
		Var y = variable("Y", 0, 10);
		Var z = variable("Z", 0, 10);
		Var cost = x.multiply(3).multiply(y).minus(z.multiply(4)); // Cost = 3XY
																	// - 4Z
		add("cost", cost);

		// ======= Define constraints
		post(x, "<", y); // X < Y
		post(x.plus(y), "=", z); // X + Y = Z
		post(y, ">", 5); // Y > 5
		post(cost, ">", 2); // cost > 2
		post(cost, "<=", 25); // cost <= 25
		log("After Constraint Posting", getVars());

		// post(cost,"=",30);

	}

	public void solve() {
		// ======= Find All Solutions
		log("=== Find All Solutions:");
		Solver solver = getSolver();
		//solver.traceFailures(true);
		//solver.traceExecution(true);

//		Solution[] solutions = solver.findAllSolutions();
//		for (Solution solution : solutions)
//			solution.log();
		SolutionIterator iter = solver.solutionIterator();
		while (iter.hasNext()) {
			Solution solution = iter.next();
			solution.log();
		}

		log("After Search", getVars());
	}

	public static void main(String[] args) {
		Test3 p = new Test3();
		p.define();
		p.solve();
	}
}