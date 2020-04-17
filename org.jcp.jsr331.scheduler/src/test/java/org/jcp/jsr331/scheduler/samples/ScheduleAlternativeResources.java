package org.jcp.jsr331.scheduler.samples;

import javax.constraints.*;
import javax.constraints.scheduler.*;

public class ScheduleAlternativeResources {
	
	Schedule s = ScheduleFactory.newSchedule("ScheduleAlternativeResources",0,40);

	Activity[] activities;
	ResourceDisjunctive[] resources;

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

		for (int i = 0; i < s.getActivities().size(); ++i) {
			Activity job = (Activity) s.getActivities().elementAt(i);
			s.log(job.toString());
		}

		// Define Resources
		ResourceDisjunctive joe  = s.resourceDisjunctive("Joe");
		ResourceDisjunctive jim  = s.resourceDisjunctive("Jim");
		ResourceDisjunctive jack = s.resourceDisjunctive("Jack");
		resources = new ResourceDisjunctive[] { joe, jim, jack };

		// Posting constraints for alternative resources
		masonry.requires(joe, jack);
		carpentry.requires(joe, jim);
		plumbing.requires(jack);
		ceiling.requires(joe, jim);
		roofing.requires(joe, jim);
		painting.requires(jack, jim);
		windows.requires(joe, jim);
		garden.requires(joe, jack, jim);
		facade.requires(joe, jack);
		movingIn.requires(joe, jim);
		s.logActivities();
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
		solver.logStats();
	}

	public static void main(String args[]) throws Exception {
		ScheduleAlternativeResources schedule = new ScheduleAlternativeResources();
		schedule.define();
		schedule.solve();
	}
}
