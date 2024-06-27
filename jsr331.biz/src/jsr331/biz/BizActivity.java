package jsr331.biz;

/*----------------------------------------------------------------
 * Copyright Cork Constraint Computation Centre, 2006-2007
 * University College Cork, Ireland, www.4c.ucc.ie
 *---------------------------------------------------------------*/

/**
 * This class represents business activity for a scheduling problem.
 * @author jacob
 *
 */

import javax.constraints.CSP;
import javax.constraints.schedule.Activity;
import javax.constraints.schedule.Schedule;

public class BizActivity extends BizObject {

	private Activity activity;

	public BizActivity(BizProblem problem, String name, int duration) {
		super(problem, name);
		CSP csp = problem.getCsp();
		javax.constraints.impl.CSPImpl p = (javax.constraints.impl.CSPImpl) csp;
		Schedule schedule = p.getSchedule();
		activity = schedule.addActivity(name, duration);
		saveDomain();
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public int getDuration() {
		return activity.getDuration();
	}

	/**
	 * This method should be defined by an adapter to a concrete solver
	 * implementation. It saves the current state of the object's domain
	 * as a String.
	 */
	@Override
	public void saveDomain() {
		setDomain(activity.toString());
	}



	/**
	 * @return true if the domain of the variable contains only one value
	 */
	@Override
	public boolean isBound() {
		return activity.getStart().isBound();
	}

	/**
	 * This method is used by a selected CP implementation to save the values
	 * of all variables after finding the problem solution
	 * @param value
	 */
	@Override
	public void saveValue() {
		setValue(activity.toString());
	}

	@Override
	public String getValue() {
		if (getProblem().getNumberOfFoundSolutions() == 0) {
			//getProblem().log("Attempt to get value of unbound constrained object "+getName());
			return "";
		}
		return activity.toString();
	}

	@Override
	public String toString() {

		return activity.toString();
	}

}
