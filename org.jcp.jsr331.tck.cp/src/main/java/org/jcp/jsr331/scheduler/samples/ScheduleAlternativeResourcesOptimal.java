package org.jcp.jsr331.scheduler.samples;

import javax.constraints.*;
import javax.constraints.scheduler.*;

public class ScheduleAlternativeResourcesOptimal {
	
	Schedule s = ScheduleFactory.newSchedule("ScheduleAlternativeResources",0,30);

	Activity[] activities;
	String[] resourceNames = {"Joe", "Jack", "Jim"};
	ResourceDisjunctive[] resources;
	Var totalCost;

	public void define() throws Exception {

		// defining jobs
		Activity masonry = s.activity("masonry  ", 7);
		Activity carpentry = s.activity("carpentry", 3);
		Activity plumbing = s.activity("plumbing ", 8);
		Activity ceiling = s.activity("ceiling  ", 3);
		Activity roofing = s.activity("roofing  ", 1);
		Activity painting = s.activity("painting ", 2);
		Activity windows = s.activity("windows  ", 1);
		Activity facade = s.activity("facade   ", 2);
		Activity garden = s.activity("garden   ", 1);
		Activity movingIn = s.activity("moving in", 1);

		activities = new Activity[] { masonry, carpentry, roofing, plumbing,
				ceiling, windows, facade, garden, painting, movingIn };

		// Posting "startsAfterEnd" constraints
		s.post(carpentry, ">", masonry);
		s.post(roofing, ">", carpentry);
		s.post(plumbing, ">", masonry);
		s.post(ceiling, ">", masonry);
		s.post(windows, ">", roofing);
		s.post(facade, ">", roofing);
		s.post(facade, ">", plumbing);
		s.post(garden, ">", roofing);
		s.post(garden, ">", plumbing);
		s.post(painting, ">", ceiling);
		s.post(movingIn, ">", windows);
		s.post(movingIn, ">", facade);
		s.post(movingIn, ">", garden);
		s.post(movingIn, ">", painting);

		// Define Resources
		ResourceDisjunctive joe  = s.resourceDisjunctive("Joe");
		ResourceDisjunctive jack = s.resourceDisjunctive("Jack");
		ResourceDisjunctive jim  = s.resourceDisjunctive("Jim");
		resources = new ResourceDisjunctive[] { joe, jack, jim };

		// Posting constraints for alternative resources
		masonry.requires(joe, jack).post();
		carpentry.requires(joe, jim).post();
		plumbing.requires(jack).post();
		ceiling.requires(joe, jim).post();
		roofing.requires(joe, jim).post();
		painting.requires(jack, jim).post();
		windows.requires(joe, jim).post();
		garden.requires(joe, jack, jim).post();
		facade.requires(joe, jack).post();
		movingIn.requires(joe, jim).post();
		
		// Defining cost
		int joeDailyCost = 300;
		int jackDailyCost = 230;
		int jimDailyCost = 250;
		
		int[] dailyCosts = { joeDailyCost, jackDailyCost, jimDailyCost };
		
		//Approach 1
//		Var[] resourceCostVars = new Var[resources.length];
//		for (int i = 0; i < resources.length; i++) {
//			Resource r = resources[i];
//			Var[] usage = s.getConstraintUsage(r);
//			resourceCostVars[i] = s.sum(usage).multiply(dailyCosts[i]);
//		}
//		totalCost = s.sum(resourceCostVars);
		
		//Approach 2
		totalCost = s.addTotalResourceCostVar("Total Cost", resourceNames, dailyCosts);
		
		for (int i = 0; i < s.getActivities().size(); ++i) {
            Activity job = (Activity) s.getActivities().elementAt(i);
            s.log(job.toString());
        }
	}
	
	public void solveOne() {
		Solver solver = s.getSolver();
		solver.setSearchStrategy(s.strategyScheduleActivities());
		solver.addSearchStrategy(s.strategyAssignResources());
		solver.addSearchStrategy(s.strategyScheduleVars());
		//solver.addSearchStrategy(totalCost);
		Solution solution = solver.findSolution();
		if (solution == null)
			s.log("No solutions");
		else {
			s.log(solution);
			//s.log("Total Cost = " + solution.getValue("Total Cost"));
			//solution.log();
		}
		solver.logStats();
	}
	
	public void solveMany() {
		Solver solver = s.getSolver();
		solver.setSearchStrategy(s.strategyScheduleActivities());
		solver.addSearchStrategy(s.strategyAssignResources());
		solver.addSearchStrategy(s.strategyScheduleVars());
		solver.setMaxNumberOfSolutions(10);
		solver.setTimeLimit(50000);
		solver.traceSolutions(true);
		Solution[] solutions = solver.findAllSolutions();
		for (int i = 0; i < solutions.length; i++) {
			s.log(solutions[i]);
			//s.log("Total Cost = " + solutions[i].getValue("Total Cost"));
			//solutions[i].log();
		}
		solver.logStats();
	}
	
	public void solveOptimal() {
		Solver solver = s.getSolver();
		solver.setSearchStrategy(s.strategyScheduleActivities());
		solver.addSearchStrategy(s.strategyAssignResources());
		solver.addSearchStrategy(s.strategyScheduleVars());
		int max = 5;
		solver.setMaxNumberOfSolutions(max);
		int timeLimit = 20; //seconds
		solver.setTimeLimit(timeLimit*1000);
		solver.setTimeLimitGlobal(200*1000);
		//solver.setOptimizationStrategy("Dichotomize");
		solver.setOptimizationStrategy("Basic");
		solver.traceSolutions(true);
		Solution solution = solver.findOptimalSolution(Objective.MINIMIZE,totalCost);
		//Solution solution = solver.findOptimalSolution(Objective.MAXIMIZE,totalCost);
		if (solution == null)
			s.log("No solutions");
		else {
			s.log(solution);
			s.log("Total Cost = " + solution.getValue("Total Cost"));
			solution.log();
		}
		solver.logStats();
	}

	public static void main(String args[]) throws Exception {
		ScheduleAlternativeResourcesOptimal schedule = new ScheduleAlternativeResourcesOptimal();
		schedule.define();
		//schedule.solveOne();
		//schedule.solveMany();
		schedule.solveOptimal();
	}
	
}
