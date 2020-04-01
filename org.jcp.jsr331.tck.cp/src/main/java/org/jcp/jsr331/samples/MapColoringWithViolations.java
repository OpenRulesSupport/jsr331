package org.jcp.jsr331.samples;

import javax.constraints.*;

/**
 * A map-coloring problem involves choosing colors for the countries on a map in
 * such a way that at most four colors are used and no two neighboring countries
 * are the same color. For our example, we will consider six countries: Belgium,
 * Denmark, France, Germany, Netherlands, and Luxembourg. The colors can be
 * blue, white, red or green but wtite color is missing. Use 3 colors and minimize
 * total constraint violation
 */

public class MapColoringWithViolations {
	
	Problem p = ProblemFactory.newProblem("TestXYZ");
	
	static final String[] colors = { "red", "green", "blue" };

	Var[] vars;
	Var   totalViolations;
	
	public void define() {
		try {
			// Variables
			int n = colors.length-1;
			Var Belgium = p.variable("Belgium",0, n);
			Var Denmark = p.variable("Denmark",0, n);
			Var France  = p.variable("France",0, n);
			Var Germany = p.variable("Germany",0, n);
			Var Netherlands = p.variable("Netherland",0, n);
			Var Luxembourg = p.variable("Luxembourg",0, n);
			vars = new Var[]{ Belgium, Denmark, France, Germany, Netherlands, Luxembourg };
			// Hard Constraints
			p.post(France,"!=",Belgium);
			p.post(France,"!=",Germany);
			p.post(Belgium,"!=",Netherlands);
			p.post(Belgium,"!=",Germany);
			p.post(Germany,"!=",Netherlands);
			p.post(Germany,"!=",Denmark);
			
			// Soft Constraints
			Var[] weightVars = {
				p.linear(France,"=",Luxembourg).asBool().multiply(257),
				p.linear(Luxembourg,"=",Germany).asBool().multiply(9043),
				p.linear(Luxembourg,"=",Belgium).asBool().multiply(568)
			};
			// Optimization objective
			totalViolations = p.sum(weightVars);
			totalViolations.setName("Total Constraint Violations");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void solve() {
		Solution solution = p.getSolver().findOptimalSolution(totalViolations);
		if (solution != null) {
			solution.log();
			for (int i = 0; i < vars.length; i++) {
				String name = vars[i].getName();
				p.log(name + " - " + colors[solution.getValue(name)]);
			}
		}
		else
			p.log("no solution found");
	}

	public static void main(String[] args) {
		MapColoringWithViolations mc = new MapColoringWithViolations();
		mc.define();
		mc.solve();
	}
}
