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

import javax.constraints.Var;

/**
 * This is an interface for scheduling object known as reservoir.
 * ResourceReservoir represents a resource for which activities can both 
 * <i>provide</i> capacity and also <i>require</i> capacity. 
 * ResourceReservoir defines required and provided capacity in a such way
 * that it is impossible to use more capacity than provided.
 * 
 */

public interface ResourceReservoir extends Resource  {

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
	public Vector getActivityConstraints();
	
}
