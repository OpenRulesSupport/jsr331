package org.jcp.jsr331.samples;

import javax.constraints.*;

/**
 * A map-coloring problem involves choosing colors for the countries on a map in
 * such a way that at most four colors are used and no two neighboring countries
 * are the same color. For our example, we will consider six countries: Belgium,
 * Denmark, France, Germany, Netherlands, and Luxembourg. The colors can be
 * blue, white, red or green.
 */

public class MapColoringStrings {

	Problem p = ProblemFactory.newProblem("MapColoringStrings");

	static final String[] colors = { "red", "green", "blue", "yellow" };

	public void define() {
		try {
			// Variables
			VarString Belgium = p.variableString("Belgium", colors);
			VarString Denmark = p.variableString("Denmark", colors);
			VarString France = p.variableString("France", colors);
			VarString Germany = p.variableString("Germany", colors);
			VarString Netherlands = p.variableString("Netherland", colors);
			VarString Luxemburg = p.variableString("Luxemburg", colors);
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
			
			for (int i = 0; i < p.getVars().length; i++) {
				VarString var = p.getVarStrings()[i];
				p.log(var.toString());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public Solution solve() {
		return p.getSolver().findSolution();
	}

	public void print(Solution solution) {
		p.log("SOLUTION:");
		for (int i = 0; i < p.getVars().length; i++) {
			VarString var = p.getVarStrings()[i];
			p.log(var.toString());
			//log(var.getName() + ": " +var.getValue());
		}
	}

	public static void main(String[] args) {
		MapColoringStrings p = new MapColoringStrings();
		p.define();
		Solution solution = p.solve();
		p.print(solution);
	}
}
