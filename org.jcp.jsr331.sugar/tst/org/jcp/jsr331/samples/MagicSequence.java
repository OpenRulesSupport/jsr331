//===============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// TestXYZ Compatibility Kit
// 
//================================================

package org.jcp.jsr331.samples;

import javax.constraints.*;

/**
 * A magic sequence is a sequence of N+1 values (x0, x1, ... xn) such that 0
 * will appear in the sequence x0 times, 1 will appear x1 times, 2 will appear
 * x2 times and so on. For example, the following sequence is a solution for
 * N=3: (1,2,1,0)
 */

public class MagicSequence {
	
	Problem p = ProblemFactory.newProblem("TestXYZ");

	int n;
	Var[] sequence;

	public MagicSequence(int n) {
		this.n = n;
	}

	public void define() {
		sequence = p.variableArray("sequence", 0, n, n + 1);
		int[] values = new int[n + 1];
		for (int i = 0; i < n + 1; i++) {
			values[i] = i;
		}
		try {
			p.postGlobalCardinality(sequence, values, sequence);
			// OR this loop
			// for (int i = 0; i < sequence.length; i++) {
			// constraintCardinality(sequence, i, "=", sequence[i]);
			// }
		} catch (Exception e) {
			p.log("exception is thrown during GlobalCardinality()" + e);
		}

		int[] coeffs = new int[n + 1];
		for (int i = 0; i < coeffs.length; i++) {
			coeffs[i] = i;
		}
		Var scalProd = p.scalProd(coeffs, sequence);
		p.post(scalProd, "=", n + 1);

	}

	public void solve() {
		// Find a solution
		Solver solver = p.getSolver();
		SearchStrategy strategy = solver.getSearchStrategy();
		strategy.setVars(sequence);
		strategy.setVarSelectorType(VarSelectorType.MAX_VALUE);
		Solution solution = solver.findSolution();
		if (solution == null)
			p.log("No Solutions");
		else {
//			StringBuffer str = new StringBuffer();
//			for (int i = 0; i < sequence.length; i++) {
//				int value = solution.getValue("sequence-" + i);
//				str.append(value + " ");
//			}
//			log(str.toString());
			solution.log();
			solver.logStats();
		}
	}

	public static void main(String[] args) {
		String arg = (args.length == 0) ? "3" : args[0];
		int n = Integer.parseInt(arg);
		MagicSequence p = new MagicSequence(n);
		p.define();
		p.solve();
	}
}
