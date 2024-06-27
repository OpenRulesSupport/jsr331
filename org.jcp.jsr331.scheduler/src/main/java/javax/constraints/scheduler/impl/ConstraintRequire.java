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

public class ConstraintRequire extends AbstractConstraintActivityResource {

	Var	index;

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
		ScheduleImpl p = (ScheduleImpl)getProblem();
		if (activity.getStartMin() > resource.getTimeMax()
				|| activity.getEndMax() < resource.getTimeMin()) {
			String msg = "Resource " + resource.getName() + " and Activity "
					+ activity.getName() + " do not overlap";
			p.log(this + " Failure to post ConstraintRequire: " + msg);
			activity.getResourceConstraints().remove(this);
			resource.getActivityConstraints().remove(this);
			setFailureReason(msg);
			throw new RuntimeException("Failure to post ConstraintRequire: " + msg);
		}

		// loop by time intervals
		for(int i=resource.getTimeMin(); i < resource.getTimeMax(); i++) {
			//p.log("i="+i);
			VectorVar vars = new VectorVar();
			// loop by resource constraints related to i-th interval
			for(Constraint constraint : resource.getActivityConstraints()) {
			    ConstraintActivityResource requireConstraint = (ConstraintActivityResource)constraint;
				Activity activity = requireConstraint.getActivity();
				Var startVar = activity.getStart();
				Var endVar = activity.getEnd();
				if (startVar.getMin() <= i && i < endVar.getMax()) { // activity may cover the i-th interval
					Var activityWithin = p.createVariable("noname",0,1);
					//p.remove("noname");
					Constraint 	activityIsInsideInterval = 
						p.linear(startVar,"<=",i).and(p.linear(endVar,">",i));
					p.postIfThen(activityIsInsideInterval, p.linear(activityWithin,"=",1));
					if (activityWithin.getMax() > 0) {
						Var requiredCapacity;
						if (requireConstraint.getCapacityVar() != null) {
							requiredCapacity = activityWithin.multiply(requireConstraint.getCapacityVar());
						} else {
							requiredCapacity = activityWithin.multiply(requireConstraint.getCapacity());
						}
						//p.log(""+activity + " activityWithin="+activityWithin);
						requiredCapacity.setName(activity.getName());
						vars.addElement(requiredCapacity);
					}
				}
			} // end of loop by resource's Activity Constraints

			if (vars.size() > 0) {
				Var requiredCapacitySum = getProblem().sum(vars.toArray());
				try {
				   p.post(requiredCapacitySum,"<=",resource.getCapacityVar(i));
				}
				catch(Exception e) {
					String msg = "Resource " + resource.getName() + " doesn't have enough capacity on the interval " + i
					  + ": available="+resource.getCapacityVar(i) + " required=" + requiredCapacitySum + " by activities:";
					String conflictingActivity = null;
					for(int v=0; v<vars.size(); v++) {
						String name = vars.elementAt(v).getName();
						msg += "\n\t"+ name;
						if (!name.equals(activity.getName()))
							conflictingActivity = name;
					}
					if (conflictingActivity != null)
						setFailureReason("Conflict with "+conflictingActivity);
					p.log("Failure to post ResourceConstraintRequires:\n"+ msg);
					activity.getResourceConstraints().remove(this);
					resource.getActivityConstraints().remove(this);
					throw new RuntimeException("Failure to post ConstraintRequire:\n"+ msg);
				}
			}
		} // end of i loop
	}
	
}
