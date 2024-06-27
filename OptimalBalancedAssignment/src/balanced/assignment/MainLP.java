package balanced.assignment;


import com.openrules.ruleengine.DecisionData;

public class MainLP {
	
	public static void main(String[] args) {
		
		// get data
		DecisionData data = new DecisionData("file:rules/BalancedAssignmentData.xls");
		BusinessProblem problem = (BusinessProblem) data.get("getProblem");
		Person[] people = (Person[]) data.get("getPeople");
		problem.setPeople(people);
		problem.setCategoryInstanceSets();
		problem.showCategoryInstanceSets();
		problem.getGroups();
		System.out.println(problem);
		problem.validate();
				
		OptimizationLP optimization = new OptimizationLP(problem);
		optimization.define();
		//optimization.defineObjective();
		optimization.defineObjective();
		optimization.solve();
		problem.showSolution();
	}

}


