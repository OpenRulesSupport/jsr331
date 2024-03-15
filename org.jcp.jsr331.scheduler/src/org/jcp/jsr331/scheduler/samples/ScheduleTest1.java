package org.jcp.jsr331.scheduler.samples;

import javax.constraints.*;
import javax.constraints.scheduler.*;

public class ScheduleTest1 {

	public static void main(String args[]) throws Exception {
		//Variables
		Schedule s = ScheduleFactory.newSchedule("Test", 0, 14);
		Activity a1 = s.activity("a1",7);
		Activity a2 = s.activity("a2",5);
		Activity a3 = s.activity("a3",2);
		ResourceDisjunctive r1 = s.resourceDisjunctive("r1");
		ResourceDisjunctive r2 = s.resourceDisjunctive("r2");

		//Constraints
		s.post(a3,">",a1); 
		s.post(a1,">",a2); 

		a1.requires(r1, r2);

		//Solving
		s.scheduleActivities();
		Solution sol = s.assignResources();
		if (sol == null)
			s.log("no solution found ");
		else
			s.logActivities();
	}
}
