package org.jcp.jsr331.linear.samples;

//===============================================
//J A V A  C O M M U N I T Y  P R O C E S S
//
//J S R  3 3 1
//
// Compatibility Kit
//
//================================================

import javax.constraints.*;

public class GroupSizing {
	
	Problem p = ProblemFactory.newProblem("GroupSizing");
	
	int numberOfGroups = 12;
	int groupMin = 16;
	int groupMax = 19;
	int numberOfPeople = 210;
	
	public void define() {
		
		// Create groups variables
		p.log("Validation");
		Var[] groupSizeVars = new Var[numberOfGroups];
		for (int g = 0; g < numberOfGroups; g++) {
			groupSizeVars[g] = p.variable("Group"+g, groupMin, groupMax);
		}
		
		Var totalPeopleVar = p.sum("totalPeopleVar",groupSizeVars);
		p.post(totalPeopleVar,"=",numberOfPeople); 
	}

	// === Problem Resolution
	public void solve() {
		Solver solver = p.getSolver();
		Solution s = solver.findOptimalSolution(Objective.MAXIMIZE, p.getVar("totalPeopleVar"));
		if (s == null)
			p.log("Unable to derive a solution.");
		else {
			p.log("*** Optimal Solution ***");
			for (int g = 0; g < numberOfGroups; g++) {
				p.log("Group"+g +": " + s.getValue("Group"+g));
			}
		}
		solver.logStats();

	}

	public static void main(String[] args) {

		GroupSizing problem = new GroupSizing();
		problem.define();
		problem.solve();

	}
}