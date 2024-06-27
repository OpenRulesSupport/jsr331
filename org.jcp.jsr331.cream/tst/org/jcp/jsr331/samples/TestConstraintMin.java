package org.jcp.jsr331.samples;


import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.Problem;
import javax.constraints.impl.constraint.ConstraintMin;

public class TestConstraintMin {

	public static void main(String[] args) {
		// PROBLEM DEFINITION
		Problem p = ProblemFactory.newProblem("TestConstraintMin");
		//======= Define variables
		Var x = p.variable("X", 0, 10);
		Var y = p.variable("Y", 5, 20);
		Var z = p.variable("Z", 3, 15);
		Var[] vars = { x, y, z };
		//======= Define and post constraints 
		new ConstraintMin(vars, ">", 3).post();
		p.post(x,">",3);
		p.post(y,"<",7);
		// PROBLEM RESOLUTION
		p.log("=== Find One solution:");
		Solver solver = p.getSolver();
		Solution solution = solver.findSolution(); 
		if (solution != null)
			solution.log();
		else
			p.log("No Solutions");
	}
}