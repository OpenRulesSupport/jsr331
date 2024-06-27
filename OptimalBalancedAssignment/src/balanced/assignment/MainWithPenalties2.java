package balanced.assignment;

import java.util.ArrayList;

import com.openrules.ruleengine.Decision;
import com.openrules.ruleengine.DecisionData;

public class MainWithPenalties2 {
	
	public static void main(String[] args) {
		
		// get data
		DecisionData data = new DecisionData("file:rules/BalancedAssignmentData.xls");
		BusinessProblem problem = (BusinessProblem) data.get("getProblem");
		Person[] people = (Person[]) data.get("getPeople");
		problem.initialize(people);
		
		// define sameness for all pairs of people
		String fileName = "file:rules/BalancedAssignment.xls";
		String decisionName = "DefineSameness";
		Decision decision = new Decision(decisionName,fileName);
		decision.put("FEEL", "On");
		decision.put("trace", "off");
		int n = 0;
		ArrayList<PersonSimilarity> similarities = problem.getSimilarities();
		for (int p1 = 0; p1 < people.length-1; p1++) {
			for (int p2 = p1+1; p2 < people.length; p2++) {
				PersonSimilarity sameness = new PersonSimilarity();
				sameness.person1 = people[p1];
				sameness.person2 = people[p2];
				decision.put("Sameness", sameness);
				decision.execute();
				similarities.add(sameness);
				n++;
				if (n%1000 == 0)
					System.out.println("Defined " + n + " similarities...");
			}
		}
		System.out.println("Defined " + similarities.size() + " similarities.");
		
		GroupSizing groupSizing = new GroupSizing(problem);
		if (!groupSizing.determine()) {
			System.out.println("Infeasible problem: check number of people, groups, and groupMin-groupMax");
			System.exit(-1);
		}
		
		OptimizationWithPenalties optimizationWithPenalties = new OptimizationWithPenalties(problem);
		optimizationWithPenalties.define();
		optimizationWithPenalties.defineObjective();
		optimizationWithPenalties.solve(60);
		problem.showSolution();
	}

}


