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
		for(int time=resource.getTimeMin(); time < resource.getTimeMax(); time++) {
			//p.log("time="+time);
			VectorVar vars = new VectorVar();
			// loop by resource constraints related to i-th interval
			for(Constraint constraint : resource.getActivityConstraints()) {
                ConstraintActivityResource requireConstraint = (ConstraintActivityResource)constraint;
                Activity activity = requireConstraint.getActivity();
                Var startVar = activity.getStart();
                Var endVar = activity.getEnd();
                //if (startVar.getMin() <= time && time < endVar.getMax()) { // activity may cover the i-th interval
                if (startVar.getMin() <= time) { // activity may cover the i-th interval
                    Var activityCoversTime = schedule.createVariable("noname",0,1);
                    //p.remove("noname");
                    Constraint  timeIntervalIsInsideActivity = 
                        //p.linear(startVar,"<=",time).and(p.linear(endVar,">",time));
                        schedule.linear(startVar,"<=",time);
                    schedule.postIfThen(timeIntervalIsInsideActivity, schedule.linear(activityCoversTime,"=",1));
                    if (activityCoversTime.getMax() > 0) {
                        Var requiredCapacity;
                        if (requireConstraint.getCapacityVar() != null) {
                            requiredCapacity = activityCoversTime.multiply(requireConstraint.getCapacityVar());
                        } else {
                            requiredCapacity = activityCoversTime.multiply(requireConstraint.getCapacity());
                        }
                        //p.log("activityCoversTime="+activityCoversTime);
                        requiredCapacity.setName(activity.getName());
                        vars.addElement(requiredCapacity);
                    }
                }
            } // end of loop by resource's Activity Constraints

			if (vars.size() > 0) {
//				Var requiredCapacitySum = getProblem().sum(vars.toArray());
//				Var totalRequiredCapacity = requiredCapacitySum.plus(accumulatedConsumedCapacities[numberOfIntervals]);
//				try {
//				   p.post(totalRequiredCapacity,"<=",resource.getCapacityVar(time));
//				   numberOfIntervals++;
//				   p.post(accumulatedConsumedCapacities[numberOfIntervals],"=",totalRequiredCapacity);
//				}
			    Var requiredCapacitySum = getProblem().sum(vars.toArray());
                try {
                   schedule.post(requiredCapacitySum,"<=",resource.getCapacityVar(time));
                }
				catch(Exception e) {
					String msg = "Resource " + resource.getName() + " doesn't have enough capacity on the interval " + time
					  + ": available="+resource.getCapacityVar(time) + " required=" + requiredCapacitySum + " by activities:";
					String conflictingActivity = null;
					for(int v=0; v<vars.size(); v++) {
						String name = vars.elementAt(v).getName();
						msg += "\n\t"+ name;
						if (!name.equals(activity.getName()))
							conflictingActivity = name;
					}
					if (conflictingActivity != null)
						setFailureReason("Conflict with "+conflictingActivity);
					schedule.log("Failure to post ResourceConstraintConsumes:\n"+ msg);
					activity.getResourceConstraints().remove(this);
					resource.getActivityConstraints().remove(this);
					throw new RuntimeException("Failure to post ConstraintRequire:\n"+ msg);
				}
			}
		} // end of time loop
	}
	
}
