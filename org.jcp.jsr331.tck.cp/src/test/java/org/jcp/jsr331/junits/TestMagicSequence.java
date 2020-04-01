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
 * A magic sequence is a sequence of N+1 values (x0, x1, ... xn) such that 0 will
 * appear in the sequence x0 times, 1 will appear x1 times, 2 will appear x2
 * times and so on. 
 * For example, the following sequence is a solution for N=3: (1,2,1,0)
 */
//===============================================
//J A V A  C O M M U N I T Y  P R O C E S S
//
//J S R  3 3 1
//
//TestXYZ Compatibility Kit
//
//================================================
public class TestMagicSequence extends TestCase {

	public static void main(String[] args) {
		TestRunner.run(new TestSuite(TestMagicSequence.class));
	}
	
	public void testExecute() {
		Problem p = ProblemFactory.newProblem("MagicSequence");
		int N = 3;
		Var[] sequence = p.variableArray("sequence", 0, N, N + 1);
		int[] values = new int[N+1];
		for (int i = 0; i < N+1; i++) {
			values[i] = i;
		}
		try{
			p.postGlobalCardinality(sequence, values, sequence);
//			// OR this loop
//			for (int i = 0; i < sequence.length; i++) {
//				problem.constraintCardinality(sequence, i, "=", sequence[i]);
//			}
		}catch (Exception e) {
			p.log("exception is thrown during globalCardinality()" + e);
		}
		int[] coeffs = new int[N + 1];
		for (int i = 0; i < coeffs.length; i++) {
			coeffs[i] = i;
		}
		p.post(coeffs, sequence, "=", N+1);

		// Find a solution
		Solver solver = p.getSolver();
		SearchStrategy strategy = solver.getSearchStrategy();
		strategy.setVars(sequence);
		strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN);
		Solution solution = solver.findSolution();
		assertNotNull(solution);
		assertEquals(solution.getValue("sequence-0"),1);
		assertEquals(solution.getValue("sequence-1"),2);
		assertEquals(solution.getValue("sequence-2"),1);
		assertEquals(solution.getValue("sequence-3"),0);
		solution.log();
	}
}
