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
import javax.constraints.scheduler.ResourceType;

public class ConstraintRequire extends AbstractConstraintActivityResource {

//	Var	index;

	public ConstraintRequire(Activity activity,
			Resource resource, int capacity) {
		super(activity,resource,capacity);
		setType("requires");
	}

	public ConstraintRequire(Activity activity,
			Resource resource, Var capacityVar) {
		super(activity,resource,capacityVar);
		setType("requires");
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
			schedule.log(this + " Failure to post ConstraintRequire: " + msg);
			activity.getResourceConstraints().remove(this);
			resource.getActivityConstraints().remove(this);
			setFailureReason(msg);
			throw new RuntimeException("Failure to post ConstraintRequire: " + msg);
		}

//		String consumePrefix = "_consumer_for_";
		// loop by time intervals
		for(int time=resource.getTimeMin(); time < resource.getTimeMax(); time++) {
			//p.log("i="+i);
			VectorVar vars = new VectorVar();
			// loop by resource constraints related to i-th interval
			for(Constraint constraint : resource.getActivityConstraints()) {
			    ConstraintActivityResource requireConstraint = (ConstraintActivityResource)constraint;
				Activity activity = requireConstraint.getActivity();
//				if (activity.getName().startsWith(consumePrefix))
//				    continue;
				Var startVar = activity.getStart();
				Var endVar = activity.getEnd();
				if (startVar.getMin() <= time && time < endVar.getMax()) { // activity may cover the time-th interval
					Var activityWithin = schedule.createVariable("noname",0,1);
					//p.remove("noname");
					Constraint 	activityIsInsideInterval = 
						schedule.linear(startVar,"<=",time).and(schedule.linear(endVar,">",time));
					schedule.postIfThen(activityIsInsideInterval, schedule.linear(activityWithin,"=",1));
					if (activityWithin.getMax() > 0) {
						Var requiredCapacity;
						if (requireConstraint.getCapacityVar() != null) {
							requiredCapacity = activityWithin.multiply(requireConstraint.getCapacityVar());
						} else {
							requiredCapacity = activityWithin.multiply(requireConstraint.getCapacity());
						}
						//p.log(""+activity + " activityWithin="+activityWithin);
						requiredCapacity.setName(schedule.resourceUsageByActivityName(resource, activity)+" at "+time);
						schedule.getScheduleVars().add(requiredCapacity);
						vars.addElement(requiredCapacity);
						if (time == startVar.getMin()) {
						    setAssignmentVar(requiredCapacity);
						}
					}
				}
			} // end of loop by resource's Activity Constraints

			if (vars.size() > 0) {
			    postTimeCapacityConstraint(resource, time, vars);
//				if ( (i < resource.getTimeMax()-1) && resource.getType().equals(ResourceType.CONSUMABLE)) {
//				    for(int j=i+1; j < resource.getTimeMax(); j++) {
//				        postTimeCapacityConstraint(resource, j, vars);
//				    }
//		        }
			}
		} // end of i loop
		
	}
	
	void postTimeCapacityConstraint(Resource resource, int time, VectorVar vars) {
	    ScheduleImpl schedule = (ScheduleImpl)getProblem();
	    Var requiredCapacitySum = getProblem().sum(vars.toArray());
	    try {
//	        schedule.log("postTimeCapacityConstraint: time = " + time + " " + requiredCapacitySum 
//	                +  " <= " + resource.getCapacityVar(time));
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
             schedule.log("Failure to post ResourceConstraintRequires:\n"+ msg);
             schedule.log(activity.toString());
             activity.getResourceConstraints().remove(this);
             resource.getActivityConstraints().remove(this);
             throw new RuntimeException("Failure to post ConstraintRequire:\n"+ msg);
         }
	}
	
}
