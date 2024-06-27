package org.jcp.jsr331.samples;

/*
 * These problems are said to have many practical applications
 * including sensor placements for x-ray crystallography and
 * radio astronomy. A Golomb ruler may be defined as a set of m
 * integers 0 = a_1 < a_2 < ... < a_m such that the m(m-1)/2 differences
 * a_j - a_i, 1 <= i < j <= m are distinct.
 * Such a ruler is said to contain m marks and is of length a_m.
 * The objective is to find optimal (minimum length) or near optimal rulers.
 *
 * Note that a symmetry can be removed by adding the constraint that
 * a_2 - a_1 < a_m - a_{m-1}, the first difference is less than the last.
 */

import javax.constraints.SearchStrategy;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.VarSelector.VarSelectorType;
import javax.constraints.impl.Problem;

public class GolombRulers extends Problem {

	int marks = 9;
	int domain = 45;
	Var[] originalVars, diffVars;

	public GolombRulers(int marks, int domain) {
		this.marks = marks;
		this.domain = domain;
	}

	public void define() {
		originalVars = variableArray("originalVars", 0, domain - 1, marks);
		diffVars = variableArray("diffs", 0, domain - 1,
				(marks * (marks - 1)) / 2);

		for (int i = 0; i < (marks - 1); i++)
			post(originalVars[i], "<", originalVars[i + 1]);
		post(originalVars[0], "=", 0);
		post(originalVars[marks - 1], "=", domain - 1);

		int counter = 0;
		for (int i = 0; i < (marks - 1); i++) {
			for (int j = i + 1; j < marks; j++) {
				post(originalVars[j].minus(originalVars[i]), "=",
						diffVars[counter]);
				counter++;
			}
		}
		postAllDifferent(diffVars);
		// to avoid symmetry
		post(diffVars[0], "<", diffVars[diffVars.length - 1]);
	}

	public void solve() {
		Solver solver = getSolver();
		SearchStrategy strategy = solver.getSearchStrategy();
		strategy.setVars(originalVars);
		strategy.setVarSelectorType(VarSelectorType.MIN_DOMAIN);
		// strategy.setValueSelector(new ValueSelectorMax());
		Solution solution = solver.findSolution();
		if (solution != null) {
			StringBuffer str1 = new StringBuffer();
			StringBuffer str2 = new StringBuffer();
			for (int i = 0; i < marks; i++) {
				int value = solution.getValue("originalVars-" + i);
				str1.append(value + " ");
			}
			for (int i = 0; i < diffVars.length; i++) {
				// int value = solution.getValue("diffs-"+i);
				// str2.append(value + " ");
				str2.append(diffVars[i] + " ");
			}
			log("originalVars: ");
			log(str1.toString());
			log("diffs: ");
			log(str2.toString());
		} else
			log("no solution found");
		solver.logStats();
	}

	public static void main(String[] args) {
		GolombRulers p = new GolombRulers(9, 45);
		p.define();
		p.solve();
	}
}
