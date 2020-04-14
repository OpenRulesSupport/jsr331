package org.jcp.jsr331.samples;

import javax.constraints.*;

public class TestIfThen {

	public static void main(String[] args) {
		Problem p = ProblemFactory.newProblem("ifthen");
		Var a = p.variable("a",0, 10);
		Var b = p.variable("b",0, 10);
		 
		Constraint c1 = p.linear(a,"=",3);
		Var aBool = c1.asBool();
		Constraint c2 = p.linear(b,"=",7);
		Var bBool = c2.asBool();
		c1.implies(c2).post();
		c1.post();
		 
		p.add("aBool",aBool);
		p.add("bBool",bBool);
		             
		Solver solver = p.getSolver();
		Solution solution = solver.findSolution();
		solution.log();
	}
}
