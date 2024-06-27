package balanced.assignment;

import java.util.ArrayList;
import java.util.Set;

import javax.constraints.*;
import javax.constraints.impl.search.selectors.ValueSelectorMax;

import com.openrules.csp.OptimizationProblem;

public class OptimizationLP extends Optimization {

	public OptimizationLP(BusinessProblem businessProblem) {
		super(businessProblem);
	}
	
	public void defineObjective() {
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

}