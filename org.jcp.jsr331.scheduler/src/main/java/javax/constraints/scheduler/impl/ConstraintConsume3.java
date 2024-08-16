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

public class ConstraintConsume3 extends AbstractConstraintActivityResource {

	Var	index;

	public ConstraintConsume3(Activity activity,
			Resource resource, int capacity) {
		super(activity,resource,capacity);
		setType("consumes");
	}

	public ConstraintConsume3(Activity activity,
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
		int start = resource.getTimeMin();
		int end = resource.getTimeMax();
		String consumedActivityName = activity.getName()+"PotentiallyConsumedCapacityAtTime-";
		
		for(int time=start; time < end; time++) {
			//p.log("time="+time);
		    VectorVar consumptionVars = new VectorVar();
			// loop by resource constraints related to i-th interval
		    if (time > start) {
                Var previouslyConsumedVar = schedule.getVar(consumedActivityName+(time-1));
                if (previouslyConsumedVar != null) {
                    schedule.log("Time " + time +": Add " + previouslyConsumedVar);
                    //consumptionVars.add(previouslyConsumedVar);
                    postTimeCapacityConstraint(resource,time,previouslyConsumedVar);
                }
            }
			for(Constraint currentConstraint : resource.getActivityConstraints()) {
                ConstraintActivityResource constraint = (ConstraintActivityResource)currentConstraint;
                Activity constraintActivity = constraint.getActivity();
                Var startVar = constraintActivity.getStart();
                Var endVar = constraintActivity.getEnd();
                if (startVar.getMin() <= time && time < endVar.getMax()) { // activity may cover the i-th interval
                    Var activityCoversTime = schedule.createVariable("noname",0,1);
                    //p.remove("noname");
                    Constraint  timeIntervalIsInsideActivity = 
                        schedule.linear(startVar,"<=",time).and(schedule.linear(endVar,">",time));
                    
                    schedule.postIfThen(timeIntervalIsInsideActivity, schedule.linear(activityCoversTime,"=",1));
                    
                    if (activityCoversTime.getMax() > 0) {
                        Var consumedCapacity;
                        if (constraint.getCapacityVar() != null) {
                            consumedCapacity = activityCoversTime.multiply(constraint.getCapacityVar());
                        } else {
                            consumedCapacity = activityCoversTime.multiply(constraint.getCapacity());
                        }
                        //p.log("activityCoversTime="+activityCoversTime);
                        consumedCapacity.setName(constraintActivity.getName()+time);
                        consumptionVars.addElement(consumedCapacity);
                    }
                }
            } // end of loop by resource's Activity Constraints

			if (consumptionVars.size() > 0) {
//				Var requiredCapacitySum = getProblem().sum(vars.toArray());
//				Var totalRequiredCapacity = requiredCapacitySum.plus(accumulatedConsumedCapacities[numberOfIntervals]);
//				try {
//				   p.post(totalRequiredCapacity,"<=",resource.getCapacityVar(time));
//				   numberOfIntervals++;
//				   p.post(accumulatedConsumedCapacities[numberOfIntervals],"=",totalRequiredCapacity);
//				}
			    
//			    if (time > start) {
//	                Var previouslyConsumedVar = schedule.getVar(consumedActivityName+(time-1));
//	                if (previouslyConsumedVar != null) {
//	                    schedule.log("Time " + time +": Add " + previouslyConsumedVar);
//	                    consumptionVars.add(previouslyConsumedVar);
//	                }
//	            }
			    Var potentiallyConsumedCapacityAtTime = getProblem().sum(consumptionVars.toArray());
			    schedule.add(consumedActivityName+time, potentiallyConsumedCapacityAtTime);			    
			    postTimeCapacityConstraint(resource,time,potentiallyConsumedCapacityAtTime);
			}
		} // end of time loop
	}
	
	void postTimeCapacityConstraint(Resource resource, int time, Var consumedCapacity) {
        ScheduleImpl schedule = (ScheduleImpl)getProblem();
        try {
          schedule.log("postTimeCapacityConstraint: time = " + time + " " + consumedCapacity 
                  +  " <= " + resource.getCapacityVar(time));
            schedule.post(consumedCapacity,"<=",resource.getCapacityVar(time));
         }
         catch(Exception e) {
             String msg = "Resource " + resource.getName() + " doesn't have enough capacity on the interval " + time
               + ": available="+resource.getCapacityVar(time) + " consumes=" + consumedCapacity + " by activities:";
             schedule.log("Failure to post ResourceConstraintRequires:\n"+ msg);
             schedule.log(activity.toString());
             activity.getResourceConstraints().remove(this);
             resource.getActivityConstraints().remove(this);
             throw new RuntimeException("Failure to post ConstraintConsume:\n"+ msg);
         }
    }
	
}
