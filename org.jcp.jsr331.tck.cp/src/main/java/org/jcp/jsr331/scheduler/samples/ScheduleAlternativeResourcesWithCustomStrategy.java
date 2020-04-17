package org.jcp.jsr331.scheduler.samples;

import java.util.Vector;

import javax.constraints.*;
import javax.constraints.impl.search.AbstractSearchStrategy;
import javax.constraints.scheduler.*;

public class ScheduleAlternativeResourcesWithCustomStrategy {
	
	Schedule s = ScheduleFactory.newSchedule("ScheduleAlternativeResourcesWithCustomStrategy",0,40);

	Activity[] activities;
	ResourceDisjunctive[] resources;

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

		activities = new Activity[] { masonry, carpentry, roofing, plumbing,
			ceiling, windows, facade, garden, painting, movingIn 
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

		for (int i = 0; i < s.getActivities().size(); ++i) {
			Activity job = (Activity) s.getActivities().elementAt(i);
			s.log(job.toString());
		}

		// Define Resources
		ResourceDisjunctive joe  = s.resourceDisjunctive("Joe");
		ResourceDisjunctive jim  = s.resourceDisjunctive("Jim");
		ResourceDisjunctive jack = s.resourceDisjunctive("Jack");
		resources = new ResourceDisjunctive[] { joe, jim, jack };

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

	}

	public void solve()  {

		// Define attendance
		Var[] attendanceVars = new Var[resources.length];
		for (int i = 0; i < resources.length; i++) {
			//attendanceVars[i] = attendance(resources[i]);
			attendanceVars[i] = s.sum(resources[i].getCapacities());
		}
		Var objective = s.sum(attendanceVars);
		s.add("Total Attendance", objective);

		Solver solver = s.getSolver();
		solver.setSearchStrategy(s.strategyScheduleActivities());
		solver.addSearchStrategy(s.strategyAssignResources());
		solver.addSearchStrategy(new LogSolution(solver));
		
		Solution solution = solver.findOptimalSolution(objective);
		
		solver.logStats();
		
		// TODO: optimalSolution search does not stop?
//		solver.setTimeLimit(1000);
//		Solution solution = solver.findOptimalSolution(objective);
//		if (solution == null) {
//			s.log("No Solutions");
//		} else {
//			s.log("=============== Final Solution:");
//			s.logActivities();
//			s.log("Total attendance: " + objective);
//		}
		
		// TODO: different solutions look the same?
//		SolutionIterator iter = solver.solutionIterator();
//		int n = 0;
//		while(iter.hasNext()) {
//			Solution solution = iter.next();
//			n++;
//			s.log("=============== Solution:" + n);
//			s.log("Total attendance: " + solution.getValue("Total Attendance"));
//			if (n>4)
//				break;
//		}
	}
	
	public Var attendance(Resource res) {
		Vector<Constraint> constraints = res.getActivityConstraints();
		Var[] requiredCapacities =  new Var[constraints.size()];
		for(int i=0; i< constraints.size(); i++) {
			ConstraintActivityResource rc = 
				(ConstraintActivityResource)constraints.elementAt(i);
			if (rc.getCapacity() == -1)
				requiredCapacities[i] = rc.getCapacityVar();
			else
				requiredCapacities[i] = s.variable(1,1);
		}
		return s.sum(requiredCapacities);
	}

	public static void main(String args[]) throws Exception {
		ScheduleAlternativeResourcesWithCustomStrategy schedule = new ScheduleAlternativeResourcesWithCustomStrategy();
		schedule.define();
		schedule.solve();
	}
	
	class LogSolution extends AbstractSearchStrategy {
		
		public LogSolution(Solver s) {
			super(s);
			setType(SearchStrategyType.CUSTOM);
		}
		
		public boolean run() {
			s.log("======================== LogSolution:");
			s.logActivities();
			s.logResources();
			s.log("Total attendance: " + s.getVar("Total Attendance"));
			return true;
		}
	}
}
