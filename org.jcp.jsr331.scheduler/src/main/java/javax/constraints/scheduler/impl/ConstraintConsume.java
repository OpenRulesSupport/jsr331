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
//import javax.constraints.impl.Problem;
import javax.constraints.Var;
import javax.constraints.scheduler.Activity;
import javax.constraints.scheduler.ConstraintActivityResource;
import javax.constraints.scheduler.Resource;
import javax.constraints.scheduler.ConsumptionTable;

public class ConstraintConsume extends AbstractConstraintActivityResource {

	Var	index;

	public ConstraintConsume(Activity activity,
			Resource resource, int capacity) {
		super(activity,resource,capacity);
		setType("consumes");
	}

	public ConstraintConsume(Activity activity,
			Resource resource, Var capacityVar) {
		super(activity,resource,capacityVar);
		setType("consumes");
	}
	
	/**
	 * Posts this constraint by posting capacity constraints for every resource interval:
	 * sum of all required capacities by activities that may cover this interval should be less or equal
	 * to the available resource capacity on this interval
	 */
	public void post() {
		ScheduleImpl schedule = (ScheduleImpl)getProblem();
		if (activity.getStartMin() > resource.getTimeMax()
				|| activity.getEndMax() < resource.getTimeMin()) {
			String msg = "Resource " + resource.getName() + " and Activity "
					+ activity.getName() + " do not overlap";
			schedule.log(this + " Failure to post ConstraintConsume: " + msg);
			activity.getResourceConstraints().remove(this);
			resource.getActivityConstraints().remove(this);
			setFailureReason(msg);
			throw new RuntimeException("Failure to post ConstraintConsume: " + msg);
		}

		Var activityStartVar = activity.getStart();
		ConsumptionTable consumptionTable = resource.getConsumptionTable();
		int from = activityStartVar.getMin();
		int to = activityStartVar.getMax();
		int duration = activity.getDuration();
		for(int time=from; time <= to; time++) {
            Var activityStartsAtTime = schedule.linear(activityStartVar,"=",time).asBool();
            String name = activity.getName().trim()+"StartsAt"+time;
            schedule.add(name,activityStartsAtTime);
            Var consumedCapacity;
            if (getCapacityVar() != null) {
                consumedCapacity = activityStartsAtTime.multiply(getCapacityVar());
            } else {
                consumedCapacity = activityStartsAtTime.multiply(getCapacity());
            }
            name = activity.getName().trim()+"["+time+"]Consumes$";
            schedule.add(name, consumedCapacity);
            consumptionTable.consume(time,consumedCapacity,duration);
		} 
		consumptionTable.postConstraints();
	}
	
}
