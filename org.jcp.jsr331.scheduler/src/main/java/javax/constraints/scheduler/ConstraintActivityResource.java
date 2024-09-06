package javax.constraints.scheduler;

import javax.constraints.Constraint;
import javax.constraints.Var;

/**
 * This is an interface for different resource constraints.
 */

public interface ConstraintActivityResource extends Constraint {

	public Activity getActivity();

	public int getCapacity();

	public Var getCapacityVar();

	public Resource getResource();

	public String getFailureReason();
	public void setFailureReason(String failureReason);
	
	public String getType();
	public void setType(String type);
	
	public Var getAssignmentVar();
	public void setAssignmentVar(Var assignmentVar);

}
