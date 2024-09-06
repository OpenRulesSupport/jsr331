//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//============================================= 
package javax.constraints.scheduler.impl;

import javax.constraints.Var;
import javax.constraints.impl.AbstractConstraint;
import javax.constraints.scheduler.Activity;
import javax.constraints.scheduler.Resource;
import javax.constraints.scheduler.ConstraintActivityResource;

/**
 * This is a base class for different resource constraints such as
 * activity.requires(resource,capacity) where capacity
 * may be an integer value or Var variable.
 */

abstract public class AbstractConstraintActivityResource extends AbstractConstraint implements ConstraintActivityResource {

	Activity 	activity;
	Resource 	resource;
	int 		capacity;
	Var 		capacityVar;
	String 		failureReason;
	String 		type;
	Var         assignmentVar;

	public AbstractConstraintActivityResource(Activity activity,
			                   Resource resource,
			                   int capacity) {
		super(activity.getSchedule());
		this.capacity = capacity;
		capacityVar = null;
		this.activity = activity;
		this.resource = resource;
	}

	public AbstractConstraintActivityResource(Activity activity,
			                      Resource resource,
			                      Var capacityVar) {
		super(activity.getSchedule());
		this.capacityVar = capacityVar;
		this.capacity = -1; // to differentiate from another constructor
		this.activity = activity;
		this.resource = resource;
		this.assignmentVar = null;
	}

	public Activity getActivity() {
		return activity;
	}

	public int getCapacity() {
		return capacity;
	}

	public javax.constraints.Var getCapacityVar() {
		return capacityVar;
	}

	public Resource getResource() {
		return resource;
	}

	public String getFailureReason() {
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Var getAssignmentVar() {
	    return assignmentVar;
	};
    public void setAssignmentVar(Var assignmentVar) {
        this.assignmentVar = assignmentVar;
    }
}
