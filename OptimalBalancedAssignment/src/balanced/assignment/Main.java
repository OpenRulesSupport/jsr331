package balanced.assignment;


import com.openrules.ruleengine.DecisionData;

public class Main {
	
	public static void main(String[] args) {
		
		// get data
		DecisionData data = new DecisionData("file:rules/BalancedAssignmentData.xls"); // Small.xls"
		BusinessProblem problem = (BusinessProblem) data.get("getProblem");
		Person[] people = (Person[]) data.get("getPeople");
		problem.initialize(people);
		
//		GroupSizing groupSizing = new GroupSizing(problem);
//		if (!groupSizing.determine()) {
//			System.out.println("Infeasible problem: check number of people, groups, and groupMin-groupMax");
//			System.exit(-1);
//		}
			
		Optimization optimization = new Optimization(problem);
		optimization.define();
		optimization.solve(); 
		problem.showSolution();
	}

}


