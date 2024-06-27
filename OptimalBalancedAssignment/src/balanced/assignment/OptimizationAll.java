package balanced.assignment;

import java.util.ArrayList;
import java.util.Set;

import javax.constraints.*;
import javax.constraints.impl.search.selectors.VarSelectorRandom;

import com.openrules.csp.OptimizationProblem;

public class OptimizationAll extends OptimizationProblem {

	BusinessProblem businessProblem;
	Person[] people;
	Group[] groups;

	public OptimizationAll(BusinessProblem businessProblem) {
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
			csp.post(groupSizeVar,"=",businessProblem.groups[g].getSize());
//			csp.post(groupSizeVar,">=",businessProblem.getGroupMin());
//			csp.post(groupSizeVar,"<=",businessProblem.getGroupMax());
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
		
//		// Remove symmetry by ordering groups by their sizes
//		csp.log("Remove symmetry by ordering groups by their sizes");
//		for (int g = 0; g < groups.length; g++) {
//			Var groupSizeVar = csp.getVar(varGroupSizeName(g));
//			if (g>0) {
//				Var prevGroupSize = csp.getVar(varGroupSizeName(g-1));
//				csp.post(prevGroupSize,"<=",groupSizeVar);
//			}
//		}
	}
	
	public Solution solve(Objective objectiveType,int timeLimitInSec) { 

		csp.log("=== SOLVE:");
		Solver solver = csp.getSolver();
		solver.traceSolutions(true);
		if (timeLimitInSec > 0)
			solver.setTimeLimit(timeLimitInSec*1000); 
//		solver.traceExecution(true);
		solver.traceSolutions(true);
//		solver.addStrategyLogVariables(); 
//		SearchStrategy strategy = solver.getSearchStrategy();
//		strategy.setVarSelectorType(VarSelectorType.INPUT_ORDER);
//		Solution solution = solver.findSolution(); 
		Solution solution = solver.findOptimalSolution(objectiveType,objectiveVar);
		solver.logStats();
		if (solution != null) {
			//solution.log();
			saveSolution(solution);
		} else {
			csp.log("No Solutions");
		}
		return solution;
	}
	
	public void defineObjective() {
		csp.log("defineObjective: Total Variation");
		csp.log("Define optimization objective");
//		int[] coefs = { 1,-1, -1};
		Var[] diffByCategories = new Var[Person.categories.length];
		int c =0;
//		int max = businessProblem.maxSizeOfAnyCategorySet();
//		int min = businessProblem.minSizeOfAnyCategorySet();
		for (int category = 0; category < Person.categories.length; category++) {
			csp.log("category: " + category);
			Set<String> categoryInstances = businessProblem.getCategoryInstances(category);
			Var[] diffByCategoryInstances = new Var[categoryInstances.size()];
			int ct = 0;
			for(String categoryInstance : categoryInstances) {
				csp.log("\tcategoryInstance: " + categoryInstance);
				int maxSizeOfCategoryInstances = Math.min(businessProblem.getGroupMax()*businessProblem.getNumberOfGroups(),
                                                          businessProblem.getNumberOfPeopleInCategoryInstance(category, categoryInstance));
				Var maxPeopleInCategoryInstanceInAnyGroup = csp.variable(0,businessProblem.getGroupMax()-1);
				Var minPeopleInCategoryInstanceInAnyGroup = csp.variable(0,businessProblem.getGroupMax()-1);
				int g = 0;
				for (Group group : groups) {
//					csp.log("\t\t" + group);
					Person[] groupPeople = group.getPeople();
					ArrayList<Var> peopleInCategoryInstanceForGroup = new ArrayList<Var>();
					int p = 0;
					for (Person person : groupPeople) {
						if (person.belongsToCategory(category, categoryInstance)) {
							//String varName = group.getId() + "-" + person.getId();
							String varName = "Group"+g+"Person"+p;
							peopleInCategoryInstanceForGroup.add(csp.getVar(varName));
						}
						p++;
					}
					Var sumPeopleInCategoryInstanceForGroup = csp.sum(peopleInCategoryInstanceForGroup);
					csp.post(sumPeopleInCategoryInstanceForGroup,"<=",businessProblem.getGroupMax());
//					Var scalProdMax = csp.scalProd(coefs, new Var[] {maxPeopleInCategoryInstanceInAnyGroup,sumPeopleInCategoryInstanceForGroup});
//					csp.post(scalProdMax,">=",0);
//					Var scalProdMin = csp.scalProd(coefs, new Var[] {minPeopleInCategoryInstanceInAnyGroup,sumPeopleInCategoryInstanceForGroup});
//					csp.post(scalProdMin,"<=",0);
					csp.post(maxPeopleInCategoryInstanceInAnyGroup, ">=", sumPeopleInCategoryInstanceForGroup); 
					csp.post(minPeopleInCategoryInstanceInAnyGroup, "<=", sumPeopleInCategoryInstanceForGroup);
					g++;
				}
				diffByCategoryInstances[ct] = csp.variable("Diff"+c+"-"+ct,0,maxSizeOfCategoryInstances);
				csp.post(diffByCategoryInstances[ct],"=",maxPeopleInCategoryInstanceInAnyGroup.minus(minPeopleInCategoryInstanceInAnyGroup));
				csp.log("\t\tdiffByCategoryInstances[" + ct + "]: " + diffByCategoryInstances[ct]);
//				diffByCategoryInstances[ct] = csp.variable(0, max);
//				Var scalProd = csp.scalProd(coefs, new Var[] {maxPeopleInCategoryInstanceInAnyGroup,minPeopleInCategoryInstanceInAnyGroup,diffByCategoryInstances[ct]});
//				csp.post(scalProd,"=",0);			
				ct++;
			}
			diffByCategories[c] = csp.sum(diffByCategoryInstances);
			diffByCategories[c].setName("DiffBy-"+Person.categories[category]);
			csp.log("\tdiffByCategories[" + c + "]:" + diffByCategories[c]);
			c++;
		}
				
		Var objective = csp.sum(diffByCategories);
		//csp.post(objective,">=",0);
		csp.add("Objective", objective);
		setObjective(objective);
		csp.log(""+objective);
	}
	
	public void saveSolution(Solution solution) {
//		Var[] vars = csp.getVars();
//		for (int i = 0; i < vars.length; i++) {
//			String name = vars[i].getName();
//			int value = solution.getValue(name);
//			if (value>0) {
//				csp.log(name + ": " + value);
//			}
//		}
		for (int p = 0; p < people.length; p++) {
			Person person = people[p];
			for (int g = 0; g < groups.length; g++) {
				String name = varName(g,p); 
				int value = solution.getValue(name);
				if (value>0) {
					person.setGroup(g);
				}
			}
		}
	}
	
	String varName(int g, int p) {
		return "Group"+g+"Person"+p;
	}
	
	String varGroupSizeName(int g) {
		return "Group"+g+"Size";
	}
	
}