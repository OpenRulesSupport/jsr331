package org.jcp.jsr331.hakan;

/**
 *
 *   Who killed agatha? (The Dreadsbury Mansion Murder Mystery) in Choco.

 *   This is a standard benchmark for theorem proving.  
 *   http://www.lsv.ens-cachan.fr/~goubault/H1.dist/H1.1/Doc/h1003.html
 *   """ 
 *   Someone in Dreadsbury Mansion killed Aunt Agatha. 
 *   Agatha, the butler, and Charles live in Dreadsbury Mansion, and 
 *   are the only ones to live there. A killer always hates, and is no 
 *   richer than his victim. Charles hates noone that Agatha hates. Agatha 
 *   hates everybody except the butler. The butler hates everyone not richer 
 *   than Aunt Agatha. The butler hates everyone whom Agatha hates. 
 *   Noone hates everyone. Who killed Agatha? 
 *   """

 *   Originally from 
 *   F. J. Pelletier: Seventy-five problems for testing automatic theorem provers. Journal of Automated Reasoning, 2: 191â€“216, 1986.

 *   Compare with the following models:
 *   - MiniZinc: http://www.hakank.org/minizinc/who_killed_agatha.mzn
 *   - Comet: http://www.hakank.org/comet/who_killed_agatha.mzn
 *   - Gecode: http://www.hakank.org/gecode/who_killed_agatha.cpp

 * 
 * This Choco model was created by Hakan Kjellerstrand (hakank@bonetmail.com)
 * Also, see his Choco page: http://www.hakank.org/choco/ 
 *
 */

import javax.constraints.Constraint;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.Problem;

/*
 This version use the nth constraint (Element) and accordingly 
 transposed hates/richer matrices.

 */

public class WhoKilledAgatha {
	
	static String[] names = { "agatha", "butler", "charles" };

	public void solve() {

		int n = 3;
		Problem p = ProblemFactory.newProblem("Agatha");

		Var the_killer = p.variable("the_killer", 0, n - 1);

		int agatha = 0;
		int butler = 1;
		int charles = 2;

		// constants for nth
		Var zero = p.variable("zero", 0, 0);
		Var one = p.variable("one", 1, 1);

		Var[][] hates = new Var[n][n];
		Var[][] richer = new Var[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				hates[i][j] = p.variable("hates" + i + "" + j, 0, 1);
				richer[i][j] = p.variable("richer" + i + "" + j, 0, 1);
			}
		}

		//
		// The comments below contains the corresponding MiniZinc code,
		// for documentation and comparision.
		//

		// """
		// Agatha, the butler, and Charles live in Dreadsbury Mansion, and
		// are the only ones to live there.
		// """

		// "A killer always hates, and is no richer than his victim."
		// MiniZinc: hates[the_killer, the_victim] = 1
		// richer[the_killer, the_victim] = 0
		// Note: I cannot get nth to work here...
		for (int i = 0; i < n; i++) {
			p.postElement(hates[agatha], the_killer, "=", one);
			p.postElement(richer[agatha], the_killer, "=", zero);
		}

		// define the concept of richer:
		// a) no one is richer than him-/herself
		for (int i = 0; i < n; i++) {
			p.post(richer[i][i], "=", 0);
		}

		// (contd...)
		// b) if i is richer than j then j is not richer than i
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					// MiniZinc: richer[i,j] == 1 <-> richer[j,i] == 0
					Constraint c1 = p.linear(richer[j][i], "=", 1);
					Constraint c2 = p.linear(richer[i][j], "=", 0);
					p.postIfThen(c1, c2);
					p.postIfThen(c2, c1);
				}
			}
		}

		// "Charles hates no one that Agatha hates."
		for (int i = 0; i < n; i++) {
			// MiniZinc: hates[agatha, i] = 1 -> hates[charles, i] = 0
			Constraint c1 = p.linear(hates[i][agatha], "=", 1);
			Constraint c2 = p.linear(hates[i][charles], "=", 0);
			p.postIfThen(c1, c2);
		}

		// "Agatha hates everybody except the butler. "
		p.post(hates[charles][agatha], "=", 1);
		p.post(hates[agatha][agatha], "=", 1);
		p.post(hates[butler][agatha], "=", 0);

		// "The butler hates everyone not richer than Aunt Agatha. "
		for (int i = 0; i < n; i++) {
			// MiniZinc: richer[i, agatha] = 0 -> hates[butler, i] = 1
			p.postIfThen(p.linear(richer[agatha][i], "=", 0),
							   p.linear(hates[i][butler], "=", 1) );
		}

		// "The butler hates everyone whom Agatha hates."
		for (int i = 0; i < n; i++) {
			// MiniZinc: hates[agatha, i] = 1 -> hates[butler, i] = 1
			p.postIfThen(p.linear(hates[i][agatha], "=", 1), 
							   p.linear(hates[i][butler], "=", 1));
		}

		// "No one hates everyone. "
		for (int i = 0; i < n; i++) {
			// MiniZinc: sum(j in r) (hates[i,j]) <= 2
			// IntegerVariable a[] = makeIntVarArray("a", n, 0,1);
			Var[] a = p.variableArray("a", 0, 1, n);
			for (int j = 0; j < n; j++) {
				a[j] = hates[j][i];
			}
			p.post(p.sum(a), "<=", 2);

		}

		// "Who killed Agatha?"
		// partition.constraint(the_victim, "=", agatha);

		Solver solver = p.getSolver();

		Solution s = solver.findSolution();

		if (s != null) {
			p.log("Solution: the killer is " + names[s.getValue("the_killer")]);
		} else {
			p.log("Problem is not feasible.");
		}
		solver.logStats();

	} // end model

	public static void main(String args[]) {
		WhoKilledAgatha t = new WhoKilledAgatha();
		t.solve();

	} // end main

} // end class

