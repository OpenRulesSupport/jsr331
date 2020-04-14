//===============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// TestXYZ Compatibility Kit
// 
//================================================
package org.jcp.jsr331.tests;

/* ------------------------------------------------------------
 The eight-queens problem is a well known problem that involves
 placing eight queens on a chess board in such a way that none
 of them can capture any other using the conventional moves
 allowed to a queen.  In other words, the problem is to select
 eight squares on a chess board so that any pair of selected
 squares is never aligned vertically, horizontally, nor
 diagonally.
 ------------------------------------------------------------ */
import javax.constraints.DomainType;
import javax.constraints.ProblemFactory;
import javax.constraints.ProblemState;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Var;
import javax.constraints.VarSelectorType;
import javax.constraints.Problem;
import javax.constraints.Solver;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

public class TestQueens extends TestCase {

	public static void main(String[] args) {
		TestRunner.run(new TestSuite(TestQueens.class));
	}
	
	public void testExecute() {
		//========= Problem Representation ==================
		Problem problem = ProblemFactory.newProblem("Queens");
		int size = 8;
		problem.log("Queens " + size);

		// create 3 arrays of variables
		Var[] x = problem.variableArray("x", 0, size-1, size);
		Var[] x1 = new Var[size];
		Var[] x2 = new Var[size];
		problem.setDomainType(DomainType.DOMAIN_SMALL);
		for (int i = 0; i < size; i++) {
			x1[i] = x[i].plus(i);
			x2[i] = x[i].minus(i);
		}

		// post "all different" constraints
		problem.postAllDifferent(x);
		problem.postAllDifferent(x1);
		problem.postAllDifferent(x2);

		//========= Problem Resolution ==================
		// Find a solution
		Solver solver = problem.getSolver();
		solver.setTimeLimit(600000);
//		solver.setSearchStrategy(new GoalAssignValuesTimeLimit(x, 
//				                                     new VarSelectorMinSizeMin(x), 
//				                                     new ValueSelectorMin()));
		SearchStrategy strategy = solver.getSearchStrategy();
		strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN_MIN_VALUE);
//		solver.traceExecution(true);
//		solver.traceFailures(true);
		Solution solution = solver.findSolution(ProblemState.RESTORE);
		assertNotNull(solution);
		solution.log();
//		assertEquals(x[0].getValue(),0);
//		assertEquals(x[1].getValue(),6);
//		assertEquals(x[2].getValue(),3);
//		assertEquals(x[3].getValue(),5);
//		assertEquals(x[4].getValue(),7);
//		assertEquals(x[5].getValue(),1);
//		assertEquals(x[6].getValue(),4);
//		assertEquals(x[7].getValue(),2);
		
		Solution[] solutions = solver.findAllSolutions();
		problem.log("Total number of solutions: " + solutions.length);
		for (int i = 0; i < solutions.length; i++) {
			solutions[i].log();
		}
		assertEquals(solutions.length,92);
	}
}
