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

public class ConstraintConsumeWithDurationLoop extends AbstractConstraintActivityResource {

	Var	index;

	public ConstraintConsumeWithDurationLoop(Activity activity,
			Resource resource, int capacity) {
		super(activity,resource,capacity);
		setType("consumes");
	}

	public ConstraintConsumeWithDurationLoop(Activity activity,
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
		for(int startSlot=from; startSlot <= to; startSlot++) {
            Var activityStartsAtSlot = schedule.linear(activityStartVar,"=",startSlot).asBool();
            String name = activity.getName().trim()+"StartsAt"+startSlot;
            schedule.add(name,activityStartsAtSlot);
            Var consumedCapacity;
            if (getCapacityVar() != null) {
                consumedCapacity = activityStartsAtSlot.multiply(getCapacityVar());
            } else {
                consumedCapacity = activityStartsAtSlot.multiply(getCapacity());
            }
            name = activity.getName().trim()+"["+startSlot+"]Consumes$";
            schedule.add(name, consumedCapacity);
            for(int durationSlot=0; durationSlot < duration; durationSlot++) {
                int time = startSlot + durationSlot;
                consumptionTable.addVar(time,consumedCapacity);
            }
		} 
	}
	
}
