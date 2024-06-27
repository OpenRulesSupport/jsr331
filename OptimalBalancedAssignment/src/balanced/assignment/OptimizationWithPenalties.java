package balanced.assignment;

import java.util.ArrayList;

import javax.constraints.*;

public class OptimizationWithPenalties extends Optimization {

	public OptimizationWithPenalties(BusinessProblem businessProblem) {
		super(businessProblem);
	}

	public void defineObjective() {
		csp.log("defineObjective: Total Penalty");
		// Define Diversity variables
		csp.log("Post diversity constraints");
		ArrayList<PersonSimilarity> similarities = businessProblem.getSimilarities();
		
		Var[] groupPenalties = new Var[groups.length];
		for (int g = 0; g < groups.length; g++) {
			csp.log("\t group: " + g);
			ArrayList<Var> penaltyVars = new ArrayList<Var>();
			int n = 0;
			for (int p1 = 0; p1 < people.length-1; p1++) {
				for (int p2 = p1+1; p2 < people.length; p2++) {
					Var var1 = csp.getVar(varName(g,p1));
					Var var2 = csp.getVar(varName(g,p2));
					int sameness = similarities.get(n).getSameness();
					if (sameness != 0) {
						Var var = var1.multiply(var2).multiply(sameness);
						csp.add("pn"+n,var);
						penaltyVars.add(var);
					}
					n++;
				}
			}
			groupPenalties[g] = csp.sum(penaltyVars);
			csp.add("Group" + g + "Penalty",groupPenalties[g]);
		}
		
		// Define optimization objective
		Var objective = csp.sum(groupPenalties);
		csp.add("Objective", objective);
		setObjective(objective);
	}
		
}