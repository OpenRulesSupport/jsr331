package org.jcp.jsr331.groovy;

import javax.constraints.Solution
import javax.constraints.impl.Var
import javax.constraints.Constraint
import javax.constraints.impl.Problem
import javax.constraints.impl.constraint.Linear
import javax.constraints.impl.search.Solver

import org.jcp.jsr331.groovy.Post

//public class Test {
	
def post = { left,oper,right -> new Linear(left,oper,right).post() }
	
//	static main(args) {
		// PROBLEM DEFINITION
		Problem p = new Problem("Test");
		//======= Define variables
		int[] domain = [2,3,4,5,6,7,9,10];
		Var x = p.variable("X", domain);
		Var y = p.variable("Y", domain);
		Var z = p.variable("Z", domain);
		//======= Define and post constraints 
		try {
			p.post(x,"<",y) 			// X < Y
			p.post(z,">",7) 			// Z > 7
			p.post(x.plus(y),"=",z); 	// X + Y = Z
			//new Linear(x+y, "=", z).post();
			// (x+y=z).post();
			
			Var[] vars   = [ x, y, z ];
			println "After posting 1"
			vars.each{ println it }
			
//				int[] values = [ 3, 4, -7 ];
//				new Linear(values,vars, Oper.LT, 16).post(); // 3X + 4Y - 7Z < 16
//				new Linear(x*3+y*4-z*7, Oper.LT, 16).post(); // 3X + 4Y - 7Z < 16
			p.post(x*3+y*4-z*7, "<", 16)
		
			// post("x*3+y*4-z*7 < 16");
			
			println "After posting 2"
			vars.each{ println it }
			
		} catch (Exception e) {
			println("Error posting constraints: " + e);
			System.exit(-1);
		}
		
		
		
		// PROBLEM RESOLUTION
		println "=== Find One solution:"
		Solver solver = new Solver(p);
		def solution = solver.findSolution(); 
		if (solution != null)
			solution.log();
		else
			p.log("No Solutions");
		solver.logStats();
//	}

//}
