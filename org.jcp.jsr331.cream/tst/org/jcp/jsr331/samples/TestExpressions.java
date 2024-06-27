package org.jcp.jsr331.samples;


import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.Problem;

public class TestExpressions {

	public static void main(String[] args) {
		//==== PROBLEM DEFINITION ==============================
		Problem p = ProblemFactory.newProblem("TestXYZ");
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
			Var scalProd1 = p.scalProd(coef1,vars);
			p.post(scalProd1, ">", 0); 
			
			// x + y + z + r >= 15
			p.post(p.sum(vars), ">=", 15);
			
			// 2x - 4y + 5z - r > x*y
			int[] coef2 = { 2, -4, 5, -1 };
			Var scalProd2 = p.scalProd(coef2,vars);
			p.post(scalProd2, ">", x.multiply(y)); 
			
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