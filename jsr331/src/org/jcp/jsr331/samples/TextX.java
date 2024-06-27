package org.jcp.jsr331.samples;

import javax.constraints.Solver.Objective;
import javax.constraints.Solver.ProblemState;
import javax.constraints.impl.Problem;
import javax.constraints.Var;
import javax.constraints.Solver;
import javax.constraints.Solution;

public class TextX {

	public static void main(String[] args) {
		// ==== PROBLEM DEFINITION ==============================
		Problem p = new Problem("Test");
		// ======= Define variables
		Var x = p.variable("X", 1, 10);
		Var y = p.variable("Y", 1, 10);
		Var z = p.variable("Z", 1, 10);
		Var r = p.variable("R", 1, 10);
		Var[] vars = { x, y, z, r };
		Var a = new javax.constraints.impl.Var(p,"A", 1,5);
		// ======= Define and post constraints
		try {
			p.post(x, "<", y); // X < Y
			p.post(z, ">", 4); // Z > 4
			p.post(x.plus(y), "=", z); // X + Y = Z

			p.postAllDifferent(vars);

			int[] coef1 = { 3, 4, -5, 2 };
			p.post(coef1, vars, ">", 0); // 3x + 4y -5z + 2r > 0

			p.post(vars, ">=", 15); // x + y + z + r >= 15

			// 2x - 4y + 5z - r > x*y
			int[] coef2 = { 2, -4, 5, -1 };
			p.post(coef2, vars, ">", x.multiply(y));

		} catch (Exception e) {
			p.log("Error posting constraints: " + e);
			System.exit(-1);
		}

		// === PROBLEM RESOLUTION ================================
		p.log("=== Find Solution:");
		Solver solver = p.getSolver();
		Solution solution = solver.findSolution(ProblemState.RESTORE);
		if (solution != null)
			solution.log();
		else
		p.log("No Solution");
		p.log(vars);
		solver.logStats();
		
//		p.log("=== Find All Solutions:");
//		Solution[] solutions = solver.findAllSolutions();
//		for (int i = 0; i < solutions.length; i++) {
//			solutions[i].log();
//		}
//		p.log(vars);
		
		p.log("=== Find Optimal Solution:");
		//solver.traceExecution(true);
		Solution optimalSolution = solver.findOptimalSolution(Objective.MAXIMIZE,p.sum(vars));
		if (optimalSolution != null)
			optimalSolution.log();
		solver.logStats();
	}
}
