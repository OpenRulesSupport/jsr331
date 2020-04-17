//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//============================================= 
package javax.constraints.scheduler.impl;

import javax.constraints.scheduler.Schedule;


/**
 * This is a base for activities and resources
 */


public class SchedulingObject {
	
	String name;
	Schedule schedule;
	
	public SchedulingObject(Schedule schedule) {
		this.schedule = schedule;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Schedule getSchedule() {
		return schedule;
	}
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

}
