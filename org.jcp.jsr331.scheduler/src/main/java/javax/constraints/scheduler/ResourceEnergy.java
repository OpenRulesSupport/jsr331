//=============================================
// J A V A  C O M M U N I T Y  P R O C E S S
// 
// J S R  3 3 1
// 
// Specification
// 
//============================================= 
package javax.constraints.scheduler;

import java.util.Vector;

import javax.constraints.Constraint;
import javax.constraints.Var;

/**
 * This is an interface represents a resource available as a certain 
 * amount of energy (for example, in watt-hours,
 * in human-months) over certain time buckets (for example, minutes, 
 * hours, months, years). The available energy of a time bucket is 
 * used by the activities executed on that time bucket, and as a 
 * consequence, energy capacity constraints use the energy of the discrete
 * energy resource. <br>
 * For example, let's assume that each unit of time corresponds to an hour, 
 * and that we have defined a discrete energy resource that has a time 
 * step of 24 (corresponding to a day), and energy 10. Then if we have 
 * an activity of duration 3 (hours) that requires the resource with 
 * capacity 2 (machines), it uses energy of 6 (machine-hours), and thus 
 * if this activity is scheduled on the first day, the remaining energy 
 * for that first day is 4 (machine-hours). <br>
 * 
 */

public interface ResourceEnergy extends Resource {

	public Var[] getCapacities();

	/**
	 * Sets resource maximum capacity at the specified moment of time.
	 * 
	 * @param time
	 *            Time moment for new capacity
	 * @param capacity
	 *            New capacity
	 * @throws Exception
	 */
	public void setCapacityMax(int time, int capacity) throws Exception;

	/**
	 * Sets resource minimum capacity at the specified moment of time.
	 * 
	 * @param time
	 *            Time moment for new capacity
	 * @param capacity
	 *            New capacity
	 * @throws Exception
	 */
	public void setCapacityMin(int time, int capacity) throws Exception;
	
	/**
	 * Sets resource maximum capacity at the specified interval of time
	 * [time1;time2)
	 * 
	 * @param time1
	 *            A start time
	 * @param time2
	 *            An end time
	 * @param capacity
	 *            New capacity
	 * @throws Failure
	 */
	public void setCapacityMax(int time1, int time2, int capacity)
			throws Exception;
	
	/**
	 * Sets resource minimum capacity at the specified interval of time [time1;
	 * time2)
	 * 
	 * @param time1
	 *            A start time
	 * @param time2
	 *            An end time
	 * @param capacity
	 *            New capacity
	 * @throws Failure
	 */
	public void setCapacityMin(int time1, int time2, int capacity)
			throws Exception;
	
	/**
	 * Returns resource maximum capacity at the specified moment of time
	 * 
	 * @param time
	 *            Time for capacity query
	 * @throws Failure
	 * @return Capacity
	 */
	public int getCapacityMax(int time);

	/**
	 * Returns resource minimum capacity at the specified moment of time
	 * 
	 * @param time
	 *            Time for capacity query
	 * @throws Failure
	 * @return Capacity
	 */
	public int getCapacityMin(int time);
	/**
	 * @return Resource availability durarion
	 */
	public int getDuration();

	/**
	 * Returns internal Var variable associated with capacity at specified
	 * moment of time
	 * 
	 * @param time
	 *            Moment of time
	 * @return Capacity variable
	 * @throws Exception
	 */
	public Var getCapacityVar(int time);
	
	/**
	 * Returns resource availability start time
	 * 
	 * @return Resource availability start time
	 */
	public int getTimeMin();

	/**
	 * Returns resource availability end time
	 * 
	 * @return Resource availability end time
	 */
	public int getTimeMax();

	/**
	 * 
	 * @return a vector of all ResourceConstraint objects
	 * associated with this resource
	 */
	public Vector<Constraint> getActivityConstraints();
}
