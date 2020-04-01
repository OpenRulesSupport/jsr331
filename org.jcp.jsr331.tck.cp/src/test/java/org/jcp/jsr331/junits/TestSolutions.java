//===============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// TestXYZ Compatibility Kit
// 
//================================================
package org.jcp.jsr331.junits;

import javax.constraints.Objective;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.SolutionIterator;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.Solution;

import junit.framework.*;
import junit.textui.TestRunner;

public class TestSolutions extends TestCase {

	public static void main(String[] args) {
		TestRunner.run(new TestSuite(TestSolutions.class));
	}
	
	public Problem defineCsp() {

		Problem p = ProblemFactory.newProblem("TestSolutions");
		//======= Define variables
		Var x = p.variable("X", 0, 10);
		Var y = p.variable("Y", 0, 10);
		Var z = p.variable("Z", 0, 10);
		//======= Define constraints
		p.post(x,"<",y); 		// X < Y
		p.post(x.plus(y),"=",z); 	// X + Y = Z
		p.post(y,">",5); 		// Y > 5
		// Add Cost as an objective
		Var cost = p.variable("Cost", 2, 25);
		// Cost = 3XY - 4Z
		p.post(cost,"=",x.multiply(3).multiply(y).minus(z.multiply(4))); 
		return p;
	}

	public void testOneSolution() {
		
		Problem problem = defineCsp();		
		problem.log("=== One solution:");
		Solver solver = problem.getSolver();
		Solution solution = solver.findSolution();
		if (solution == null)
			problem.log("No Solutions");
		else
			solution.log();
		problem.log("After Search");
		problem.log(problem.getVars());
		assertTrue(solution.getValue("X") == 2);
		assertTrue(solution.getValue("Y") == 6);
		assertTrue(solution.getValue("Z") == 8);
		assertTrue("testOneSolution: Invalid Cost", solution.getValue("Cost") == 4);
	}
	
	public void testAllSolutions() {

		Problem problem = defineCsp();				
		problem.log("=== All solutions:");
		Solver solver = problem.getSolver();
		solver.setMaxNumberOfSolutions(4);
		Solution[] solutions = solver.findAllSolutions();
		for(Solution sol : solutions) {
			sol.log();
		}
		assertTrue(solutions.length == 4);
	}
	
	public void testSolutionIterator() {

		Problem problem = defineCsp();				
		problem.log("=== Solution Iterator:");
		Solver solver = problem.getSolver();
		SolutionIterator iter = solver.solutionIterator();
		int n = 0;
		while(iter.hasNext()) {
			Solution solution = iter.next();
			solution.log();
			n++;
		}
		assertTrue(n == 5);
//		problem.log(problem.getVars());
//		testOneSolution();
	}
	
	public void testOptimalSolution() {

		Problem problem = defineCsp();				
		problem.log("=== Optimal Solution:");
		Solver solver = problem.getSolver();
		Var costVar = problem.getVar("Cost");
		Solution solution = solver.findOptimalSolution(Objective.MAXIMIZE, costVar);
		if (solution == null)
			problem.log("No Solutions");
		else
			solution.log();
		problem.log("Cost=" + solution.getValue("Cost"));
		assertTrue(solution.getValue("Cost") == 23);
//		problem.log(problem.getVars());
	}
}