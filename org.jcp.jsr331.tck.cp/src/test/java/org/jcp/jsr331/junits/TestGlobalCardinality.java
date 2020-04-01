//===============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// TestXYZ Compatibility Kit
// 
//================================================
package org.jcp.jsr331.junits;

import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TestGlobalCardinality extends TestCase {

	public TestGlobalCardinality(String name) {
		super(name);
	}

	public static void main(String[] args) {
		TestRunner.run(new TestSuite(TestGlobalCardinality.class));
	}

	public void testExecute1() {
		Problem problem = ProblemFactory.newProblem("TestGlobalCardinality1");
		problem.log(problem.getName());
		try {
			int n = 3;
			Var[] vars = problem.variableArray("var", 0, n-1, n);
			int[] values = new int[n];
			for (int i = 0; i < values.length; i++) {
				values[i] = i;
			}			
			Var[] cardinalityVars = problem.variableArray("cards",0,n-1,n);
			problem.postGlobalCardinality(vars, values, cardinalityVars);
			problem.log(vars);
			problem.log(cardinalityVars);
			
			Solver solver = problem.getSolver();
			solver.getSearchStrategy().setVars(vars);
			Solution solution = solver.findSolution();
			assertNotNull(solution);
			assertEquals(solution.getValue("var-0"),0);
			assertEquals(solution.getValue("var-1"),0);
			assertEquals(solution.getValue("var-2"),1);
			if (solution != null) {
				solution.log();
				problem.log(cardinalityVars);
			}
			else
				problem.log("No solutions");
			
			problem.log("Success "+problem.getName());
			
		} catch (Exception e) {
			problem.log("FAILURE "+problem.getName());
			fail(problem.getName()+ " failed!");
		}
	}
	
	
//	/** the following test failed with constrainer
//	public void testExecute2() {
//		Problem problem = new Problem("TestGlobalCardinality2");
//		problem.log(problem.getName());
//		try {
//			int n = 3;
//			Var[] vars = problem.varArray("var", 0, n-1, n);
//			Var[] valueVars = problem.varArray("valueVar", 0, n-1, n); 			
//			Var[] cardinalityVars = problem.varArray("cards",0,n-1,n);
//			problem.globalCardinality(vars, cardinalityVars, valueVars);
//			problem.log(vars);
//			problem.log(valueVars);
//			problem.log(cardinalityVars);
//			
//			Solver solver = problem.getSolver();
//			Goal goal1 = new GoalAssignValues(valueVars); //,new ValueSelectorMax());
//			Goal goal2 = new GoalAssignValues(vars);
//			solver.setGoal(goal1.and(goal2));
//			solver.trace(valueVars);
//			solver.trace(vars);
//			solver.traceFailures(true);
//			Solution solution = solver.findSolution();
//			assertNotNull(solution);
//			assertEquals(solution.getValue("var-0"),0);
//			assertEquals(solution.getValue("var-1"),0);
//			assertEquals(solution.getValue("var-2"),1);
//			assertEquals(solution.getValue("valueVar-0"),2);
//			assertEquals(solution.getValue("valueVar-1"),1);
//			assertEquals(solution.getValue("valueVar-2"),0);
//			if (solution != null) {
//				solution.log();
//				problem.log(cardinalityVars);
//			}
//			else
//				problem.log("No solutions");
//			
//			problem.log("Success "+problem.getName());
//			
//		} catch (Exception e) {
//			problem.log("FAILURE "+problem.getName());
//			fail(problem.getName()+ " failed!");
//		}
//	}
	

}