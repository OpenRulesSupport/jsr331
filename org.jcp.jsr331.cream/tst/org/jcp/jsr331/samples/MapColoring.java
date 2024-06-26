package org.jcp.jsr331.samples;

import javax.constraints.*;

/**
 * A map-coloring problem involves choosing colors for the countries on a map in
 * such a way that at most four colors are used and no two neighboring countries
 * are the same color. For our example, we will consider six countries: Belgium,
 * Denmark, France, Germany, Netherlands, and Luxembourg. The colors can be
 * blue, white, red or green.
 */

public class MapColoring {
	
	Problem p = ProblemFactory.newProblem("TestXYZ");

	static final String[] colors = { "red", "green", "blue", "yellow" };

	public void define() {
		try {
			// Variables
			int n = colors.length;
			Var Belgium = p.variable("Belgium", 0, n);
			Var Denmark = p.variable("Denmark", 0, n);
			Var France  = p.variable("France", 0, n);
			Var Germany = p.variable("Germany", 0, n);
			Var Netherlands = p.variable("Netherland", 0, n);
			Var Luxemburg = p.variable("Luxemburg", 0, n);
			// Constraints
			p.post(France, "!=", Belgium);
			p.post(France, "!=", Luxemburg);
			p.post(France, "!=", Germany);
			p.post(Luxemburg, "!=", Germany);
			p.post(Luxemburg, "!=", Belgium);
			p.post(Belgium, "!=", Netherlands);
			p.post(Belgium, "!=", Germany);
			p.post(Germany, "!=", Netherlands);
			p.post(Germany, "!=", Denmark);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public void solve() {
		Solution solution = p.getSolver().findSolution();
		if (solution != null) {
			solution.log();
			for (int i = 0; i < p.getVars().length; i++) {
				Var var = p.getVars()[i];
				p.log(var.getName() + " - "
						+ colors[solution.getValue(var.getName())]);
			}
		} else
			p.log("no solution found");
	}

	public static void main(String[] args) {
		MapColoring mc = new MapColoring();
		mc.define();
		mc.solve();
	}
}
