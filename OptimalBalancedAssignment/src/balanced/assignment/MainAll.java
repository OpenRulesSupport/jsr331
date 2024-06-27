package balanced.assignment;


import com.openrules.ruleengine.DecisionData;

public class MainAll {
	
	public static void main(String[] args) {
		
		// get data
		DecisionData data = new DecisionData("file:rules/BalancedAssignmentData.xls"); // Small.xls"
		BusinessProblem problem = (BusinessProblem) data.get("getProblem");
		Person[] people = (Person[]) data.get("getPeople");
		problem.initialize(people);
		
		GroupSizing groupSizing = new GroupSizing(problem);
		if (!groupSizing.determine()) {
			System.out.println("Infeasible problem: check number of people, groups, and groupMin-groupMax");
			System.exit(-1);
		}
			
		OptimizationAll optimization = new OptimizationAll(problem);
		optimization.define();
		optimization.defineObjective();
		optimization.solve(30); // time limit = 30 seconds
		problem.showSolution();
	}

}


