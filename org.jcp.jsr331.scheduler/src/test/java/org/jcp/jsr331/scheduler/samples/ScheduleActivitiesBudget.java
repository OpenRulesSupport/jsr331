package org.jcp.jsr331.scheduler.samples;

import javax.constraints.*;
import javax.constraints.scheduler.*;

public final class ScheduleActivitiesBudget {
	
	Schedule s = ScheduleFactory.newSchedule("ScheduleActivitiesBudget",0,40);
	
	public void define() throws Exception {

		// defining jobs
		Activity masonry =   s.activity("masonry  ",7);
		Activity carpentry = s.activity("carpentry",3);
		Activity plumbing =  s.activity("plumbing ",8);
		Activity ceiling =   s.activity("ceiling  ",3);
		Activity roofing =   s.activity("roofing  ",1);
		Activity painting =  s.activity("painting ",2);
		Activity windows =   s.activity("windows  ",1);
		Activity facade =    s.activity("facade   ",2);
		Activity garden =    s.activity("garden   ",1);
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

		/* Building the house now requires $1,000 for each s.activity per day.
		   $13,000 are available on the first day.
		   Additional $16,000 becomes available only on day 15. 
		*/
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
			s.log("=== POST: "+activity); //.getName() + " requires $" + consumptionPerDay + " per day");
			activity.requires(budget, consumptionPerDay);
			s.log(budget.toString());
		}
		
		s.logActivities();
		s.log(budget.toString());
	}
	
	public void solve() {

		Solver solver = s.getSolver();
		solver.setSearchStrategy(s.strategyScheduleActivities());
		solver.addSearchStrategy(s.strategyAssignResources());
		Solution solution = solver.findSolution();
		if (solution == null)
			s.log("No solutions");
		else {
			s.log(solution);
		}
			
	}
	
	public static void main(String args[]) throws Exception {
		ScheduleActivitiesBudget p = new ScheduleActivitiesBudget();
		p.define();
		p.solve();		
	}
}
