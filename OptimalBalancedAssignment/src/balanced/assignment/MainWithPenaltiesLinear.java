package balanced.assignment;

import com.openrules.ruleengine.DecisionData;

public class MainWithPenaltiesLinear {
	
	public static void main(String[] args) {
		
		// get data
		//DecisionData data = new DecisionData("file:rules/BalancedAssignmentDataSmall.xls");
		DecisionData data = new DecisionData("file:rules/BalancedAssignmentData.xls");
		BusinessProblem problem = (BusinessProblem) data.get("getProblem");
		Person[] people = (Person[]) data.get("getPeople");
		problem.initialize(people);
		
		// define sameness for all pairs of people
		String fileName = "file:rules/BalancedAssignment.xls";
		String decisionName = "DefineSameness";
		problem.defineSimilarities(fileName,decisionName);
		
		GroupSizing groupSizing = new GroupSizing(problem);
		if (!groupSizing.determine()) {
			System.out.println("Infeasible problem: check number of people, groups, and groupMin-groupMax");
			System.exit(-1);
		}
		
		Group[] groups = problem.getGroups();
		for(Group group : groups) {
			System.out.println("\nAssign people to Group "+group.getId());
			//OptimizationGroupLinear opt = new OptimizationGroupLinear(problem, group);
			OptimizationGroupCP opt = new OptimizationGroupCP(problem, group);
			opt.define();
			opt.solve();
		}
	
		problem.showSolution();
	}

}


