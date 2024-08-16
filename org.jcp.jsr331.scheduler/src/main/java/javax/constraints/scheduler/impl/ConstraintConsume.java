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

		// loop by time intervals
//		Var[] accumulatedConsumedCapacities = p.variableArray(resource.getName()+"-accumulatedCapacities", 
//		                0, resource.getCapacityMax(),resource.getTimeMax()-resource.getTimeMin()+1);
//		int numberOfIntervals = 0;
//		p.post(accumulatedConsumedCapacities[numberOfIntervals],"=",0); // ???
		int resourceStart = resource.getTimeMin();
		int resourceEnd = resource.getTimeMax();
		Var activityStartVar = activity.getStart();
        Var activityEndVar = activity.getEnd();
        		
		ConsumptionTable consumptionTable = resource.getConsumptionTable();
		int from = activityStartVar.getMin();
		int to = activityEndVar.getMax();
		int duration = activity.getDuration();
		int index = 0;
		for(int time=from; time < to; time++) {
            String name = activity.getName().trim()+"CanCoverTime"+time;
            Var activityCanCoverTime = schedule.variable(name,0,1);
            Constraint  timeIsInsideActivity = 
                schedule.linear(activityStartVar,"<=",time).and(schedule.linear(activityEndVar,">",time));
            schedule.postIfThen(timeIsInsideActivity, schedule.linear(activityCanCoverTime,"=",1));
            Constraint  timeIsNotInsideActivity = 
                    schedule.linear(activityStartVar,">",time).or(schedule.linear(activityEndVar,"<=",time));
            schedule.postIfThen(timeIsNotInsideActivity, schedule.linear(activityCanCoverTime,"=",0));
            
            Var consumedCapacity;
            if (getCapacityVar() != null) {
                consumedCapacity = activityCanCoverTime.multiply(getCapacityVar());
            } else {
                consumedCapacity = activityCanCoverTime.multiply(getCapacity());
            }
            
            name = activity.getName().trim()+"CanConsumedAtTime"+index;
            index++;
            if (index > duration-1)
                index = 0;
            schedule.add(name, consumedCapacity);
            //p.log("activityCoversTime="+activityCoversTime);
            consumptionTable.addVar(time,consumedCapacity);
		} 
	}
	
}
