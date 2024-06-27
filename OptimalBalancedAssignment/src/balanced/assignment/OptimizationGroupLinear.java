package balanced.assignment;

import java.util.ArrayList;

import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;

import com.openrules.csp.OptimizationProblem;

public class OptimizationGroupLinear extends OptimizationProblem {

	BusinessProblem businessProblem;
	Group group;

	public OptimizationGroupLinear(BusinessProblem businessProblem, Group group) {
		this.businessProblem = businessProblem;
		this.group = group;
	}
	
	public void define() { // PROBLEM DEFINITION
		
		// Define assignment variables
		Person[] people = businessProblem.getPeople();
		Var[] personVars = new Var[people.length];
		for (int p = 0; p < people.length; p++) {
			personVars[p] = csp.variable("P"+p,0,1);
			if (people[p].getGroup() >= 0) { // no assigned to any group 
				csp.post(personVars[p],"=",0);
			}
		}
		Var groupSizeVar = csp.sum("PSum"+group.getId(),personVars);
		csp.post(groupSizeVar,"=",group.getSize());
		
		// Define Diversity variables
		csp.log("Post diversity constraints");
		ArrayList<PersonSimilarity> similarities = businessProblem.getSimilarities();
		
		ArrayList<Var> penaltyVars = new ArrayList<Var>();
		int n = 0;
		for (PersonSimilarity similarity : similarities) {
			int sameness = similarity.getSameness();
			int p1 = similarity.getPerson1().getNumber();
			int p2 = similarity.getPerson2().getNumber();
			//csp.log("p1="+p1+" p2="+p2 + " sameness =" + sameness);
			Var var1 = csp.getVar("P"+p1);
			Var var2 = csp.getVar("P"+p2);
			Var penaltyVar = createPenaltyVar(n, var1, var2, sameness);
			penaltyVars.add(penaltyVar);
			n++;
		}
		csp.log("defineObjective: Group Penalty");
		Var	groupPenalty = csp.sum(penaltyVars);
		csp.add("Group Penalty",groupPenalty);
		
		// Define optimization objective
		setObjective(groupPenalty);

	}
	
	public Var createPenaltyVar(int n, Var var1, Var var2, int sameness) {
//		penaltyVar = var1.multiply(var2).multiply(sameness); // non-linear
		
		/* Linearization
		 * x,y,z could be 0 or 1
		 * then z = x * x can be linearized as 
		 * z <= x, z <= y, x + y - z <= 1
		 * By some reasons: z - x - y >= -1	with COIN produced an infeasible error
		 */
		int[] coefs = { 1,1,-1 };
		//Var z = csp.variable("z"+n, 0,1);
		Var z = csp.variable("pn"+n, 0,1);
		csp.post(z,"<=",var1);
		csp.post(z,"<=",var2);
		//csp.post(z,">=",var1.plus(var2).minus(1));
		Var scalProd = csp.scalProd("sc"+n,coefs, new Var[] {var1,var2,z});
		csp.post(scalProd,"<=",1);
//		csp.post(scalProd,">=",0);
		Var penaltyVar = z.multiply(sameness);
		
		csp.add(var1.getName()+"&"+var2.getName(),penaltyVar);
		return penaltyVar;
	}
	
	public Solution solve() { 
		Solver solver = csp.getSolver();
		Var objective = getObjective();
		Solution solution = null;
		if (objective != null) {
			csp.log("=== Find Optimal Solution:");
			solution = solver.findOptimalSolution(objectiveVar);
		}
		else {
			csp.log("=== Find a Feasible Solution:");
			solution = solver.findSolution();
		}
		solver.logStats();
		if (solution != null) {
			saveSolution(solution);
		} else {
			csp.log("No Solutions");
		}
		return solution;
	}
	
	public void saveSolution(Solution solution) {
		//solution.log();
		//csp.log("Group " + group.id + " Penalty = "+solution.getValue("Group Penalty"));
		group.setPenalty(solution.getValue("Group Penalty"));
		for (int p = 0; p < group.people.length; p++) {
			int value = solution.getValue("P"+p);
			if (value == 1) {
				Person person = group.people[p];
				person.setGroup(group.getId());
				//csp.log("\t"+person.getNumber());
			}
		}
		csp.log(group.toString());
	}
		
}