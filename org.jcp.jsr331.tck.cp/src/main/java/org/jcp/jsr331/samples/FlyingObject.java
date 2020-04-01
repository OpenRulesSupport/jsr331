package org.jcp.jsr331.samples;

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
	boolean chicken;
	boolean duck;
	public boolean isChicken() {
		return chicken;
	}

	public void setChicken(boolean chicken) {
		this.chicken = chicken;
	}

	public boolean isDuck() {
		return duck;
	}

	public void setDuck(boolean duck) {
		this.duck = duck;
	}

	boolean inAirplan;
	String abilityToFly;
	String abilityToSwim;
	
	public boolean isInAirplan() {
		return inAirplan;
	}

	public void setInAirplan(boolean inAirplan) {
		this.inAirplan = inAirplan;
	}

	public String getAbilityToSwim() {
		return abilityToSwim;
	}

	public void setAbilityToSwim(String abilityToSwim) {
		this.abilityToSwim = abilityToSwim;
	}

	public String getAbilityToFly() {
		return abilityToFly;
	}

	public FlyingObject(String id) {
		this.id = id;
		bird = true;
		penguin = false;
		duck = false;
		chicken = false;
		inAirplan = false;
		abilityToFly = "?";
		abilityToSwim = "?";
	}

	@Override
	public String toString() {
		return "FlyingObject [id=" + id + ", bird=" + bird + ", penguin="
				+ penguin + ", chicken=" + chicken + ", duck=" + duck
				+ ", inAirplan=" + inAirplan + ", abilityToFly=" + abilityToFly
				+ ", abilityToSwim=" + abilityToSwim + "]";
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
	
	public boolean solve() {
		Problem p = ProblemFactory.newProblem("DefeasibleLogic");
		p.log("\n=== SOLVE "+this);
		Var flyVar = p.variable("canFly",0,1);
		Constraint canFly = p.linear(flyVar, "=", 1);
		Constraint canNotFly = p.linear(flyVar, "=", 0);
		
		Var swimVar = p.variable("canSwim",0,1);
		Constraint canSwim = p.linear(swimVar, "=", 1);
		Constraint canNotSwim = p.linear(swimVar, "=", 0);

		if (bird) {
			canFly.post("birdCanFly",Probability.HIGH);
			canNotSwim.post("birdCanNotSwim",Probability.VERY_HIGH);
		}
		
		if (penguin) {
			canNotFly.post("penguinCanNotFly",Probability.VERY_HIGH);
			canSwim.post();
		}
		
		if (duck)
			canSwim.post();
		if (chicken)
			canNotSwim.post();
			
		if (inAirplan)
			canFly.post();
		
		p.log("After posting probability constraints");
		p.log(p.getVars());

		Var objective = p.getTotalConstraintViolation();
		Solver solver = p.getSolver();
		Solution solution = solver.findOptimalSolution(objective);
		if (solution != null) {
			solution.log();
			abilityToFly = solution.getValue("canFly") == 1 ? "YES" : "NO";
			abilityToSwim = solution.getValue("canSwim") == 1 ? "YES" : "NO";
			return true;
		}
		else {
			p.log("no solution found");
			return false;
		}
	}
	
	public void log() {
		System.out.println(toString());
	}

	public static void main(String[] args) {
		FlyingObject fo1 = new FlyingObject("One");
		fo1.solve();
		fo1.log();
		
		FlyingObject fo2 = new FlyingObject("Two");
		fo2.setBird(true);
		fo2.setPenguin(true);
		fo2.setAirplan(false);
		fo2.solve();
		fo2.log();
		
		FlyingObject fo3 = new FlyingObject("Three");
		fo3.setBird(true);
		fo3.setPenguin(true);
		fo3.setAirplan(true);
		fo3.solve();
		fo3.log();
		
		FlyingObject fo4 = new FlyingObject("Four");
		fo4.setBird(true);
		fo4.setChicken(true);
		fo4.solve();
		fo4.log();
		
		FlyingObject fo5 = new FlyingObject("Five");
		fo5.setBird(true);
		fo5.setDuck(true);
		fo5.solve();
		fo5.log();
	}
}
