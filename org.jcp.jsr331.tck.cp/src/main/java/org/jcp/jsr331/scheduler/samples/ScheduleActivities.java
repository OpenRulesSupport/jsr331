package org.jcp.jsr331.scheduler.samples;

import javax.constraints.Objective;
import javax.constraints.Solution;
import javax.constraints.Solver;
import javax.constraints.Var;
import javax.constraints.scheduler.Activity;
import javax.constraints.scheduler.Schedule;
import javax.constraints.scheduler.ScheduleFactory;

public final class ScheduleActivities {
	
	Schedule s = ScheduleFactory.newSchedule("ScheduleActivities",0,40);
	
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

		s.logActivities();
	}
	
	public void solve() {
		s.log("Find A Feasible Solution...");
		Solution solution = s.scheduleActivities();
		if (solution == null)
			s.log("No solutions");
		else {
//			s.log("SOLUTION:");
			s.logActivities();
			s.log(solution);
		}
			
	}
	
	public void solveOptimal() {
		s.log("Find Optimal Solution...");
		Solver solver = s.getSolver();
		solver.setSearchStrategy(s.strategyScheduleActivities());
		solver.setMaxNumberOfSolutions(10);
		solver.setTimeLimitGlobal(15000);
		//solver.traceSolutions(true);
		Var objective = s.getActivity("moving in").getStart();
		Solution solution = solver.findOptimalSolution(Objective.MINIMIZE,objective);
		if (solution == null)
			s.log("No solutions");
		else {
			s.log(solution);
		}
			
	}
	
	public void solveMany() {
		s.log("Find Multiple Solutions...");
		Solver solver = s.getSolver();
		solver.setSearchStrategy(s.strategyScheduleActivities());
		solver.setMaxNumberOfSolutions(3);
		//solver.setTimeLimitGlobal(15000);
		//solver.traceSolutions(true);
		s.log(s.getVars());
		Solution[] solutions = solver.findAllSolutions();
		if (solutions == null)
			s.log("No solutions");
		else {
			for (int i = 0; i < solutions.length; i++) {
				s.log(solutions[i]);
			}
		}
	}
	
	public static void main(String args[]) throws Exception {
		ScheduleActivities p = new ScheduleActivities();
		p.define();
//		p.solve();
//		p.solveOptimal();
		p.solveMany();
	}
}
