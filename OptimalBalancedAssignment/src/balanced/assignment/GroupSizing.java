package balanced.assignment;

import javax.constraints.*;
import com.openrules.csp.OptimizationProblem;

public class GroupSizing extends OptimizationProblem {

	BusinessProblem problem;
	
	public GroupSizing(BusinessProblem problem) {
		this.problem = problem;
	}
	
	public void define() { // PROBLEM DEFINITION
		
		// Create groups variables
		csp.log("Validation");
		Var[] groupSizeVars = new Var[problem.numberOfGroups];
		for (int g = 0; g < problem.numberOfGroups; g++) {
			groupSizeVars[g] = csp.variable("Group"+g, problem.groupMin, problem.groupMax);
		}
		
		Var totalPeopleVar = csp.sum("totalPeopleVar",groupSizeVars);
		csp.post(totalPeopleVar,"=",problem.people.length); 
		setObjective(totalPeopleVar);
		
	}
	
	public boolean determine() {
		define();
		Solution sol = solve();
		if (sol == null)
			return false;
		return true;
	}
	
	public void saveSolution(Solution solution) {
		solution.log();
		for (int g = 0; g < problem.groups.length; g++) {
			Group group = problem.groups[g];
			group.setSize(solution.getValue("Group"+g));
		}
	}

}