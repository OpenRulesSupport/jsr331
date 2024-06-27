package org.jcp.jsr331.samples;

import javax.constraints.Var;
import javax.constraints.Solution;
import javax.constraints.impl.Problem;

/**
 * A map-coloring problem involves choosing colors for the countries on a map in
 * such a way that at most four colors are used and no two neighboring countries
 * are the same color. For our example, we will consider six countries: Belgium,
 * Denmark, France, Germany, Netherlands, and Luxembourg. The colors can be
 * blue, white, red or green but wtite color is missing. Use 3 colors and minimize
 * total constraint violation
 */

public class MapColoringWithViolations extends Problem {
	static final String[] colors = { "red", "green", "blue" };

	Var[] vars;
	Var   totalViolations;
	
	public void define() {
		try {
			// Variables
			int n = colors.length-1;
			Var Belgium = variable("Belgium",0, n);
			Var Denmark = variable("Denmark",0, n);
			Var France = variable("France",0, n);
			Var Germany = variable("Germany",0, n);
			Var Netherlands = variable("Netherland",0, n);
			Var Luxemburg = variable("Luxemburg",0, n);
			vars = new Var[]{ Belgium, Denmark, France, Germany, Netherlands, Luxemburg };
			// Hard Constraints
			post(France,"!=",Belgium);
			post(France,"!=",Germany);
			post(Belgium,"!=",Netherlands);
			post(Belgium,"!=",Germany);
			post(Germany,"!=",Netherlands);
			post(Germany,"!=",Denmark);
			
			// Soft Constraints
			Var[] weightVars = {
				linear(France,"=",Luxemburg).asBool().multiply(257),
				linear(Luxemburg,"=",Germany).asBool().multiply(9043),
				linear(Luxemburg,"=",Belgium).asBool().multiply(568)
			};
			// Optimization objective
			totalViolations = sum(weightVars);
			totalViolations.setName("Total Constraint Violations");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void solve() {
		Solution solution = getSolver().findOptimalSolution(totalViolations);
		if (solution != null) {
			solution.log();
			for (int i = 0; i < vars.length; i++) {
				String name = vars[i].getName();
				log(name + " - " + colors[solution.getValue(name)]);
			}
		}
		else
			log("no solution found");
	}

	public static void main(String[] args) {
		MapColoringWithViolations p = new MapColoringWithViolations();
		p.define();
		p.solve();
	}
}
