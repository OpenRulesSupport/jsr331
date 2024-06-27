package balanced.assignment;

import java.util.Set;

import javax.constraints.*;
import javax.constraints.impl.search.selectors.VarSelectorRandom;

import com.openrules.csp.OptimizationProblem;

public class Optimization extends OptimizationProblem {

	BusinessProblem businessProblem;
	Person[] people;
	Group[] groups;

	public Optimization(BusinessProblem businessProblem) {
		this.businessProblem = businessProblem;
	}
	
	public void define() { // PROBLEM DEFINITION
		
		// Define assignment variables
		people = businessProblem.getPeople();
		groups = businessProblem.getGroups();
		
		// Create groups variables
		csp.log("Create groups variables and post group min-max constraints");
		for (int g = 0; g < groups.length; g++) {
			Var[] peopleVarsInsideGroup = new Var[people.length];
			for (int p = 0; p < people.length; p++) {
				peopleVarsInsideGroup[p] = csp.variable(varName(g,p), 0, 1);
			}
			// Post group Min-Max constraints
			Var groupSizeVar = csp.sum(peopleVarsInsideGroup);
			csp.add(varGroupSizeName(g), groupSizeVar);
			int groupSize = businessProblem.groups[g].getSize();
			if (groupSize > 0)
				csp.post(groupSizeVar,"=",businessProblem.groups[g].getSize());
			else {
				csp.post(groupSizeVar,">=",businessProblem.getGroupMin());
				csp.post(groupSizeVar,"<=",businessProblem.getGroupMax());
			}
		}
		
		// Each person belongs to one and only one group
		csp.log("Post constraints: Each person belongs to one and only one group");
		for (int p = 0; p < people.length; p++) {
			Var[] personVarsInAllGroups = new Var[groups.length];
			for (int g = 0; g < groups.length; g++) {
				personVarsInAllGroups[g] = csp.getVar(varName(g,p));
			}
			Var sum = csp.sum(personVarsInAllGroups);
			csp.add("Person"+p+"Sum",sum);
			csp.post(sum,"=",1); 
		}
	}
	
	public Solution solve() { 
		csp.log("Solve big optimization problem");
		int totalPenaltyInAllGroups = 0;
		for (int g = 0; g < groups.length; g++) {
			Group group = groups[g];
			csp.log("Solve optimization problem for group " + g);
			OptimizationGroup optimizationGroup = new OptimizationGroup(businessProblem,group);
			optimizationGroup.define();
			Solution groupSolution = optimizationGroup.solve(3);
			if (groupSolution == null) {
				throw new RuntimeException("Cannot solve optimization problem for group: " + group);
			}
			totalPenaltyInAllGroups += groupSolution.getValue("Objective");
//			try {
//				Var groupSizeVar = csp.getVar(varGroupSizeName(g));
//				csp.post(groupSizeVar,"=",group.getSize());
//				
//			} catch (Exception e) {
//				throw new RuntimeException("Cannot post size-constraint for group: " + group);
//			}
			for (int p = 0; p < group.people.length; p++) {
				try {
					int personAssignedToGroup = group.people[p].getGroup();
					if (personAssignedToGroup == group.getId()) {
						Var personGroupVar = csp.getVar(varName(g,p));
						//csp.log("Assign person " + p + " to group " + g );
						csp.post(personGroupVar,"=",1);
					}
				} catch (Exception e) {
					throw new RuntimeException("Cannot assign person " + p + " to group: " + group);
				}
			}
		}
		csp.log("\nTotal Penalty in All Groups:" + totalPenaltyInAllGroups);
		return null;
	}
	
	
	String varName(int g, int p) {
		return "Group"+g+"Person"+p;
	}
	
	String varGroupSizeName(int g) {
		return "Group"+g+"Size";
	}
	
}