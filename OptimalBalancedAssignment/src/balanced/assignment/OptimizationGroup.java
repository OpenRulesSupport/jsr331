package balanced.assignment;

import java.util.ArrayList;
import java.util.Set;

import javax.constraints.*;

import com.openrules.csp.OptimizationProblem;

public class OptimizationGroup extends OptimizationProblem {

	BusinessProblem businessProblem;
	Group group;

	public OptimizationGroup(BusinessProblem businessProblem, Group group) {
		this.businessProblem = businessProblem;
		this.group = group;
	}
	
	public void define() { // PROBLEM DEFINITION
		csp.log("Define opt.problem for " + group);
		// Define assignment variables
		Person[] people = businessProblem.getPeople();
		
		// Create groups variables
		csp.log("Create groups variables and post group min-max constraints");
		Var[] peopleVarsInsideGroup = new Var[people.length];
		for (int p = 0; p < people.length; p++) {
			int min = 0;
			int max = 1;
			int personGroup = people[p].group;
			if (personGroup >= 0) {
				if (personGroup == group.getId()) {
					min = 1;
					max = 1;
				}
				else {
					min = 0;
					max = 0;
				}
			}
			peopleVarsInsideGroup[p] = csp.variable(varName(group.id,p), min, max);
		}
		// Post group Min-Max constraints
		Var groupSizeVar = csp.sum(peopleVarsInsideGroup);
		csp.add(varGroupSizeName(group.id), groupSizeVar);
		int groupSize = group.getSize();
		if (groupSize > 0)
			csp.post(groupSizeVar,"=",groupSize);
		else {
			csp.post(groupSizeVar,">=",businessProblem.getGroupMin());
			csp.post(groupSizeVar,"<=",businessProblem.getGroupMax());
		}
		
		// Create person variables
		Var[] personVars = new Var[group.size];
		for (int i = 0; i < group.size; i++) {
			personVars[i] = csp.variable("Person"+i,0,people.length-1);
			if (i > 0) {
				csp.post(personVars[i-1],"<",personVars[i]);
			}
		}
		//csp.postAllDiff(personVars);
		for (int p = 0; p < people.length; p++) {
			Constraint c1 = csp.linear(peopleVarsInsideGroup[p], "=", 0);
			for (int i = 0; i < group.size; i++) {
				Constraint c2 = csp.linear(personVars[i], "!=", p);
				csp.postIfThen(c1, c2);
			}
		}
		
		defineObjective();
	}
	
	public void defineObjective() {
		csp.log("defineObjective: Total Penalty");
		// Define Diversity variables
		csp.log("Post diversity constraints");
		ArrayList<PersonSimilarity> similarities = businessProblem.getSimilarities();
		
		ArrayList<Var> penaltyVars = new ArrayList<Var>();
		int n = 0;
		for (int p1 = 0; p1 < group.people.length-1; p1++) {
			for (int p2 = p1+1; p2 < group.people.length; p2++) {
				Var var1 = csp.getVar(varName(group.getId(),p1));
				Var var2 = csp.getVar(varName(group.getId(),p2));
				int sameness = similarities.get(n).getSameness();
				if (sameness != 0) {
					Var var = var1.multiply(var2).multiply(sameness);
					csp.add("pn"+n,var);
					penaltyVars.add(var);
				}
				n++;
			}
		}
		
		// Define optimization objective
		Var objective = csp.sum(penaltyVars);
		csp.add("Objective", objective);
		setObjective(objective);
	}
	
	public void defineVariationObjective() {
		csp.log("defineGoupObjective: Variation within group " + group.id);
		Var[] variationByCategories = new Var[Person.categories.length];
		int c =0;
		Person[] groupPeople = group.getPeople();
		for (int category = 0; category < Person.categories.length; category++) {
			csp.log("category: " + category);
			Set<String> categoryInstances = businessProblem.getCategoryInstances(category);
			Var[] variationByCategoryInstances = new Var[categoryInstances.size()];
			int ct = 0;
			for(String categoryInstance : categoryInstances) {
				csp.log("\tcategoryInstance: " + categoryInstance);
				int maxSizeOfCategoryInstances = Math.min(businessProblem.getGroupMax()*businessProblem.getNumberOfGroups(),
                                                          businessProblem.getNumberOfPeopleInCategoryInstance(category, categoryInstance));
				Var maxPeopleInCategoryInstance = csp.variable(0,businessProblem.getGroupMax()-1);
				Var minPeopleInCategoryInstance = csp.variable(0,businessProblem.getGroupMax()-1);
				
				Var[] peopleInCategoryInstance = new Var[groupPeople.length];
				int p = 0;
				for (Person person : groupPeople) {
					peopleInCategoryInstance[p] = csp.variable("peopleInCategory" + c +"Instance"+ct+"Person"+p, 0,1);
					Var var = csp.getVar(varName(group.getId(),person.getNumber()));
					csp.postIfThen(csp.linear(var, "=",0), csp.linear(peopleInCategoryInstance[p],"=",0)); 
					if (!person.belongsToCategory(category, categoryInstance)) {
						csp.post(peopleInCategoryInstance[p],"=",0);
					}
					p++;
				}
				Var sumPeopleInCategoryInstanceForGroup = csp.sum(peopleInCategoryInstance);
				csp.post(sumPeopleInCategoryInstanceForGroup,"<=",businessProblem.getGroupMax());
				csp.post(maxPeopleInCategoryInstance, ">=", sumPeopleInCategoryInstanceForGroup); 
				csp.post(minPeopleInCategoryInstance, "<=", sumPeopleInCategoryInstanceForGroup);
				
				variationByCategoryInstances[ct] = csp.variable("VariationBy"+c+"-"+ct,0,maxSizeOfCategoryInstances);
				csp.post(variationByCategoryInstances[ct],"=",maxPeopleInCategoryInstance.minus(minPeopleInCategoryInstance));
				csp.log("\t\t" + variationByCategoryInstances[ct]);
				ct++;
			}
			variationByCategories[c] = csp.sum(variationByCategoryInstances);
			variationByCategories[c].setName("VariationBy-"+Person.categories[category]);
			csp.log("\tvariationByCategories[" + c + "]:" + variationByCategories[c]);
			c++;
		}
				
		Var objective = csp.sum(variationByCategories);
		//csp.post(objective,">=",0);
		csp.add("Objective", objective);
		setObjective(objective);
		csp.log(""+objective);
	}
	
	public Solution solve(int timeLimitInSec) {
		//return super.solve(Objective.MINIMIZE,timeLimitInSec); 
		return super.solve(Objective.MINIMIZE, timeLimitInSec, 
							VarSelectorType.MIN_VALUE, //VarSelectorType.MAX_VALUE, 
							ValueSelectorType.RANDOM);
	}
	
	public void saveSolution(Solution solution) {
		//solution.log();
		csp.log("\nSolution for Group " +  group.id + ": Group Penalty=" + solution.getValue("Objective"));
		int size = 0;
		for (Person person : group.people) {
			String name = varName(group.id,person.getNumber()); 
			int value = solution.getValue(name);
			if (value > 0) {
				person.setGroup(group.id);
				csp.log("\t" + person);
				size++;
			}
		}
		group.setSize(size);
	}
	
	String varName(int g, int p) {
		return "Group"+g+"Person"+p;
	}
	
	String varGroupSizeName(int g) {
		return "Group"+g+"Size";
	}
	
}