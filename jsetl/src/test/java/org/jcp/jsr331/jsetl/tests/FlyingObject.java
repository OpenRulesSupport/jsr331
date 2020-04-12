package org.jcp.jsr331.jsetl.tests;

import javax.constraints.Constraint;
import javax.constraints.Problem;
import javax.constraints.ProblemFactory;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.Probability;

public class FlyingObject {

	String id;
	boolean bird;
	boolean penguin;
	boolean inAirplan;
	String abilityToFly;
	
	public FlyingObject(String id) {
		this.id = id;
		bird = true;
		penguin = false;
		inAirplan = false;
		abilityToFly = "?";
	}

	@Override
	public String toString() {
		return "FlyingObject [id=" + id + ", bird=" + bird + ", penguin="
				+ penguin + ", inAirplan=" + inAirplan + ", abilityToFly="
				+ abilityToFly + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isBird() {
		return bird;
	}

	public void setBird(boolean bird) {
		this.bird = bird;
	}

	public boolean isPenguin() {
		return penguin;
	}

	public void setPenguin(boolean penguin) {
		this.penguin = penguin;
	}

	public boolean isAirplan() {
		return inAirplan;
	}

	public void setAirplan(boolean airplan) {
		this.inAirplan = airplan;
	}

	public String igetAbilityToFly() {
		return abilityToFly;
	}

	public void setAbilityToFly(String abilityToFly) {
		this.abilityToFly = abilityToFly;
	}
	
	public String solve() {
		Problem p = ProblemFactory.newProblem("DefeasibleLogic");
		Var flyVar = p.variable("Fly",0,1);
		Constraint canFly = p.linear(flyVar, "=", 1);
		Constraint canNotFly = p.linear(flyVar, "=", 0);

		if (bird) 
			canFly.post("birdCanFly",Probability.HIGH);
		if (penguin) 
			canNotFly.post("penguinCanNotFly",Probability.VERY_HIGH);
			
		if (inAirplan)
			canFly.post();
		
		p.log("After posting probability constraints");
		p.log(p.getVars());

		Var objective = p.getTotalConstraintViolation();
		Solver solver = p.getSolver();
		Solution solution = solver.findOptimalSolution(objective);
		if (solution != null) {
			solution.log();
			abilityToFly = solution.getValue("Fly") == 1 ? "YES" : "NO";
		}
		else {
			p.log("no solution found");
		}
		return abilityToFly;
	}
	
	public void log() {
		System.out.println(toString());
	}

	public static void main(String[] args) {
		FlyingObject fo1 = new FlyingObject("One");
		fo1.solve();
		fo1.log();
		
		FlyingObject fo2 = new FlyingObject("One");
		fo2.setId("Two");
		fo2.setBird(true);
		fo2.setPenguin(true);
		fo2.setAirplan(false);
		fo2.solve();
		fo2.log();
		
		FlyingObject fo3 = new FlyingObject("One");
		fo3.setId("Three");
		fo3.setBird(true);
		fo3.setPenguin(true);
		fo3.setAirplan(true);
		fo3.solve();
		fo3.log();
	}
}
