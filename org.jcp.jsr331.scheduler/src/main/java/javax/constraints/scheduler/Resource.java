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
import javax.constraints.Problem;
import javax.constraints.Solution;
import javax.constraints.Var;


/**
 * This is an interface for discrete resources with
 * finite capacity that can vary with time.  At any time t, 
 * the capacity represents the number of available units
 * of the resource. Activities may require some amount of
 * the capacity of a resource.
 * 
 */

public interface Resource {

	/**
	 * 
	 * @return a name of this resource
	 */
	public String getName();
	
	/**
	 * Sets a name for this resource
	 * @param name
	 */
	public void setName(String name);
	
	/**
	 * 
	 * @return a type of this resource
	 * @see ResourceType
	 */
	public ResourceType getType();
	
	/**
	 * Sets the type of the resource
	 * @param type
	 * @see ResourceType
	 */
	public void setType(ResourceType type);

	/**
	 * A resource is always associated with a scheduling problem, an instance of the class Schedule.
	 * @return an associated schedule
	 */
	public Schedule getSchedule();

	/**
	 * 
	 * @return an array of constrained integer variables that represent capacities 
	 * of this resource at every time unit of the associated schedule
	 */
	public Var[] getCapacities();

	/**
	 * Sets resource maximal capacity at the specified moment of time.
	 * 
	 * @param time
	 *            Time moment for new capacity
	 * @param capacity
	 *            New capacity
	 * @throws Exception
	 */
	public void setCapacityMax(int time, int capacity) throws Exception;
	
	/**
	 * Creates and returns a constraint that when posted 
	 * sets resource maximal capacity at the specified moment of time.
	 * 
	 * @param time
	 *            Time moment for new capacity
	 * @param capacity
	 *            New capacity
	 * @throws Exception when time is outside the schedule horizon
	 */
	public Constraint capacityMaxConstraint(int time, int capacity);

	/**
	 * Sets resource minimal capacity at the specified moment of time.
	 * 
	 * @param time
	 *            Time moment for new capacity
	 * @param capacity
	 *            New capacity
	 */
	public void setCapacityMin(int time, int capacity) throws Exception;
	
	/**
	 * Creates and returns a constraint that when posted 
	 * sets resource minimal capacity at the specified moment of time.
	 * 
	 * @param time
	 *            Time moment for new capacity
	 * @param capacity
	 *            New capacity
	 * @throws Exception when time is outside the schedule horizon
	 */
	public Constraint capacityMinConstraint(int time, int capacity);
	
	/**
	 * Sets resource maximal capacity at the specified interval of time
	 * [time1;time2)
	 * 
	 * @param time1
	 *            A start time
	 * @param time2
	 *            An end time
	 * @param capacity
	 *            New capacity
	 * @throws an exception
	 */
	public void setCapacityMax(int time1, int time2, int capacity)
			throws Exception;
	
	/**
	 * Creates and returns an array of constraints that when all posted 
	 * set resource maximal capacity at the specified interval of time
	 * [time1;time2)
	 * 
	 * @param time1
	 *            A start time
	 * @param time2
	 *            An end time
	 * @param capacity
	 *            New capacity
	 * @throws an RuntimeException when time2 < time1
	 */
	public Constraint[] capacityMaxConstraints(int time1, int time2, int capacity);
	
	/**
	 * Sets resource minimal capacity at the specified interval of time [time1;
	 * time2)
	 * 
	 * @param time1
	 *            A start time
	 * @param time2
	 *            An end time
	 * @param capacity
	 *            New capacity
	 * @throws an exception
	 */
	public void setCapacityMin(int time1, int time2, int capacity)
			throws Exception;
	
	/**
	 * Creates and returns an array of constraints that when all posted 
	 * set resource maximal capacity at the specified interval of time
	 * [time1;time2)
	 * 
	 * @param time1
	 *            A start time
	 * @param time2
	 *            An end time
	 * @param capacity
	 *            New capacity
	 * @throws an exception when time2 < time1
	 */
	public Constraint[] capacityMinConstraints(int time1, int time2, int capacity);
	
	/**
     * Returns resource maximal capacity at any moment of time (theoretical capacity)
     * 
     * @return theoretical capacity
     */
    public int getCapacityMax();
	
	/**
	 * Returns resource maximal capacity at the specified moment of time
	 * 
	 * @param time
	 *            Time for capacity query
	 * @return Capacity
	 */
	public int getCapacityMax(int time);

	/**
	 * Returns resource minimal capacity at the specified moment of time
	 * 
	 * @param time
	 *            Time for capacity query
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
	
	/**
	 * Adds a activity-resource constraint to the problem
	 * @param rc
	 */
	public void add(ConstraintActivityResource rc);
	
	/**
	 * This method associates a certain cost with  resource during certain time
	 * period [x1,x2) and with certain utilization of this resource [y1;y2),
	 * For example, electricity as a resource may have different costs during certain
	 * time periods of the day (horizontal changes). This cost may wary based on consumed electricity,
	 * e.g. an initial amount may cost less (probably it was produced by wind), 
	 * but the more you consume the more you may have to pay. It is also known is "cost per volume"
	 * and presented on a vertical scale. This method always associates with an area {x1,x2,y1,y2} one
	 * constrained integer variable that represents actual utilization of resource
	 * within this area by all related activities. These utilization variables 
	 * can be used to post additional constraints on the resource and activities.
	 * @param x1 int
	 * @param x2 int
	 * @param y1 int
	 * @param y2 int
	 * @param cost int
	 * @return utilization variable Var
	 */
	public Var setCost(int x1, int x2, int y1, int y2, int cost);
	
//	/**
//	 * Saves capacity variables for this resource as they were found in this solution
//	 * @param solution
//	 */
//	public void save(Solution solution);
	
	/**
	 * Sets a cost of the resource
	 * @param cost
	 */
	public void setCost(int cost);
	
	/**
	 * @return a cost of the resource
	 */
	public int getCost();
	


}
