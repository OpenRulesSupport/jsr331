package org.jcp.jsr331.scheduler.samples;

import javax.constraints.*;
import javax.constraints.scheduler.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MoveAction;

public final class ScheduleActivitiesWorkerBudgetOptimal {
	
	Schedule s = ScheduleFactory.newSchedule("ScheduleActivitiesWorkerBudget",0,50);
	Var objective;
	
	public void define() throws Exception {

		// defining jobs
		Activity masonry =   s.activity("masonry",7);
		Activity carpentry = s.activity("carpentry",3);
		Activity plumbing =  s.activity("plumbing",8);
		Activity ceiling =   s.activity("ceiling",3);
		Activity roofing =   s.activity("roofing",1);
		Activity painting =  s.activity("painting",2);
		Activity windows =   s.activity("windows",1);
		Activity facade =    s.activity("facade",2);
		Activity garden =    s.activity("garden",1);
		Activity movingIn =  s.activity("moving in",1);
				
		Activity[] activities = {
			masonry,carpentry,roofing,plumbing,ceiling,windows,	
			facade,garden,painting,movingIn
		};

		// Posting "startsAfterEnd" constraints
		s.post(carpentry,">",masonry);
		s.post(roofing,">",carpentry);
		s.post(plumbing,">",masonry);
		s.post(ceiling,">",masonry);
		s.post(windows,">",roofing);
		s.post(facade,">",roofing);
		s.post(facade,">",plumbing);
		s.post(garden,">",roofing);
		s.post(garden,">",plumbing);
		s.post(painting,">",ceiling);
		s.post(movingIn,">",windows);
		s.post(movingIn,">",facade);
		s.post(movingIn,">",garden);
		s.post(movingIn,">",painting);

		// Adding Disjunctive Resource
		ResourceDisjunctive worker = s.resourceDisjunctive("Worker");
		for (int i = 0; i < activities.length; ++i) {
			activities[i].requires(worker);
		}
		
		// Define resource "Budget"
		int initialBudget = 13000;
		int totalBudget = initialBudget + 16000;
		Resource budget = s.resource("Budget",totalBudget,ResourceType.CONSUMABLE);
		budget.setCapacityMax(0,15,initialBudget);
		s.log(budget.toString());
		// Post budget requirement constraints
		int consumptionPerDay = 1000;
		for (int i = 0; i < activities.length; i++) {
			Activity activity = activities[i];
			s.log("=== POST: "+activity.getName() + " requires $" + consumptionPerDay + " per day");
			activity.requires(budget, consumptionPerDay);
			s.log(budget.toString());
		}
		
		//objective = movingIn.getStart();
		//objective.setName("Moving In ASAP");
		objective = s.sum(budget.getCapacities());
		objective.setName("Total Budget");
		
		s.logActivities();
		s.log(budget.toString());
	}
	
	public void solve() {

		Solver solver = s.getSolver();
		solver.setSearchStrategy(s.strategyScheduleActivities());
		solver.addSearchStrategy(s.strategyAssignResources());
		//Solution solution = solver.findSolution();
		solver.setMaxNumberOfSolutions(3);
		int timeLimit = 20; //sec
		solver.setTimeLimit(timeLimit*1000);
		solver.setTimeLimitGlobal(5*timeLimit*1000); // milliseconds
		//solver.traceSolutions(true);
		solver.setOptimizationStrategy("Dichotomize");
		Solution solution = solver.findOptimalSolution(Objective.MINIMIZE,objective);
		if (solution == null)
			s.log("No solutions");
		else {
			s.log("OPTIMAL SOLUTION:");
			//solution.log();
			s.log(solution);
		}
			
	}
	
	public static void main(String args[]) throws Exception {
		ScheduleActivitiesWorkerBudgetOptimal p = new ScheduleActivitiesWorkerBudgetOptimal();
		p.define();
		p.solve();		
	}
}
