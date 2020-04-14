//===============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// TestXYZ Compatibility Kit
// 
//================================================
package org.jcp.jsr331.tests;


import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.Problem;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TestCardinality extends TestCase {

	public TestCardinality(String name) {
		super(name);
	}

	public static void main(String[] args) {
		TestRunner.run(new TestSuite(TestCardinality.class));
	}

	public void testExecute1() {
		Problem p = ProblemFactory.newProblem("TestCardinality1");
		p.log(p.getName());
		try {
			int n = 5;
			Var[] vars = new Var[n];
			for (int i = 0; i < vars.length; i++)
				vars[i] = p.variable("v" + i, 0, n - 1);

			Var[] cardinalities = p.variableArray("cardinalities", 0, n, n); 
			for (int i = 0; i < cardinalities.length; i++) {
				p.postCardinality(vars, i, "=", cardinalities[i]);
			}
			
			p.post(cardinalities[1],"=",2);
			p.post(cardinalities[2],"=",3);

			Solver solver = p.getSolver();
			Solution solution = solver.findSolution();
			if (solution != null)
				solution.log();
			else
				p.log("No solutions");

			int count1 = 0;
			int count2 = 0;
			for (int i = 0; i < vars.length; i++) {
				if (solution.getValue("v"+i) == 1)
					count1++;
				if (solution.getValue("v"+i) == 2)
					count2++;
			}
			
			assertEquals(count1,2);
			assertEquals(count2,3);
			p.log("Success " + p.getName());

		} catch (Exception e) {
			p.log("FAILURE");
			fail("test failed!");
		}
	}

	public void testExecute2() {
		Problem problem = ProblemFactory.newProblem("TestCardinality2");
		problem.log(problem.getName());
		try {
			int n = 5;
			Var[] vars = new Var[n];
			for (int i = 0; i < vars.length; i++) {
				vars[i] = problem.variable("v" + i, 0, n - 1);
			}
			problem.postCardinality(vars, 1, "=", 2);
			Var valueVar = problem.variable("valueVar", 3, 4);
			problem.postCardinality(vars, 2, "=", valueVar);
			

			Solver solver = problem.getSolver();
			Solution solution = solver.findSolution();
			if (solution != null) {
				solution.log();
			}
			else
				problem.log("No solutions");
			
			
			int count1 = 0;
			int count2 = 0;
			int actualValueVar = solution.getValue("valueVar");
			for (int i = 0; i < vars.length; i++) {
				if (solution.getValue("v"+i) == 1)
					count1++;
				if (solution.getValue("v"+i) == 2)
					count2++;
			}
			
			assertEquals(count1,2);
			assertEquals(count2,actualValueVar);
			problem.log("Success " + problem.getName());

		} catch (Exception e) {
			problem.log("FAILURE");
			fail("test failed!");
		}
	}

	public void testExecute3() {
		Problem problem = ProblemFactory.newProblem("TestCardinality3");
		problem.log(problem.getName());
		try {
			int n = 6;
			Var[] vars = problem.variableArray("var", 0, n - 1, n);

			Var[] valueVars = problem.variableArray("count", 0, n - 1, n);
			for (int i = 0; i < valueVars.length; i++) {
				problem.postCardinality(vars, i, "=", valueVars[i]);
			}
			
			problem.postAllDifferent(vars);

			Solver solver = problem.getSolver();
			Solution solution = solver.findSolution();
			if (solution != null) {
				solution.log();
				problem.log(valueVars);
			}
			else
				problem.log("No solutions");

			assertEquals(solution.getValue("count-0"),1);
			assertEquals(solution.getValue("count-1"),1);
			assertEquals(solution.getValue("count-2"),1);
			assertEquals(solution.getValue("count-3"),1);
			assertEquals(solution.getValue("count-4"),1);
			assertEquals(solution.getValue("count-5"),1);
			problem.log("Success " + problem.getName());

		} catch (Exception e) {
			problem.log("FAILURE " + problem.getName());
			fail(problem.getName() + " failed!");
		}
	}
}