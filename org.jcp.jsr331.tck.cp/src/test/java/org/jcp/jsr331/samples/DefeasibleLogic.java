package org.jcp.jsr331.samples;

import javax.constraints.Constraint;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.Probability;

/*
[8/1/2013 2:51:37 PM] Mark Proctor:
you might be interested in Defeasible Logic
http://drools.46999.n3.nabble.com/Defeasible-Logic-tt4025258.html
it has a funny sounding name, but it actually helps solve real world business problems, in an really clean way.
it deals with separation of concerns, on default logic.
so you can have a default rule, but another rule can override that.

1) all birds fly
2) penguins don't fly
3) a penguin with a rocket flies
how do you deal with the conflict?

that's what defeasible handles
it's written with legal systems in mind
yes it's similar
it's called skeptical reasoning
it allows the exception rules to be cleanly maintained separate
without having to put all your exception logic into the one rule
the RuleML group push it quite hard, it's a big research effort there
the other thing is defeasible is a TMS, so it maintained the truth hierarchies for this
via a tree of logical insertions

*/

public class DefeasibleLogic {
	Problem p = ProblemFactory.newProblem("DefeasibleLogic");
	static int TRUE = 1;
	static int FALSE = 0;
	
	public void resolve() {
		Var abilityToFly  = p.variable("abilityToFly",0,1);
		Var bird  = p.variable("bird",0,1);
		Var penguin  = p.variable("penguin",0,1);
		Var rocket  = p.variable("rocket",0,1);
		p.log("=== solve ===");
		Constraint isBird = p.linear(bird, "=", TRUE);
		Constraint isNotBird = p.linear(bird, "=", FALSE);
		Constraint isPenguin = p.linear(penguin, "=", TRUE);
		Constraint isNotPenguin = p.linear(penguin, "=", FALSE);
		Constraint hasRocket = p.linear(rocket, "=", TRUE);
		Constraint noRocket = p.linear(rocket, "=", FALSE);
		
		Constraint canFly = p.linear(abilityToFly, "=", TRUE);
		Constraint canNotFly = p.linear(abilityToFly, "=", FALSE);
		
		// Hard constraints
		p.postIfThen(isPenguin, isBird);
		p.postIfThen(hasRocket,canFly);
		
		p.log("After posting all constraints");
		p.log(p.getVars());
		
		// Test constraints		
		p.post(isBird);
		p.post(isPenguin);
		p.post(noRocket);
		
		
		p.log("After posting test constraints");
		p.log(p.getVars());
		Constraint penguinCanNotFly = isPenguin.implies(canNotFly);
		Constraint birdCanFly = isBird.implies(canFly);
		p.log(p.getVarBools());
		
		birdCanFly.post("birdCanFly",Probability.LOW);
		penguinCanNotFly.post("penguinCanNotFly",Probability.VERY_HIGH);
		p.log("After posting probability constraints");
		p.log(p.getVars());

		Solver solver = p.getSolver();
		Solution solution = solver.findOptimalSolution(p.getTotalConstraintViolation());
		if (solution != null) {
			solution.log();
		}
		else
			p.log("no solution found");
		solver.logStats();
	}
	
/*
	public void run() {
        p.log("=== run ===");
		Constraint isBird = p.linear(bird, "=", TRUE);
		Constraint isNotBird = p.linear(bird, "=", FALSE);
		Constraint isPenguin = p.linear(penguin, "=", TRUE);
		Constraint isNotPenguin = p.linear(penguin, "=", FALSE);
		Constraint hasRocket = p.linear(rocket, "=", TRUE);
		Constraint noRocket = p.linear(rocket, "=", FALSE);
		
		Constraint canFly = p.linear(abilityToFly, "=", TRUE);
		Constraint canNotFly = p.linear(abilityToFly, "=", FALSE);
		
//		p.postIfThen(hasRocket,canFly);
//		p.postIfThen(noRocket.and(isBird.and(isNotPenguin)), canFly);
//		p.postIfThen(noRocket.and(isNotBird), canNotFly);
		
		p.log("After posting constraints");
		p.log(p.getVars());
		
		p.post(isBird);
		p.post(isPenguin);
		p.post(hasRocket);
		
		p.log("After posting isBird, isPenguin, hasRocket");
		p.log(p.getVars());

//		Solver solver = p.getSolver();
//		Solution[] solutions = solver.findAllSolutions();
//		for (Solution solution : solutions)
//			solution.log();
//		solver.logStats();
	}
	
	public void solve() {
		p.log("=== solve ===");
		Constraint isBird = p.linear(bird, "=", TRUE);
		Constraint isNotBird = p.linear(bird, "=", FALSE);
		Constraint isPenguin = p.linear(penguin, "=", TRUE);
		Constraint isNotPenguin = p.linear(penguin, "=", FALSE);
		Constraint hasRocket = p.linear(rocket, "=", TRUE);
		Constraint noRocket = p.linear(rocket, "=", FALSE);
		
		Constraint canFly = p.linear(abilityToFly, "=", TRUE);
		Constraint canNotFly = p.linear(abilityToFly, "=", FALSE);
		
		Constraint birdCanFly = isBird.implies(canFly);
		Constraint penguinCanNotFly = isPenguin.implies(canNotFly);
		
		// Hard constraints
		p.postIfThen(isPenguin, isBird);
		p.postIfThen(hasRocket,canFly);
		
		// Constraints that could be violated under certain additional conditions
		Var birdCanFlyVar = birdCanFly.asBool();
		p.add("birdCanFlyVar",birdCanFlyVar);
		Var penguinCanNotFlyVar = penguinCanNotFly.asBool();
		p.add("penguinCanNotFlyVar",penguinCanNotFlyVar);
		Var[] weightVars = {
			birdCanFlyVar.multiply(1),
			penguinCanNotFlyVar.multiply(10)
		};
		// Optimization objective
		Var totalViolations = p.sum(weightVars);
		totalViolations.setName("Total Constraint Violations");
		
		p.log("After posting all constraints");
		p.log(p.getVars());
		
		// Test constraints		
		p.post(isBird);
		p.post(isNotPenguin);
		p.post(noRocket);
		
		p.log("After posting test constraints");
		p.log(p.getVars());

		Solver solver = p.getSolver();
		Solution solution = solver.findOptimalSolution(totalViolations);
		if (solution != null) {
			solution.log();
		}
		else
			p.log("no solution found");
		solver.logStats();
	}
*/	
	
	
	public static void main(String[] args) {
		DefeasibleLogic t = new DefeasibleLogic();
		//t.run();
		t.resolve();
	}
}
