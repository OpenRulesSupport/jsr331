//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//============================================= 
package javax.constraints.scheduler.impl;

import javax.constraints.scheduler.ResourceDisjunctive;
import javax.constraints.scheduler.ResourceType;

/**
 * This is an interface for discrete resources of capacity 1 (one)
 * introduced for reasons of expressiveness and efficiency.
 */

public class BasicResourceBoolean extends BasicResource implements ResourceDisjunctive {

	public BasicResourceBoolean(ScheduleImpl schedule, String name, int timeMin, int timeMax) {
		super(schedule, name, timeMin, timeMax, 1);
		setType(ResourceType.BOOLEAN);
	}

	public BasicResourceBoolean(ScheduleImpl schedule, String name)	{
		super(schedule,name,1);
		setType(ResourceType.BOOLEAN);
	}

	public void setUnavailable(int time) {
		setCapacityMax(time, 0);
	}


	public void setUnavailable(int time1, int time2) {
		setCapacityMax(time1, time2, 0);
	}

}
