package org.jcp.jsr331.samples;

import javax.constraints.Var;
import javax.constraints.Solution;
import javax.constraints.impl.Problem;

/**
 * A map-coloring problem involves choosing colors for the countries on a map in
 * such a way that at most four colors are used and no two neighboring countries
 * are the same color. For our example, we will consider six countries: Belgium,
 * Denmark, France, Germany, Netherlands, and Luxembourg. The colors can be
 * blue, white, red or green.
 */

public class MapColoring extends Problem {

	static final String[] colors = { "red", "green", "blue", "yellow" };

	public void define() {
		try {
			// Variables
			int n = colors.length;
			Var Belgium = variable("Belgium", 0, n);
			Var Denmark = variable("Denmark", 0, n);
			Var France = variable("France", 0, n);
			Var Germany = variable("Germany", 0, n);
			Var Netherlands = variable("Netherland", 0, n);
			Var Luxemburg = variable("Luxemburg", 0, n);
			// Constraints
			post(France, "!=", Belgium);
			post(France, "!=", Luxemburg);
			post(France, "!=", Germany);
			post(Luxemburg, "!=", Germany);
			post(Luxemburg, "!=", Belgium);
			post(Belgium, "!=", Netherlands);
			post(Belgium, "!=", Germany);
			post(Germany, "!=", Netherlands);
			post(Germany, "!=", Denmark);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void solve() {
		Solution solution = getSolver().findSolution();
		if (solution != null) {
			solution.log();
			for (int i = 0; i < getVars().length; i++) {
				Var var = getVars()[i];
				log(var.getName() + " - "
						+ colors[solution.getValue(var.getName())]);
			}
		} else
			log("no solution found");
	}

	public static void main(String[] args) {
		MapColoring p = new MapColoring();
		p.define();
		p.solve();
	}
}
