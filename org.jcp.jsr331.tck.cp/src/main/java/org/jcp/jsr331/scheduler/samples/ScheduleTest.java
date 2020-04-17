package org.jcp.jsr331.scheduler.samples;

import java.util.Vector;

import javax.constraints.*;
import javax.constraints.scheduler.*;

public class ScheduleTest {

	public static void main(String args[]) throws Exception {
		//Variables
		Schedule s = ScheduleFactory.newSchedule("Test", 0, 14);
		Activity a1 = s.activity("a1",7);
		Activity a2 = s.activity("a2",5);
		Activity a3 = s.activity("a3",2);
		Resource r1 = s.resourceDisjunctive("r1");
		Resource r2 = s.resourceDisjunctive("r2");

		//Constraints
		s.post(a3,">",a1); //a3.startsAfterEnd(a1).post();
		s.post(a1,">",a2); //a1.startsAfterEnd(a2).or(a2.startsAfterEnd(a3)).post();

		Var cV1 = s.variable("cV1",0, 1);
		Var cV2 = s.variable("cV2",0, 1);
		a1.requires(r1, cV1).post();
		a1.requires(r2, cV2).post();
		//s.post(cV1.plus(cV2),"=",1);
		s.post(cV1,cV2,"=",1);

//		a1.requires(r1, 1).or(a1.requires(r2,1 )).post();

		try{
			s.post(r1.getCapacities(),">=",12);
		}catch (Exception e) {
			System.out.println("Exception during posting constraint");
		}

		//Solving
		Vector<Var> varsVec =  new Vector<Var>();
		for(int i=0; i<s.getActivities().size(); i++)
			varsVec.add(((Activity)s.getActivities().elementAt(i)).getStart());
		for (int i = 0; i < s.getResources().size(); i++) {
			for (int j = 0; j < ((Resource)s.getResources().get(i)).getDuration(); j++) {
				varsVec.add(((Resource)s.getResources().get(i)).getCapacityVar(j));
			}
		}
		Var[] vars = new Var[varsVec.size()];
		for (int i = 0; i < vars.length; i++) {
			vars[i] = varsVec.elementAt(i);
		}
		Solver solver = s.getSolver();
		solver.setSearchStrategy(vars);
		Solution sol = solver.findSolution();
		if (sol == null)
			System.out.println("no solution found ");
		else{
			System.out.println("solution is");
			for(int i = 0; i < s.getActivities().size(); i++){
				Activity act = (Activity)s.getActivities().elementAt(i);
				System.out.println(act);
			}
			System.out.println(r1.toString());
			System.out.println(r2.toString());
		}
	}
}
