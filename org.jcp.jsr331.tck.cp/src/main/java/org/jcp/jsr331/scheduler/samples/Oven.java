package org.jcp.jsr331.scheduler.samples;

import javax.constraints.*;
import javax.constraints.scheduler.*;

public final class Oven {
	
	Schedule s = ScheduleFactory.newSchedule("oven",0,11);
	
	public void define() throws Exception { 
		
		Activity A = s.activity("A",1);
		Activity B = s.activity("B",4);
		Activity C = s.activity("C",4);
		Activity D = s.activity("D",2);
		Activity E = s.activity("E",4);

		Resource oven = s.resource("oven",3);

		oven.setCapacityMax(0, 2);
		oven.setCapacityMax(1, 1);
		oven.setCapacityMax(2, 0);
		oven.setCapacityMax(3, 1);
		oven.setCapacityMax(4, 1);
		oven.setCapacityMax(10, 1);

		A.requires(oven, 2);
		B.requires(oven, 1);
		C.requires(oven, 1);
		D.requires(oven, 1);
		E.requires(oven, 2);
	}
	
	public void solve() {

		Solver solver = s.getSolver();
		solver.setSearchStrategy(s.strategyScheduleActivities());
		solver.addSearchStrategy(s.strategyAssignResources());
		Solution solution = solver.findSolution();
		if (solution == null) {
			s.log("No Solutions");
		}
		else {
			s.log("Solution:"); 
			s.logActivities();
			s.logResources();
		}
		solver.logStats();
		/*
		 * A[5 -- 1 --> 6) requires resource oven[2] 
		 * B[3 -- 4 --> 7) requires resource oven[1] 
		 * C[7 -- 4 --> 11) requires resource oven[1] 
		 * D[0 -- 2 --> 2) requires resource oven[1] 
		 * E[6 -- 4 --> 10) requires resource oven[2]
		 */
	}
	
	public void solveAll() {
		Solver solver = s.getSolver();
		solver.setSearchStrategy(s.strategyScheduleActivities());
		solver.addSearchStrategy(s.strategyAssignResources());
		Solution[] solutions = solver.findAllSolutions();
		if (solutions == null) {
			s.log("No Solutions");
		}
		else {
			for(Solution solution: solutions) {
				solution.log();
				s.log(solution);
			}
		}
		solver.logStats();
	}
	
	public static void main(String args[]) throws Exception {
		Oven p = new Oven();
		p.define();
		//p.solve();
		p.solveAll();
	}
}
