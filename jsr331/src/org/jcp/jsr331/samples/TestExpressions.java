package org.jcp.jsr331.samples;


import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.impl.Problem;

public class TestExpressions {

	public static void main(String[] args) {
		//==== PROBLEM DEFINITION ==============================
		Problem p = new Problem("Test");
		//======= Define variables
		Var x = p.variable("X",1,10);
		Var y = p.variable("Y",1,10);
		Var z = p.variable("Z",1,10);
		Var r = p.variable("R",1,10);
		Var[] vars   = { x, y, z, r };
		//======= Define and post constraints 
		try {
			// X < Y
			p.post(x,"<",y); 
			// X + Y = Z
			p.post(x.plus(y),"=",z); 
			
			p.postAllDifferent(vars);
			
			// 3x + 4y -7z + 2r > 0
			int[] coef1 = { 3, 4, -7, 2 };
			p.post(coef1,vars, ">", 0); 
			
			// x + y + z + r >= 15
			p.post(vars, ">=", 15);
			
			// 2x - 4y + 5z - r > x*y
			int[] coef2 = { 2, -4, 5, -1 };
			p.post(coef2,vars, ">", x.multiply(y)); 
			
		} catch (Exception e) {
			p.log("Error posting constraints: " + e);
			System.exit(-1);
		}
		p.log(vars);
		//=== PROBLEM RESOLUTION ================================
		p.log("=== Find Solution:");
		Solver solver = p.getSolver();
		Solution solution = solver.findSolution(); 
		if (solution != null)
			solution.log();
		else
			p.log("No Solutions");
		solver.logStats();
	}
}