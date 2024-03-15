package org.jcp.jsr331.samples;

import javax.constraints.*;

public class TestXYZ {

	public static void main(String[] args) {
		// ==== PROBLEM DEFINITION ==============================
		Problem p = ProblemFactory.newProblem("TestXYZ");
		// ======= Define variables
		Var x = p.variable("X", 1, 10);
		Var y = p.variable("Y", 1, 10);
		Var z = p.variable("Z", 1, 10);
		Var cost = x.multiply(3).multiply(y).minus(z.multiply(4));
		p.add("Cost",cost);
		// ======= Define and post constraints
		p.post(x, "<", y); // X < Y
		p.post(x.plus(y), "=", z); // X + Y = Z

		// === PROBLEM RESOLUTION ================================
		p.log("=== Find Solution:");
		Solver solver = p.getSolver();
		Solution solution = solver.findSolution();
		if (solution != null)
			solution.log();
		else
			p.log("No Solution");
		p.log("Cost " + cost);
	}
}
