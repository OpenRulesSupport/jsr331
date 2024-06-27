package org.jcp.jsr331.samples;

import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarSelector.VarSelectorType;
import javax.constraints.impl.Problem;

/**
 * A magic square is a square matrix where the sum of every row, column, and
 * diagonal is equal to the same value; the numbers in the magic square are
 * consecutive and start with 1.
 */

public class MagicSquare extends Problem {

	int n;
	Var[] vars;

	public MagicSquare(int n) {
		this.n = n;
	}

	public void define() {
		int sum = n * (n * n + 1) / 2;
		// create all magic square elements
		vars = variableArray("vars", 1, n * n, n * n);
		// all elements must be unique
		postAllDifferent(vars);

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
			post(rows[i], "=", sum);
			post(columns[i], "=", sum);
		}

		// the diagonals are populated, let's impose constraint on their sums
		post(diagonal1, "=", sum);
		post(diagonal2, "=", sum);
	}

	public void solve() {
		Solver solver = getSolver();
		SearchStrategy strategy = solver.getSearchStrategy();
		strategy.setVars(vars);
		strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN);
		Solution solution = solver.findSolution();
		if (solution == null)
			log("No Solutions");
		else {
			// print the solution found
			for (int i = 0; i < n; i++) {
				String str = new String();
				for (int j = 0; j < n; j++) {
					int value = solution.getValue("vars-" + (i * n + j));
					str += value + " ";
				}
				log(str);
			}
		}
		solver.logStats();
	}

	public static void main(String[] args) {
		String arg = (args.length == 0) ? "5" : args[0];
		int n = Integer.parseInt(arg);
		MagicSquare p = new MagicSquare(n);
		p.define();
		p.solve();
	}
}