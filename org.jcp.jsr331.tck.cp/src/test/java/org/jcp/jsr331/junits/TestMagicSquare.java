//===============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// TestXYZ Compatibility Kit
// 
//================================================
package org.jcp.jsr331.junits;


import javax.constraints.ProblemFactory;
import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarSelectorType;
import javax.constraints.Problem;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;


/**
 * A magic square is a square matrix where the sum of every row,
 * column, and diagonal is equal to the same value; the numbers in the magic
 * square are consecutive and start with 1. 
 */

public class TestMagicSquare extends TestCase {

	public static void main(String[] args) {
		TestRunner.run(new TestSuite(TestMagicSquare.class));
	}
	
	public void testExecute() {
		int n = 3;
		int sum = n * (n * n + 1) / 2;

		// ========= Problem Representation ==================

		// create problem instance
		Problem pb = ProblemFactory.newProblem("Magic Square");
		// create all magic square elements
		Var[] vars = pb.variableArray("v", 1, n * n, n * n);
		// all elements must be unique
		pb.postAllDifferent(vars);

		// create arrays of rows and columns
		Var[][] rows = new Var[n][n];
		Var[][] columns = new Var[n][n];

		// create arrays for diagonals
		Var[] diagonal1 = new Var[n];
		Var[] diagonal2 = new Var[n];

		for (int i = 0; i < n; i++) {
			// populate the arrays
			for (int j = 0; j < n; j++) {
				rows[i][j] = vars[i * n + j];
				columns[i][j] = vars[j * n + i];
			}
			diagonal1[i] = vars[i * n + i];
			diagonal2[i] = vars[i * n + (n - i - 1)];
			// the i-th row and column are populated
			// impose constraint on their sums
			pb.post(rows[i], "=", sum);
			pb.post(columns[i],"=",sum);
		}

		// the diagonals are populated, let's impose constraint on their sums
		pb.post(diagonal1,"=",sum);
		pb.post(diagonal2,"=",sum);

		// ========= Problem Resolution ==================
		Solver solver = pb.getSolver();
		SearchStrategy strategy = solver.getSearchStrategy();
		strategy.setVars(vars);
		strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN);
		Solution solution = solver.findSolution();
		assertNotNull(solution);
		solution.log();
		assertEquals(solution.getValue("v-0"),2);
		assertEquals(solution.getValue("v-1"),7);
		assertEquals(solution.getValue("v-2"),6);
		assertEquals(solution.getValue("v-3"),9);
		assertEquals(solution.getValue("v-4"),5);
		assertEquals(solution.getValue("v-5"),1);
		assertEquals(solution.getValue("v-6"),4);
		assertEquals(solution.getValue("v-7"),3);
		assertEquals(solution.getValue("v-8"),8);
		// print the solution found
		for (int i = 0; i < n; i++) {
			String str = new String();
			for (int j = 0; j < n; j++) {
				int value = solution.getValue("v-"+(i * n + j));
				str = str + value + " ";
			}
			pb.log(str);
		}
	}
}