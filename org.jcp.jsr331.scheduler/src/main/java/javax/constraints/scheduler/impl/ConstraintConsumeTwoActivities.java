//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//============================================= 
package javax.constraints.scheduler.impl;

import javax.constraints.Constraint;
import javax.constraints.Var;
import javax.constraints.scheduler.Activity;
import javax.constraints.scheduler.Resource;


public class ConstraintConsumeTwoActivities extends AbstractConstraintActivityResource {

	Constraint constraintRequire1;
	Constraint constraintRequire2;
	ScheduleImpl schedule;

	public ConstraintConsumeTwoActivities(Activity activity, Resource resource, int capacity) {
		super(activity,resource,capacity);
		init();
	}
	
	public ConstraintConsumeTwoActivities(Activity activity, Resource resource, Var capacityVar) {
        super(activity,resource,capacityVar);
        init();
    }
	
	public void init() {
	    schedule = (ScheduleImpl)getProblem();
        setType("consumes");
        constraintRequire1 = new ConstraintRequire(activity, resource, capacity);
        Activity activityConsumer = schedule.activity(activity.getName()+"Consumer", 
                                          schedule.getDuration()-activity.getDuration()-1);
        schedule.post(activityConsumer.getStart(),"=",activity.getEnd().plus(1));
        constraintRequire2 = new ConstraintRequire(activityConsumer, resource, capacity);
	}

	/**
	 * Posts this constraint by posting capacity constraints for every resource interval:
	 * sum of all required capacities by activities that may cover this interval should be less or equal
	 * to the available resource capacity on this interval
	 */
	public void post() {
	    constraintRequire1.post();
	    constraintRequire2.post();
	}
	
}
